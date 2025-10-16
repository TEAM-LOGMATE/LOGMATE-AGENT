package com.logmate.processor.parser;

/**
 * LogParser
 *
 * 로그 파서의 표준 인터페이스.
 *
 * 모든 로그 파서는 이 인터페이스를 구현하여
 * 원시 로그(raw string)를 공통 데이터 구조(ParsedLogData)로 변환한다.
 *
 * 제공 메서드:
 *  - parse(rawLine)        : 한 줄의 원시 로그를 파싱하여 ParsedLogData 반환
 *  - isFormatCorrect(line) : 해당 로그가 기대한 포맷인지 검증
 *
 * 활용 목적:
 *  - Spring Boot, Tomcat, Nginx 등 다양한 로그 포맷을
 *    파서별로 구현해도 동일한 인터페이스로 접근 가능하다.
 *  - 상위 모듈(Exporter, Filter 등)은
 *    구체적인 로그 포맷을 몰라도 파싱 결과를 처리할 수 있다.
 *
 * 확장 가이드:
 *  - 새로운 로그 포맷을 지원하려면 이 인터페이스를 구현한다.
 *  - 예: SpringBootLogParser, TomcatAccessLogParser
 */
public interface LogParser {

  ParsedLogData parse(String rawLine);
  boolean isFormatCorrect(String rawLine);
}
