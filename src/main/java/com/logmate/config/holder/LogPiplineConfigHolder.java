package com.logmate.config.holder;


import com.logmate.config.data.pipeline.LogPipelineConfig;
import com.logmate.config.validator.ConfigValidator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogPiplineConfigHolder {

  private static final Map<Integer, LogPipelineConfig> watcherConfigMap = new ConcurrentHashMap<>();

  public static Optional<LogPipelineConfig> get(Integer thNum) {
    if (watcherConfigMap.get(thNum) == null) {
      return Optional.empty();
    }
    return Optional.of(watcherConfigMap.get(thNum));
  }

  public static boolean update(LogPipelineConfig logPipelineConfig, Integer thNum) {
    try {
      ConfigValidator.validate(logPipelineConfig);
    } catch (IllegalArgumentException e) {
      log.error("[LogPiplineConfigHolder] Invalid watcher config: {}", e.getMessage());
      return false;
    }

    watcherConfigMap.put(thNum, logPipelineConfig);
    log.debug("[LogPiplineConfigHolder] Watcher config updated.{}", logPipelineConfig);
    return true;
  }

  public static boolean put(LogPipelineConfig logPipelineConfig, Integer thNum) {
    log.debug("[LogPiplineConfigHolder] Watcher config added.{}", logPipelineConfig);
    return update(logPipelineConfig, thNum);
  }

  public static Set<Integer> getAllThreadNums() {
    return watcherConfigMap.keySet();
  }

  public static void remove(Integer removedThNum) {
    if (watcherConfigMap.get(removedThNum) != null) {
      log.debug("[LogPiplineConfigHolder] Watcher config removed. #{}", removedThNum);
      watcherConfigMap.remove(removedThNum);
    }
  }
}
