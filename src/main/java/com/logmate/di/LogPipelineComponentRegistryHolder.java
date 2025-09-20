package com.logmate.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.pipeline.LogPipelineConfig;
import com.logmate.tailer.LogTailer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogPipelineComponentRegistryHolder {

  private static final Map<Integer, Injector> injectorMap = new ConcurrentHashMap<>();

  public static void create(int thNum, LogPipelineConfig logPipelineConfig, AgentConfig agentConfig) {
    injectorMap.put(thNum, Guice.createInjector(new LogPipelineComponentRegistry(logPipelineConfig, agentConfig)));
  }

  public static void remake(int thNum, LogPipelineConfig logPipelineConfig, AgentConfig agentConfig) {
    injectorMap.put(thNum, Guice.createInjector(new LogPipelineComponentRegistry(logPipelineConfig, agentConfig)));
  }

  public static LogTailer getTailer(int thNum) {
    Injector injector = injectorMap.get(thNum);
    if (injector == null) {
      return null;
    }
    return injector.getInstance(LogTailer.class);
  }
}
