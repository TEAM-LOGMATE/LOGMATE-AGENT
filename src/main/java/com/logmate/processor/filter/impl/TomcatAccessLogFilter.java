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

    // 파싱이 실패한 로그 라인 (ex. 스택트레이스, 멀티라인 로그)은
    // 사용자 설정과 상관없이 무조건 accept 처리.
    // → 디버깅 필수 정보가 손실되지 않도록 Fail-safe 보장.
    if (!webLog.isFormatCorrect()) {
      return true;
    }

    // 요청 메서드 필터링
    if (!allowedMethods.isEmpty() && !allowedMethods.contains(webLog.getMethod().toUpperCase())) {
      return false;
    }

    return true;
  }
}
