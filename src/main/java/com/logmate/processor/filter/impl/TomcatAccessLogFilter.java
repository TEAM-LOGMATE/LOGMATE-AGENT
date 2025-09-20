package com.logmate.processor.filter.impl;

import com.logmate.config.data.pipeline.FilterConfig;
import com.logmate.processor.filter.LogFilter;
import com.logmate.processor.parser.ParsedLogData;
import com.logmate.processor.parser.impl.web.TomcatAccessLogParsedLogData;
import java.util.Optional;
import java.util.Set;

public class TomcatAccessLogFilter implements LogFilter {

  // Web 로그 전용 필터 항목
  private final Set<String> allowedMethods;


  public TomcatAccessLogFilter(FilterConfig config) {
    this.allowedMethods = config.getAllowedMethods();
  }

  @Override
  public boolean accept(ParsedLogData log) {
    if (!(log instanceof TomcatAccessLogParsedLogData webLog)) {
      return false;
    }

    // 요청 메서드 필터링
    if (!allowedMethods.isEmpty() && !allowedMethods.contains(webLog.getMethod())) return false;

    return true;
  }
}
