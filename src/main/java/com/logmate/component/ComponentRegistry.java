package com.logmate.component;

import com.google.inject.AbstractModule;
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
import java.io.File;

public class ComponentRegistry extends AbstractModule {

  private final WatcherConfig config;

  public ComponentRegistry(WatcherConfig config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    bind(LogParser.class).to(SpringBootLogParser.class);
    bind(LogFilter.class).to(NonLogFilter.class);
    bind(LogExporter.class).toInstance(new HttpLogExporter(config.getExporter().getPushURL()));
    bind(LogEventListener.class).to(DefaultLogEventListener.class);
    bind(LogTailer.class).toInstance(
        new FileLogTailer(
            new File(config.getTailer().getFilePaths().get(0)),
            getProvider(LogEventListener.class).get()
        )
    );
  }
}
