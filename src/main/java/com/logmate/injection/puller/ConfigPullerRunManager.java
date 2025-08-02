package com.logmate.injection.puller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigPullerRunManager {

  private static Thread pullerThread;

  public static void start() {
    pullerThread = new Thread(new ConfigPuller());
    pullerThread.start();
    log.info("configuration puller started...");
  }

  public static void stop() {
    if (pullerThread != null) {
      pullerThread.interrupt();
      pullerThread = null;
      log.info("configuration puller stopped.");
    }
  }

  public static void restart() {
    stop();
    start();
  }
}
