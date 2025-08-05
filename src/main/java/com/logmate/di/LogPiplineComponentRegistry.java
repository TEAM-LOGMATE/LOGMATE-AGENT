package com.logmate.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.logmate.config.AgentConfig;
import com.logmate.config.LogPiplineConfig;
import com.logmate.processor.exporter.impl.ConsoleLogExporter;
import com.logmate.processor.filter.impl.TomcatAccessLogFilter;
import com.logmate.processor.parser.impl.web.TomcatAccessLogParser;
import com.logmate.tailer.LogTailer;
import com.logmate.processor.exporter.LogExporter;
import com.logmate.processor.exporter.impl.HttpLogExporter;
import com.logmate.processor.filter.LogFilter;
import com.logmate.processor.filter.impl.SpringBootLogFilter;
import com.logmate.tailer.impl.FileLogTailer;
import com.logmate.processor.listener.LogEventListener;
import com.logmate.processor.listener.impl.DefaultLogEventListener;
import com.logmate.processor.merger.MultilineProcessor;
import com.logmate.processor.parser.LogParser;
import com.logmate.processor.parser.impl.spring.SpringBootLogParser;

public class LogPiplineComponentRegistry extends AbstractModule {

  private final LogPiplineConfig logPiplineConfig;
  private final AgentConfig agentconfig;

  public LogPiplineComponentRegistry(LogPiplineConfig logPiplineConfig, AgentConfig agentconfig) {
    this.logPiplineConfig = logPiplineConfig;
    this.agentconfig = agentconfig;
  }

  @Override
  protected void configure() {
    switch (logPiplineConfig.getParser().getType()) {
      case "springboot":
        bind(LogParser.class).toInstance(new SpringBootLogParser(logPiplineConfig.getParser()));
        bind(LogFilter.class).toInstance(new SpringBootLogFilter(logPiplineConfig.getFilter()));
        break;
      case "tomcat-access":
        bind(LogParser.class).toInstance(new TomcatAccessLogParser(logPiplineConfig.getParser()));
        bind(LogFilter.class).toInstance(new TomcatAccessLogFilter(logPiplineConfig.getFilter()));
        break;
      default:
        bind(LogParser.class).toInstance(new SpringBootLogParser(logPiplineConfig.getParser()));
        bind(LogFilter.class).toInstance(new SpringBootLogFilter(logPiplineConfig.getFilter()));
    }
    bind(LogExporter.class).toInstance(
        new ConsoleLogExporter());
        //new HttpLogExporter(logPiplineConfig.getExporter(), agentconfig));
  }

  @Provides
  public MultilineProcessor provideMultilineProcessor(LogParser parser) {
    return new MultilineProcessor(
        parser,
        logPiplineConfig.getMultiline()
    );
  }

  @Provides
  public LogTailer provideLogTailer(LogEventListener listener) {
    //todo: multi thread
    return new FileLogTailer(
        logPiplineConfig.getTailer(),
        listener,
        1
    );
  }

  @Provides
  public LogEventListener provideLogEventListener(LogExporter exporter, LogFilter filter,
      LogParser parser, MultilineProcessor multilineProcessor) {
    return new DefaultLogEventListener(parser, filter, exporter, multilineProcessor);
  }
}
