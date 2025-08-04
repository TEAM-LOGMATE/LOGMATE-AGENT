package com.logmate.config.puller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigPullerRunManager {

  private static Thread pullerThread;

  public static void start() {
    pullerThread = new Thread(new ConfigPuller());
    pullerThread.start();
    log.info("[ConfigPullerRunManager] configuration puller started...");
  }

  public static void stop() {
    if (pullerThread != null) {
      pullerThread.interrupt();
      pullerThread = null;
      log.info("[ConfigPullerRunManager] configuration puller stopped.");
    }
  }
}
