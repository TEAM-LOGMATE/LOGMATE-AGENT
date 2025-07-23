package com.logmate.component;


import com.logmate.injection.config.WatcherConfig;
import com.logmate.tailer.exporter.LogExporter;
import com.logmate.tailer.exporter.impl.HttpLogExporter;
import com.logmate.tailer.filter.LogFilter;
import com.logmate.tailer.filter.impl.NonLogFilter;
import java.io.File;
import java.util.Objects;
import com.logmate.tailer.parser.LogParser;
import com.logmate.tailer.parser.impl.spring.SpringBootLogParser;
import com.logmate.tailer.listener.LogEventListener;
import com.logmate.tailer.listener.impl.DefaultLogEventListener;
import com.logmate.tailer.LogTailer;
import com.logmate.tailer.impl.FileLogTailer;

/**
 * System 에서 사용하는 컴포넌트들을 생성하고, Dependency 를 연결한다.
 *
 */
public class ComponentRegistry {

  private String logFilePath;
  private String logPushURL;
  private LogTailer logTailer;
  private LogEventListener logEventListener;
  private LogParser logParser;
  private LogFilter logFilter;
  private LogExporter logExporter;

  public ComponentRegistry(WatcherConfig watcherConfig) {
    this.logFilePath = watcherConfig.getLogFilePath();
    this.logPushURL = watcherConfig.getLogPushURL();
  }

  public LogTailer getLogTailer() {
    if (Objects.isNull(logTailer)) {
      logTailer = new FileLogTailer(new File(logFilePath), getLogEventListener());
      return logTailer;
    }
    return logTailer;
  }

  public LogEventListener getLogEventListener() {
    if (Objects.isNull(logEventListener)) {
      logEventListener = new DefaultLogEventListener(
          getLogParser(),
          getLogFilter(),
          getLogExporter()
      );
      return logEventListener;
    }
    return logEventListener;
  }

  public LogParser getLogParser() {
    if (Objects.isNull(logParser)) {
      logParser = new SpringBootLogParser();
      return logParser;
    }
    return logParser;
  }

  public LogFilter getLogFilter() {
    if (Objects.isNull(logFilter)) {
      logFilter = new NonLogFilter();
      return logFilter;
    }
    return logFilter;
  }

  public LogExporter getLogExporter() {
    if (Objects.isNull(logExporter)) {
      //logExporter = new ConsoleLogExporter();
      logExporter = new HttpLogExporter(logPushURL);
      return logExporter;
    }
    return logExporter;
  }
}
