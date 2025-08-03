package com.logmate.component;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.logmate.injection.config.AgentConfig;
import com.logmate.injection.config.WatcherConfig;
import com.logmate.tailer.LogTailer;
import com.logmate.tailer.exporter.LogExporter;
import com.logmate.tailer.exporter.impl.HttpLogExporter;
import com.logmate.tailer.filter.LogFilter;
import com.logmate.tailer.filter.impl.NonLogFilter;
import com.logmate.tailer.impl.FileLogTailer;
import com.logmate.tailer.listener.LogEventListener;
import com.logmate.tailer.listener.impl.DefaultLogEventListener;
import com.logmate.tailer.parser.LogParser;
import com.logmate.tailer.parser.impl.spring.SpringBootLogParser;

public class ComponentRegistry extends AbstractModule {

  private final WatcherConfig watcherConfig;
  private final AgentConfig agentconfig;

  public ComponentRegistry(WatcherConfig watcherConfig, AgentConfig agentconfig) {
    this.watcherConfig = watcherConfig;
    this.agentconfig = agentconfig;
  }

  @Override
  protected void configure() {
    //todo: parser type 에 대해 instance 추가
    switch (watcherConfig.getParser().getType()) {
      case "springboot":
        bind(LogParser.class).toInstance(new SpringBootLogParser(watcherConfig.getParser()));
        break;
      case "json":
        bind(LogParser.class).toInstance(new SpringBootLogParser(watcherConfig.getParser()));
      default:
        bind(LogParser.class).toInstance(new SpringBootLogParser(watcherConfig.getParser()));
    }
    bind(LogFilter.class).to(NonLogFilter.class);
    bind(LogExporter.class).toInstance(
        new HttpLogExporter(watcherConfig.getExporter(), agentconfig));
    bind(LogEventListener.class).to(DefaultLogEventListener.class);
  }

  @Provides
  public LogTailer provideLogTailer(LogEventListener listener) {
    //todo: multi thread
    return new FileLogTailer(
        watcherConfig.getTailer(),
        listener,
        1
    );
  }
}
