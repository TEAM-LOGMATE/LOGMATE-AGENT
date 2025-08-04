package com.logmate.tailer;

import com.logmate.di.TailerComponentRegistryHolder;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.WatcherConfigHolder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TailerRunManager {

  private static Map<Integer, Thread> tailerThreadMap = new ConcurrentHashMap<>();

  public static void start(Integer thNum) {
    WatcherConfigHolder.get(thNum).ifPresentOrElse(config -> {
      TailerComponentRegistryHolder.create(thNum, config, AgentConfigHolder.get());
      Thread thread = new Thread(TailerComponentRegistryHolder.getTailer(thNum));
      tailerThreadMap.put(thNum, thread);
      thread.start();
      log.info("[TailerRunManager] Log tailer for thNum #{} started...", thNum);
    }, () -> log.warn("[TailerRunManager] Unknown thNum {}. Ignoring tailer start.", thNum));

  }

  public static void restart(Integer thNum) {
    Thread oldThread = tailerThreadMap.get(thNum);
    if (oldThread == null) {
      log.warn("[TailerRunManager] Unknown thNum {}. Cannot restart tailer.", thNum);
      return;
    }

    oldThread.interrupt();
    WatcherConfigHolder.get(thNum).ifPresentOrElse(config -> {
      TailerComponentRegistryHolder.remake(thNum, config, AgentConfigHolder.get());
      Thread thread = new Thread(TailerComponentRegistryHolder.getTailer(thNum));
      tailerThreadMap.put(thNum, thread);
      thread.start();
      log.info("[TailerRunManager] Log tailer for thNum {} restarted...", thNum);
    }, () -> log.warn("[TailerRunManager] Unknown thNum {}. Cannot restart tailer.", thNum));
  }

  public static void restartAll() {
    log.info("[TailerRunManager] Restarting all tailers...");
    for (Map.Entry<Integer, Thread> entry : tailerThreadMap.entrySet()) {
      restart(entry.getKey());
    }
  }

  public static void stop(Integer removedThNum) {
    if (tailerThreadMap.get(removedThNum) == null) {
      log.warn("[TailerRunManager] Unknown thNum {}. Cannot stop tailer.", removedThNum);
      return;
    }

    tailerThreadMap.get(removedThNum).interrupt();
    tailerThreadMap.remove(removedThNum);
    log.info("[TailerRunManager] Log tailer for thNum {} stopped...", removedThNum);
  }
}
