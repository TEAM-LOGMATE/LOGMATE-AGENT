package com.logmate;

import com.logmate.component.ComponentRegistry;

public class LogMateInitializer {

  private final String filePath;
  public LogMateInitializer(String filePath) {
    this.filePath = filePath;
    run();
  }
  private void run() {
    ComponentRegistry componentRegistry = new ComponentRegistry(filePath);
    Thread tailerThread = new Thread(componentRegistry.getLogTailer());
    tailerThread.start();
    System.out.println("log-mate-library is running");
    System.out.println("Log tailer started...");
  }
}
