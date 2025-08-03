package com.logmate.injection.config.util;


import com.logmate.injection.config.WatcherConfig;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WatcherConfigHolder {

  private static final Map<Integer, WatcherConfig> watcherConfigMap = createDefault();

  public static Optional<WatcherConfig> get(Integer thNum) {
    if (watcherConfigMap.get(thNum) == null) {
      return Optional.empty();
    }
    return Optional.of(watcherConfigMap.get(thNum));
  }

  private static Map<Integer, WatcherConfig> createDefault() {
    WatcherConfig load = YamlConfigLoader.loadTailerConfig();
    Map<Integer, WatcherConfig> result = new ConcurrentHashMap<>();
    result.put(load.getThNum(), load);
    log.info("Default config loaded.{}", load);
    return result;
  }

  public static boolean update(WatcherConfig watcherConfig, Integer thNum) {
    try {
      ConfigValidator.validate(watcherConfig);
    } catch (IllegalArgumentException e) {
      log.error("Invalid config: {}", e.getMessage());
      return false;
    }

    watcherConfigMap.put(thNum, watcherConfig);
    return true;
  }
}
