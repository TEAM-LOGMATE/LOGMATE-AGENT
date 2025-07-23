package com.logmate.tailer;

import com.logmate.component.ComponentRegistry;
import com.logmate.component.ComponentRegistryHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailerRunManager {

  private static final Logger log = LoggerFactory.getLogger(TailerRunManager.class);

  private static Thread tailerThread;

  public static void start() {
    ComponentRegistry componentRegistry = ComponentRegistryHolder.get();
    tailerThread = new Thread(componentRegistry.getLogTailer());
    tailerThread.start();
    log.info("log tailer started...");
  }

  public static void restart() {

    if (tailerThread != null) {
      tailerThread.interrupt();
    }
    ComponentRegistryHolder.remake();
    ComponentRegistry componentRegistry = ComponentRegistryHolder.get();
    tailerThread = new Thread(componentRegistry.getLogTailer());
    tailerThread.start();
    log.info("log tailer restarted...");
  }
}
