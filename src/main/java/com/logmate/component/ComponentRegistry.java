package com.logmate.component;


import com.logmate.exporter.LogExporter;
import com.logmate.exporter.impl.HttpLogExporter;
import com.logmate.filter.LogFilter;
import com.logmate.filter.impl.NonLogFilter;
import java.io.File;
import java.util.Objects;
import com.logmate.parser.LogParser;
import com.logmate.parser.impl.spring.SpringBootLogParser;
import com.logmate.watcher.listener.LogEventListener;
import com.logmate.watcher.listener.impl.DefaultLogEventListener;
import com.logmate.watcher.tailer.LogTailer;
import com.logmate.watcher.tailer.impl.FileLogTailer;

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

  public ComponentRegistry(String logFilePath, String logPushURL) {
    this.logFilePath = logFilePath;
    this.logPushURL = logPushURL;
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
      logExporter = new HttpLogExporter(logPushURL);
      return logExporter;
    }
    return logExporter;
  }
}
