package com.logmate.component;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.logmate.injection.config.AgentConfig;
import com.logmate.injection.config.WatcherConfig;
import com.logmate.tailer.LogTailer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TailerComponentRegistryHolder {

  private static final Map<Integer, Injector> injectorMap = new ConcurrentHashMap<>();

  public static void create(int thNum, WatcherConfig watcherConfig, AgentConfig agentConfig) {
    injectorMap.put(thNum, Guice.createInjector(new TailerComponentRegistry(watcherConfig, agentConfig)));
  }

  public static void remake(int thNum, WatcherConfig watcherConfig, AgentConfig agentConfig) {
    injectorMap.put(thNum, Guice.createInjector(new TailerComponentRegistry(watcherConfig, agentConfig)));
  }

  public static LogTailer getTailer(int thNum) {
    Injector injector = injectorMap.get(thNum);
    if (injector == null) {
      return null;
    }
    return injector.getInstance(LogTailer.class);
  }
}
