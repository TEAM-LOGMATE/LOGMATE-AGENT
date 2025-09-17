package com.logmate.config.puller;

import com.logmate.config.AgentConfig;
import com.logmate.config.LogPiplineConfig;
import com.logmate.config.PullerConfig;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.LogPiplineConfigHolder;
import com.logmate.config.holder.PullerConfigHolder;
import com.logmate.config.puller.dto.ConfigDTO;
import com.logmate.tailer.TailerRunManager;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ConfigUpdater {

  private boolean applyAgentConfig(AgentConfig newCfg) {
    AgentConfig current = AgentConfigHolder.get();

    if (!newCfg.getEtag().equals(current.getEtag())) {
      log.info("[ConfigUpdater] AgentConfig changed. Restart required.");
      return AgentConfigHolder.update(newCfg);
    }
    return false;
  }

  private void applyPullerConfig(PullerConfig newCfg) {
    PullerConfig current = PullerConfigHolder.get();

    if (!newCfg.getEtag().equals(current.getEtag())) {
      if (PullerConfigHolder.update(newCfg)) {
        log.info("[ConfigUpdater] PullerConfig changed.");
      }
    }
  }
  private Set<Integer> applyLogPipelineConfigs(List<LogPiplineConfig> newCfgs) {
    removeMissingTailer(newCfgs);
    Set<Integer> needRestartThreadNum = new HashSet<>();
    for (LogPiplineConfig responseLogPiplineConfig : newCfgs) {
      Optional<LogPiplineConfig> opWatcherConfig = LogPiplineConfigHolder.get(
          responseLogPiplineConfig.getThNum());

      if (opWatcherConfig.isPresent()) {
        LogPiplineConfig logPiplineConfig = opWatcherConfig.get();
        if (responseLogPiplineConfig.getEtag().equals(logPiplineConfig.getEtag())) {
          continue;
        }

        if (LogPiplineConfigHolder.update(responseLogPiplineConfig, logPiplineConfig.getThNum())) {
          log.info("[ConfigUpdater] WatcherConfig #{} changed. Restart required.",
              logPiplineConfig.getThNum());
          needRestartThreadNum.add(logPiplineConfig.getThNum());
        }
      } else {
        // 신규 WatcherConfig 등록 + Tailer 시작
        boolean inserted = LogPiplineConfigHolder.put(responseLogPiplineConfig,
            responseLogPiplineConfig.getThNum());
        if (inserted) {
          log.info("[ConfigUpdater] New WatcherConfig #{} added. Starting new tailer.",
              responseLogPiplineConfig.getThNum());
          TailerRunManager.start(responseLogPiplineConfig.getThNum());
        } else {
          log.error("[ConfigUpdater] Failed to add new WatcherConfig #{}",
              responseLogPiplineConfig.getThNum());
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

    Set<Integer> needRestartThreadNum = applyLogPipelineConfigs(config.getLogPiplineConfigs());

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

  private static void removeMissingTailer(List<LogPiplineConfig> responseLogPiplineConfigs) {
    Set<Integer> receivedThreadNums = responseLogPiplineConfigs.stream()
        .map(LogPiplineConfig::getThNum)
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
