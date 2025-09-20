package com.logmate.processor.filter.impl;

import com.logmate.config.data.pipeline.FilterConfig;
import com.logmate.processor.filter.LogFilter;
import com.logmate.processor.parser.ParsedLogData;
import com.logmate.processor.parser.impl.spring.SpringBootParsedLogData;
import java.time.LocalDateTime;
import java.util.Set;

public class SpringBootLogFilter implements LogFilter {

  private final Set<String> allowedLevels;
  private final Set<String> allowedLoggers;
  private final Set<String> requiredKeywords;
  private final LocalDateTime after;

  public SpringBootLogFilter(FilterConfig filterConfig) {
    this.allowedLevels = filterConfig.getAllowedLevels() ;
    this.allowedLoggers = filterConfig.getAllowedLoggers() ;
    this.requiredKeywords = filterConfig.getRequiredKeywords() ;
    this.after = filterConfig.getAfter();
  }

  @Override
  public boolean accept(ParsedLogData log) {
    SpringBootParsedLogData springBootLog;
    if (log instanceof SpringBootParsedLogData) {
      springBootLog = (SpringBootParsedLogData) log;
    }
    else {
      return false;
    }

    if (!allowedLevels.isEmpty() && !allowedLevels.contains(springBootLog.getLevel())) return false;

    if (!allowedLoggers.isEmpty() && !allowedLoggers.contains(springBootLog.getLogger())) return false;

    if (after != null && springBootLog.getTimestamp().isBefore(after)) return false;

    if (!requiredKeywords.isEmpty()) {
      boolean hasKeyword = requiredKeywords.stream()
          .anyMatch(keyword -> springBootLog.getMessage().contains(keyword));
      if (!hasKeyword) return false;
    }

    return true;
  }
}
