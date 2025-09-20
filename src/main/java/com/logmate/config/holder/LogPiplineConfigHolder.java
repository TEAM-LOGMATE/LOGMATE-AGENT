package com.logmate.config.holder;


import com.logmate.config.data.pipeline.LogPiplineConfig;
import com.logmate.config.validator.ConfigValidator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogPiplineConfigHolder {

  private static final Map<Integer, LogPiplineConfig> watcherConfigMap = new ConcurrentHashMap<>();

  public static Optional<LogPiplineConfig> get(Integer thNum) {
    if (watcherConfigMap.get(thNum) == null) {
      return Optional.empty();
    }
    return Optional.of(watcherConfigMap.get(thNum));
  }

  public static boolean update(LogPiplineConfig logPiplineConfig, Integer thNum) {
    try {
      ConfigValidator.validate(logPiplineConfig);
    } catch (IllegalArgumentException e) {
      log.error("[LogPiplineConfigHolder] Invalid watcher config: {}", e.getMessage());
      return false;
    }

    watcherConfigMap.put(thNum, logPiplineConfig);
    log.debug("[LogPiplineConfigHolder] Watcher config updated.{}", logPiplineConfig);
    return true;
  }

  public static boolean put(LogPiplineConfig logPiplineConfig, Integer thNum) {
    log.debug("[LogPiplineConfigHolder] Watcher config added.{}", logPiplineConfig);
    return update(logPiplineConfig, thNum);
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
