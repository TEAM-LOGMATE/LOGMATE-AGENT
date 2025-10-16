package com.logmate.processor.parser;

import com.logmate.config.data.pipeline.FilterConfig;
import com.logmate.config.data.pipeline.ParserConfig;
import com.logmate.processor.filter.LogFilter;
import com.logmate.processor.filter.impl.SpringBootLogFilter;
import com.logmate.processor.filter.impl.TomcatAccessLogFilter;
import com.logmate.processor.parser.impl.spring.SpringBootLogParser;
import com.logmate.processor.parser.impl.spring.SpringBootParsedLogData;
import com.logmate.processor.parser.impl.web.TomcatAccessLogParsedLogData;
import com.logmate.processor.parser.impl.web.TomcatAccessLogParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogType {
  SPRINGBOOT("springboot",
      SpringBootLogParser.class,
      SpringBootLogFilter.class,
      SpringBootParsedLogData.class),
  TOMCAT("tomcat",
      TomcatAccessLogParser.class,
      TomcatAccessLogFilter.class,
      TomcatAccessLogParsedLogData.class);

  private final String str;
  private final Class<? extends LogParser> parserClass;
  private final Class<? extends LogFilter> filterClass;
  private final Class<? extends ParsedLogData> logDataClass;

  public LogParser createParser(ParserConfig config) {
    try {
      return parserClass.getDeclaredConstructor(ParserConfig.class).newInstance(config);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create parser for " + str, e);
    }
  }

  public LogFilter createFilter(FilterConfig config) {
    try {
      return filterClass.getDeclaredConstructor(FilterConfig.class).newInstance(config);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create filter for " + str, e);
    }
  }
}
