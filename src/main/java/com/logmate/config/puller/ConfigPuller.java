package com.logmate.config.puller;

import com.logmate.config.AgentConfig;
import com.logmate.config.PullerConfig;
import com.logmate.config.LogPiplineConfig;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.PullerConfigHolder;
import com.logmate.config.holder.LogPiplineConfigHolder;
import com.logmate.config.puller.dto.AuthenticationRequestDTO;
import com.logmate.config.puller.dto.ConfigDTO;
import com.logmate.config.puller.dto.TokenDTO;
import com.logmate.tailer.TailerRunManager;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class ConfigPuller implements Runnable {

  private final ConfigPullerClient client = new ConfigPullerClient();
  private AgentConfig agentConfig = AgentConfigHolder.get();
  private PullerConfig pullerConfig = PullerConfigHolder.get();
  private volatile boolean running = true;
  private String etag = String.valueOf(UUID.randomUUID());

  @Override
  public void run() {
    log.info("[ConfigPuller] Starting Agent authentication...");
    // 1. 초기 Agent ID 로 인증 시도
    agentAuthentication().ifPresentOrElse(
        token -> {
          // 인증 성공 시 Token Set
          log.info("[ConfigPuller] Agent authentication succeeded.");
          agentConfig.setAccessToken(token.getAccessToken());
          AgentConfigHolder.update(agentConfig);
          resetConfigs();
        },
        // 실패 시 puller stop (Agent Stop)
        () -> {
          stop();
          log.warn("[ConfigPuller] Failed to authenticate. Shutting down...");
        }
    );
    // 2. 주기적으로 Config 서버로부터 설정 Pull
    while (running) {
      String baseUrl = pullerConfig.getPullURL() + "/config";
      /*String query = String.format(
          "eTag=%s",
          URLEncoder.encode(etag, StandardCharsets.UTF_8)
      );
      String requestURL = baseUrl + "?" + query;


       */
      // HTTPs 요청
      Optional<ConfigDTO> response = client.configPull(baseUrl, agentConfig.getAccessToken());

      // Config 에 변경점이 있음
      if (response.isPresent()) {
        log.info("[ConfigPuller] Received new configuration update.");
        configsAndEtagUpdate(response.get());
      } else {
        // Config에 변경 없음
        log.debug("[ConfigPuller] No config changes. ETag={}, continuing...", etag);
      }

      try {
        Thread.sleep(pullerConfig.getIntervalSec() * 1000L);
      } catch (InterruptedException e) {
        log.warn("[ConfigPuller] ConfigPuller thread interrupted. Shutting down...");
        log.warn("[ConfigPuller] Exception: {}", e.getMessage());
        stop();
      }
    }
  }

  /**
   * Agent ID 기반 인증 요청을 보내고, access token을 응답받는다.
   */
  private Optional<TokenDTO> agentAuthentication() {
    String baseUrl = pullerConfig.getPullURL() + "/auth";
    AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO(
        agentConfig.getAgentId());

    return client.authenticationRequest(baseUrl, requestDTO);
  }

  /**
   * Config 변경 응답이 있을 경우, ETag 비교를 통해 필요한 업데이트 및 재시작 수행.
   */
  private void configsAndEtagUpdate(ConfigDTO config) {
    boolean shouldAllRestart = false;

    AgentConfig responseAgentConfig = config.getAgentConfig();
    PullerConfig responsePullerConfig = config.getPullerConfig();

    if (!responseAgentConfig.getEtag().equals(agentConfig.getEtag())) {
      log.info("[ConfigPuller] AgentConfig changed. Restart required.");
      if (AgentConfigHolder.update(responseAgentConfig)) {
        shouldAllRestart = true;
      }
    }

    if (!responsePullerConfig.getEtag().equals(pullerConfig.getEtag())) {
      if (PullerConfigHolder.update(responsePullerConfig)) {
        log.info("[ConfigPuller] PullerConfig changed.");
      }
    }

    List<LogPiplineConfig> responseLogPiplineConfigs = config.getLogPiplineConfigs();

    removeMissingTailer(responseLogPiplineConfigs);

    Set<Integer> needRestartThreadNum = new HashSet<>();
    for (LogPiplineConfig responseLogPiplineConfig : responseLogPiplineConfigs) {
      Optional<LogPiplineConfig> opWatcherConfig = LogPiplineConfigHolder.get(
          responseLogPiplineConfig.getThNum());

      if (opWatcherConfig.isPresent()) {
        LogPiplineConfig logPiplineConfig = opWatcherConfig.get();
        if (responseLogPiplineConfig.getEtag().equals(logPiplineConfig.getEtag())) {
          continue;
        }

        if (LogPiplineConfigHolder.update(responseLogPiplineConfig, logPiplineConfig.getThNum())) {
          log.info("[ConfigPuller] WatcherConfig #{} changed. Restart required.",
              logPiplineConfig.getThNum());
          needRestartThreadNum.add(logPiplineConfig.getThNum());
        }
      } else {
        // 신규 WatcherConfig 등록 + Tailer 시작
        boolean inserted = LogPiplineConfigHolder.put(responseLogPiplineConfig,
            responseLogPiplineConfig.getThNum());
        if (inserted) {
          log.info("[ConfigPuller] New WatcherConfig #{} added. Starting new tailer.",
              responseLogPiplineConfig.getThNum());
          TailerRunManager.start(responseLogPiplineConfig.getThNum());
        } else {
          log.error("[ConfigPuller] Failed to add new WatcherConfig #{}",
              responseLogPiplineConfig.getThNum());
        }
      }
    }

    // ETag 업데이트 및 Tailer 재시작
    if (shouldAllRestart) {
      log.info("[ConfigPuller] Restarting all tailers due to AgentConfig update.");
      TailerRunManager.restartAll();
    } else {
      for (Integer thNum : needRestartThreadNum) {
        TailerRunManager.restart(thNum);
      }
    }
    etag = config.getEtag();
    resetConfigs();
  }

  private static void removeMissingTailer(List<LogPiplineConfig> responseLogPiplineConfigs) {
    Set<Integer> receivedThreadNums = responseLogPiplineConfigs.stream()
        .map(LogPiplineConfig::getThNum)
        .collect(Collectors.toSet());

    Set<Integer> existingThreadNums = LogPiplineConfigHolder.getAllThreadNums(); // 내부 Map keySet
    Set<Integer> removedThreadNums = new HashSet<>(existingThreadNums);
    removedThreadNums.removeAll(receivedThreadNums); // 서버에서 빠진 것만 남음 (집합간의 차)

    for (Integer removedThNum : removedThreadNums) {
      log.info("[ConfigPuller] WatcherConfig #{} removed from server. Stopping tailer.",
          removedThNum);
      TailerRunManager.stop(removedThNum);
      LogPiplineConfigHolder.remove(removedThNum);
    }
  }

  /**
   * Pull 루프 종료
   */
  public void stop() {
    running = false;
  }

  /**
   * 최신 Holder 상태로 로컬 캐싱된 Config 갱신
   */
  private void resetConfigs() {
    agentConfig = AgentConfigHolder.get();
    pullerConfig = PullerConfigHolder.get();
  }
}
