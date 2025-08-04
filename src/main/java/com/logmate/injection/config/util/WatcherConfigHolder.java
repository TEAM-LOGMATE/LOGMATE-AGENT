package com.logmate.injection.config.util;


import com.logmate.injection.config.WatcherConfig;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WatcherConfigHolder {

  private static final Map<Integer, WatcherConfig> watcherConfigMap = new ConcurrentHashMap<>();

  public static Optional<WatcherConfig> get(Integer thNum) {
    if (watcherConfigMap.get(thNum) == null) {
      return Optional.empty();
    }
    return Optional.of(watcherConfigMap.get(thNum));
  }

  public static boolean update(WatcherConfig watcherConfig, Integer thNum) {
    try {
      ConfigValidator.validate(watcherConfig);
    } catch (IllegalArgumentException e) {
      log.error("[WatcherConfigHolder] Invalid watcher config: {}", e.getMessage());
      return false;
    }

    watcherConfigMap.put(thNum, watcherConfig);
    log.debug("[WatcherConfigHolder] Watcher config updated.{}", watcherConfig);
    return true;
  }

  public static boolean put(WatcherConfig watcherConfig, Integer thNum) {
    log.debug("[WatcherConfigHolder] Watcher config added.{}", watcherConfig);
    return update(watcherConfig, thNum);
  }

  public static Set<Integer> getAllThreadNums() {
    return watcherConfigMap.keySet();
  }

  public static void remove(Integer removedThNum) {
    if (watcherConfigMap.get(removedThNum) != null) {
      log.debug("[WatcherConfigHolder] Watcher config removed. #{}", removedThNum);
      watcherConfigMap.remove(removedThNum);
    }
  }
}
