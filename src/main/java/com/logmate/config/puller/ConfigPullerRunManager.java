package com.logmate.config.puller;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ConfigPullerRunManager {

  private Thread pullerThread;

  public void start() {
    pullerThread = new Thread(
        new ConfigPuller(new ConfigPullerClient(), new ConfigUpdater())
    );
    pullerThread.start();
    log.info("[ConfigPullerRunManager] configuration puller started...");
  }
}
