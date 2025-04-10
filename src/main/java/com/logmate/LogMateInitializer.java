package com.logmate;

import com.logmate.component.ComponentRegistry;

public class LogMateInitializer {

  private final String filePath;
  private final String logPushURL;
  public LogMateInitializer(String filePath, String logPushURL) {
    this.filePath = filePath;
    this.logPushURL = logPushURL;
    run();
  }
  private void run() {
    ComponentRegistry componentRegistry = new ComponentRegistry(filePath, logPushURL);
    Thread tailerThread = new Thread(componentRegistry.getLogTailer());
    tailerThread.start();
    System.out.println("log-mate-library is running");
    System.out.println("Log tailer started...");
  }
}
