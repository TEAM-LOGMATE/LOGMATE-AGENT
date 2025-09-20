package com.logmate.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.pipeline.LogPipelineConfig;
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

public class LogPipelineComponentRegistry extends AbstractModule {

  private final LogPipelineConfig logPipelineConfig;
  private final AgentConfig agentconfig;

  public LogPipelineComponentRegistry(LogPipelineConfig logPipelineConfig, AgentConfig agentconfig) {
    this.logPipelineConfig = logPipelineConfig;
    this.agentconfig = agentconfig;
  }

  @Override
  protected void configure() {
    switch (logPipelineConfig.getParser().getType()) {
      case "springboot":
        bind(LogParser.class).toInstance(new SpringBootLogParser(logPipelineConfig.getParser()));
        bind(LogFilter.class).toInstance(new SpringBootLogFilter(logPipelineConfig.getFilter()));
        break;
      case "tomcat":
        bind(LogParser.class).toInstance(new TomcatAccessLogParser(logPipelineConfig.getParser()));
        bind(LogFilter.class).toInstance(new TomcatAccessLogFilter(logPipelineConfig.getFilter()));
        break;
      default:
        bind(LogParser.class).toInstance(new SpringBootLogParser(logPipelineConfig.getParser()));
        bind(LogFilter.class).toInstance(new SpringBootLogFilter(logPipelineConfig.getFilter()));
    }
    bind(LogExporter.class).toInstance(
        //new ConsoleLogExporter());
        new HttpLogExporter(logPipelineConfig.getExporter(), agentconfig));
  }

  @Provides
  public MultilineProcessor provideMultilineProcessor(LogParser parser) {
    return new MultilineProcessor(
        parser,
        logPipelineConfig.getMultiline()
    );
  }

  @Provides
  public LogTailer provideLogTailer(LogEventListener listener) {
    //todo: multi thread
    return new FileLogTailer(
        logPipelineConfig.getTailer(),
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
