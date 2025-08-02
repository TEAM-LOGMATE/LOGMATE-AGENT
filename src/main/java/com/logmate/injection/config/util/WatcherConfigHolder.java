package com.logmate.injection.config.util;


import com.logmate.injection.config.WatcherConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WatcherConfigHolder {

  private static WatcherConfig watcherConfig = createDefault();

  public static WatcherConfig get() {
    return watcherConfig;
  }

  private static WatcherConfig createDefault() {
    WatcherConfig load = YamlConfigLoader.load("default-config.yml");
    log.info("Default config loaded.{}", load);
    return load;
  }

  public static boolean update(WatcherConfig watcherConfig) {
    try {
      WatcherConfigValidator.validate(watcherConfig);
    } catch (IllegalArgumentException e) {
      log.error("Invalid config: {}", e.getMessage());
      return false;
    }

    WatcherConfigHolder.watcherConfig = watcherConfig;
    return true;
  }
}
