package com.logmate.processor.parser;

import java.time.Instant;

/**
 * ParsedLogData
 *
 * 로그 파서(Parser)의 표준 결과 인터페이스.
 *
 * 모든 로그 파서는 이 인터페이스를 구현하여
 * 파싱된 로그를 공통된 구조로 반환한다.
 *
 * 필수 필드:
 *  - timestamp      : UTC 기준의 로그 발생 시각
 *  - userTimezone   : 사용자가 설정한 IANA ZoneId (예: "Asia/Seoul")
 *  - message        : 로그의 원문 또는 핵심 메시지
 *  - isFormatCorrect  : 파싱 성공 여부
 *  이외 커스텀 필드 추가 가능
 *
 * 활용 목적:
 *  - Spring Boot, Tomcat, Nginx 등 다양한 로그 포맷을
 *    단일한 구조로 통합하여 처리 가능하다.
 *  - Exporter, Filter 등 상위 모듈은
 *    로그 포맷에 상관없이 동일한 인터페이스로 접근할 수 있다.
 *
 * 확장 가이드:
 *  - 새로운 로그 포맷을 지원하려면 이 인터페이스를 구현한다.
 *  - 예: SpringBootParsedLogData, TomcatAccessLogParsedLogData
 */
public interface ParsedLogData {
  String getMessage();
  String getUserTimezone();
  Instant getTimestamp();
  boolean isFormatCorrect();
}
