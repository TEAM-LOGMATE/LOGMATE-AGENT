package com.logmate.config.puller;

import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.pipeline.LogPipelineConfig;
import com.logmate.config.data.PullerConfig;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.LogPiplineConfigHolder;
import com.logmate.config.holder.PullerConfigHolder;
import com.logmate.config.puller.dto.ConfigDTO;
import com.logmate.config.puller.dto.ConfigDTO.AgentConfigDto;
import com.logmate.config.puller.dto.ConfigDTO.LogPipelineConfigDto;
import com.logmate.config.puller.dto.ConfigDTO.PullerConfigDto;
import com.logmate.tailer.TailerRunManager;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ConfigUpdater {
  private final ConfigConverter converter;

  private boolean applyAgentConfig(AgentConfigDto newCfgDto) {
    AgentConfig current = AgentConfigHolder.get();

    if (!newCfgDto.getEtag().equals(current.getEtag())) {
      log.info("[ConfigUpdater] AgentConfig changed. Restart required.");
      newCfgDto.setAccessToken(current.getAccessToken()); //todo: dto 에서 accessToken 제외
      return AgentConfigHolder.update(converter.convert(newCfgDto));
    }
    return false;
  }

  private void applyPullerConfig(PullerConfigDto newCfgDto) {
    PullerConfig current = PullerConfigHolder.get();
    //pull URL 유지
    newCfgDto.setPullURL(current.getPullURL());
    if (!newCfgDto.getEtag().equals(current.getEtag())) {
      if (PullerConfigHolder.update(converter.convert(newCfgDto))) {
        log.info("[ConfigUpdater] PullerConfig changed.");
      }
    }
  }
  private Set<Integer> applyLogPipelineConfigs(List<LogPipelineConfigDto> newCfgsDto) {
    List<LogPipelineConfig> newCfgs = converter.convert(newCfgsDto);

    removeMissingTailer(newCfgs);
    Set<Integer> needRestartThreadNum = new HashSet<>();
    for (LogPipelineConfig responseLogPipelineConfig : newCfgs) {
      Optional<LogPipelineConfig> opLogPipelineConfig = LogPiplineConfigHolder.get(
          responseLogPipelineConfig.getThNum());

      if (opLogPipelineConfig.isPresent()) {
        LogPipelineConfig logPipelineConfig = opLogPipelineConfig.get();
        if (responseLogPipelineConfig.getEtag().equals(logPipelineConfig.getEtag())) {
          continue;
        }

        if (LogPiplineConfigHolder.update(responseLogPipelineConfig, logPipelineConfig.getThNum())) {
          log.info("[ConfigUpdater] PipelineConfig #{} changed. Restart required.",
              logPipelineConfig.getThNum());
          needRestartThreadNum.add(logPipelineConfig.getThNum());
        }
      } else {
        // 신규 WatcherConfig 등록 + Tailer 시작
        boolean inserted = LogPiplineConfigHolder.put(responseLogPipelineConfig,
            responseLogPipelineConfig.getThNum());
        if (inserted) {
          log.info("[ConfigUpdater] New WatcherConfig #{} added. Starting new tailer.",
              responseLogPipelineConfig.getThNum());
          TailerRunManager.start(responseLogPipelineConfig.getThNum());
        } else {
          log.error("[ConfigUpdater] Failed to add new WatcherConfig #{}",
              responseLogPipelineConfig.getThNum());
        }
      }
    }
    return needRestartThreadNum;
  }
  /**
   * Config 변경 응답이 있을 경우, ETag 비교를 통해 필요한 업데이트 및 재시작 수행.
   */
  public void apply(ConfigDTO config) {
    boolean shouldAllRestart = false;

    if (applyAgentConfig(config.getAgentConfig())) {
      shouldAllRestart = true;
    }

    applyPullerConfig(config.getPullerConfig());

    Set<Integer> needRestartThreadNum = applyLogPipelineConfigs(config.getLogPipelineConfigs());

    // ETag 업데이트 및 Tailer 재시작
    if (shouldAllRestart) {
      log.info("[ConfigUpdater] Restarting all tailers due to AgentConfig update.");
      TailerRunManager.restartAll();
    } else {
      for (Integer thNum : needRestartThreadNum) {
        TailerRunManager.restart(thNum);
      }
    }
  }

  private static void removeMissingTailer(List<LogPipelineConfig> responseLogPipelineConfigs) {
    Set<Integer> receivedThreadNums = responseLogPipelineConfigs.stream()
        .map(LogPipelineConfig::getThNum)
        .collect(Collectors.toSet());

    Set<Integer> existingThreadNums = LogPiplineConfigHolder.getAllThreadNums(); // 내부 Map keySet
    Set<Integer> removedThreadNums = new HashSet<>(existingThreadNums);
    removedThreadNums.removeAll(receivedThreadNums); // 서버에서 빠진 것만 남음 (집합간의 차)

    for (Integer removedThNum : removedThreadNums) {
      log.info("[ConfigUpdater] WatcherConfig #{} removed from server. Stopping tailer.",
          removedThNum);
      TailerRunManager.stop(removedThNum);
      LogPiplineConfigHolder.remove(removedThNum);
    }
  }
}
