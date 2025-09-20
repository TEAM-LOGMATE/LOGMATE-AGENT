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
  private final Set<Integer> allowedStatusCodes;
  private final Set<String> urlPrefix;

  public TomcatAccessLogFilter(FilterConfig config) {
    this.allowedMethods = config.getAllowedMethods();
    this.allowedStatusCodes = config.getAllowedStatusCodes();
    this.urlPrefix = config.getUrlPrefix();
  }

  @Override
  public boolean accept(ParsedLogData log) {
    if (!(log instanceof TomcatAccessLogParsedLogData webLog)) {
      return false;
    }

    // 요청 메서드 필터링
    if (!allowedMethods.isEmpty() && !allowedMethods.contains(webLog.getMethod())) return false;

    // 상태 코드 필터링
    if (!allowedStatusCodes.isEmpty() && !allowedStatusCodes.contains(webLog.getStatusCode())) return false;

    // URL prefix 필터링
    if (!urlPrefix.isEmpty()) {
      Optional<String> any = urlPrefix.stream()
          .filter(url -> webLog.getUrl().startsWith(url))
          .findAny();
      if (any.isEmpty()) return false;
    }

    return true;
  }
}
