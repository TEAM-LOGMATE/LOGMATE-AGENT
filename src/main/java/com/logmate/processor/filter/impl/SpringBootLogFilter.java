package com.logmate.processor.filter.impl;

import com.logmate.config.data.pipeline.FilterConfig;
import com.logmate.processor.filter.LogFilter;
import com.logmate.processor.parser.ParsedLogData;
import com.logmate.processor.parser.impl.spring.SpringBootParsedLogData;
import java.util.Set;

public class SpringBootLogFilter implements LogFilter {

  private final Set<String> allowedLevels;
  private final Set<String> requiredKeywords;

  public SpringBootLogFilter(FilterConfig filterConfig) {
    this.allowedLevels = filterConfig.getAllowedLevels();
    this.requiredKeywords = filterConfig.getRequiredKeywords();
  }

  @Override
  public boolean accept(ParsedLogData log) {
    if (!(log instanceof SpringBootParsedLogData springBootLog)) {
      return false;
    }

    // 파싱이 실패한 로그 라인 (ex. 스택트레이스, 멀티라인 로그)은
    // 사용자 설정과 상관없이 무조건 accept 처리.
    // → 디버깅 필수 정보가 손실되지 않도록 Fail-safe 보장.
    if (!springBootLog.isFormatCorrect()) {
      return true;
    }

    if (!allowedLevels.isEmpty() && !allowedLevels.contains(springBootLog.getLevel().toUpperCase())) {
      return false;
    }

    if (!requiredKeywords.isEmpty()) {
      String message = springBootLog.getMessage();
      if (message == null || message.isBlank()) {
        return false;
      }
      String refinedMessage = message.trim().toLowerCase();

      // requiredKeywords 는 미리 trim 과 LowerCase가 적용되어 있음
      boolean hasKeyword = requiredKeywords.stream()
          .anyMatch(refinedMessage::contains);

      if (!hasKeyword) {
        return false;
      }
    }

    return true;
  }
}
