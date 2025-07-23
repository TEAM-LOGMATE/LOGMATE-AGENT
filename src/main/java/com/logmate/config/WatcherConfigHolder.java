package com.logmate.config;

public class WatcherConfigHolder {

  private static WatcherConfig watcherConfig = WatcherConfig.getDefault();

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
    if (watcherConfig.getLogFilePath().isEmpty()) {
      return false;
    }

    if (watcherConfig.getLogPushURL().isEmpty()) {
      return false;
    }

    return true;
  }
}
