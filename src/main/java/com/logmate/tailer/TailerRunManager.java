package com.logmate.tailer;

import com.logmate.component.ComponentRegistryHolder;
import com.logmate.injection.config.util.AgentConfigHolder;
import com.logmate.injection.config.util.WatcherConfigHolder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TailerRunManager {
  private static Thread tailerThread;

  public static void start() {
    //todo: 멀티 thread방식 변경
    ComponentRegistryHolder.create(1, WatcherConfigHolder.get(1).get(), AgentConfigHolder.get());
    tailerThread = new Thread(ComponentRegistryHolder.getTailer(1));
    tailerThread.start();
    log.info("log tailer started...");
  }

  public static void restart() {

    if (tailerThread != null) {
      tailerThread.interrupt();
    }
    //todo: 멀티 thread방식 변경
    ComponentRegistryHolder.remake(1, WatcherConfigHolder.get(1).get(), AgentConfigHolder.get());
    tailerThread = new Thread(ComponentRegistryHolder.getTailer(1));
    tailerThread.start();
    log.info("log tailer restarted...");
  }
}
