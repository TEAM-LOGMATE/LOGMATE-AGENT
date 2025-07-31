package com.logmate.injection.config;


import com.logmate.injection.config.loader.YamlConfigLoader;

public class WatcherConfigHolder {

  private static WatcherConfig watcherConfig = createDefault();

  public static WatcherConfig get() {
    return watcherConfig;
  }

  public static boolean update(WatcherConfig watcherConfig) {
    if (!isValidNewConfig(watcherConfig)) {
      return false;
    }

    WatcherConfigHolder.watcherConfig = watcherConfig;
    return true;
  }

  private static boolean isValidNewConfig(WatcherConfig watcherConfig) {

    return true;
  }

  private static WatcherConfig createDefault() {
    return YamlConfigLoader.load("default-config.yml");
  }
}
