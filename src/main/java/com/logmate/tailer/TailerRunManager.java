package com.logmate.tailer;

import com.logmate.component.ComponentRegistryHolder;
import com.logmate.injection.config.WatcherConfigHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailerRunManager {

  private static final Logger log = LoggerFactory.getLogger(TailerRunManager.class);

  private static Thread tailerThread;

  public static void start() {
    ComponentRegistryHolder.create(WatcherConfigHolder.get());
    tailerThread = new Thread(ComponentRegistryHolder.getTailer());
    tailerThread.start();
    log.info("log tailer started...");
  }

  public static void restart() {

    if (tailerThread != null) {
      tailerThread.interrupt();
    }

    ComponentRegistryHolder.remake(WatcherConfigHolder.get());
    tailerThread = new Thread(ComponentRegistryHolder.getTailer());
    tailerThread.start();
    log.info("log tailer restarted...");
  }
}
