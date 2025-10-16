package com.logmate.processor.filter;

import com.logmate.processor.parser.ParsedLogData;

/**
 * LogFilter
 *
 * 로그 필터링을 위한 표준 인터페이스.
 *
 * 모든 LogFilter는 특정 조건에 따라 로그 데이터를 수용(accept)할지 여부를 결정한다.
 *
 * 설계 의도:
 * - 파싱된 로그(ParsedLogData)가 주어졌을 때, 조건에 맞는 로그만 다음 단계로 전달한다.
 * - 스택트레이스, 멀티라인 로그 등은 필터링하지 않는다.
 * - 조건 예시:
 *   - Spring Boot 로그의 경우: 로그 레벨(INFO, ERROR 등) + 특정 키워드 포함 여부
 *   - Web(Tomcat) 로그의 경우: HTTP 요청 메서드(GET, POST 등) 허용 여부
 *
 * 확장 가이드:
 * - 새로운 로그 필터가 필요하다면 이 인터페이스를 구현한다.
 *   예) 특정 사용자 아이디 기반 필터, 특정 IP 기반 필터, 응답 코드 기반 필터
 *
 * 사용 흐름:
 * 1. LogParser가 원본 로그 문자열을 ParsedLogData로 변환한다.
 * 2. LogFilter가 조건을 검사하여 true/false를 반환한다.
 * 3. true인 경우에만 파이프라인의 다음 단계(LogProcessor, Exporter 등)로 전달된다.
 */
public interface LogFilter {
  boolean accept(ParsedLogData log);
}
