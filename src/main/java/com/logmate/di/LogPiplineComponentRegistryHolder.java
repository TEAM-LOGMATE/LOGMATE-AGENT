package com.logmate.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.logmate.config.AgentConfig;
import com.logmate.config.LogPiplineConfig;
import com.logmate.tailer.LogTailer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogPiplineComponentRegistryHolder {

  private static final Map<Integer, Injector> injectorMap = new ConcurrentHashMap<>();

  public static void create(int thNum, LogPiplineConfig logPiplineConfig, AgentConfig agentConfig) {
    injectorMap.put(thNum, Guice.createInjector(new LogPiplineComponentRegistry(logPiplineConfig, agentConfig)));
  }

  public static void remake(int thNum, LogPiplineConfig logPiplineConfig, AgentConfig agentConfig) {
    injectorMap.put(thNum, Guice.createInjector(new LogPiplineComponentRegistry(logPiplineConfig, agentConfig)));
  }

  public static LogTailer getTailer(int thNum) {
    Injector injector = injectorMap.get(thNum);
    if (injector == null) {
      return null;
    }
    return injector.getInstance(LogTailer.class);
  }
}
