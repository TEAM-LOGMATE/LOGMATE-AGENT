package com.logmate.config;

public class WatcherConfig {

  private String logFilePath;
  private String logPushURL;

  public WatcherConfig() {
  }

  public static WatcherConfig getDefault() {
    return new WatcherConfig("sample.log", "http://localhost:8080");
  }

  public WatcherConfig(String logFilePath, String logPushURL) {
    this.logFilePath = logFilePath;
    this.logPushURL = logPushURL;
  }

  public String getLogFilePath() {
    return logFilePath;
  }

  public String getLogPushURL() {
    return logPushURL;
  }
}
