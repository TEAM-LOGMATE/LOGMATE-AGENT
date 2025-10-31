package com.logmate.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.pipeline.LogPipelineConfig;
import com.logmate.processor.exporter.impl.ConsoleLogExporter;
import com.logmate.processor.fallback.FallbackStorage;
import com.logmate.processor.fallback.impl.FileFallbackStorage;
import com.logmate.processor.parser.LogType;
import com.logmate.tailer.LogTailer;
import com.logmate.processor.exporter.LogExporter;
import com.logmate.processor.exporter.impl.HttpLogExporter;
import com.logmate.processor.filter.LogFilter;
import com.logmate.tailer.impl.FileLogTailer;
import com.logmate.processor.listener.LogEventListener;
import com.logmate.processor.listener.impl.DefaultLogEventListener;
import com.logmate.processor.merger.MultilineProcessor;
import com.logmate.processor.parser.LogParser;
import org.slf4j.MDC;

public class LogPipelineComponentRegistry extends AbstractModule {

  private final LogPipelineConfig logPipelineConfig;
  private final AgentConfig agentconfig;

  public LogPipelineComponentRegistry(LogPipelineConfig logPipelineConfig, AgentConfig agentconfig) {
    this.logPipelineConfig = logPipelineConfig;
    this.agentconfig = agentconfig;
  }

  @Override
  protected void configure() {
    LogType logType = LogType.valueOf(logPipelineConfig.getParser().getType().toUpperCase());
    bind(LogParser.class).toInstance(
        logType.createParser(logPipelineConfig.getParser())
    );
    bind(LogFilter.class).toInstance(
        logType.createFilter(logPipelineConfig.getFilter())
    );
    bind(LogExporter.class).toInstance(
        //new ConsoleLogExporter()
        new HttpLogExporter(logPipelineConfig.getExporter(), agentconfig)
    );
    bind(FallbackStorage.class).toInstance(
        new FileFallbackStorage(logPipelineConfig.getFallbackStorage())
    );
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
    return new FileLogTailer(
        logPipelineConfig.getTailer(),
        listener,
        logPipelineConfig.getThNum()
    );
  }

  @Provides
  public LogEventListener provideLogEventListener(LogExporter exporter, LogFilter filter,
      LogParser parser, MultilineProcessor multilineProcessor, FallbackStorage fallbackStorage) {
    return new DefaultLogEventListener(parser, filter, exporter, multilineProcessor, fallbackStorage);
  }
}
