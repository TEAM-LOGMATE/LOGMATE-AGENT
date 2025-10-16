package com.logmate.processor.parser.impl.spring;

import com.logmate.config.data.pipeline.ParserConfig;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.logmate.processor.parser.LogParser;
import com.logmate.processor.parser.ParsedLogData;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Boot 로그 파서
 * - Spring Boot 기본 로그 패턴(ISO-8601 기반)을 정규식으로 매칭
 * - timestamp를 UTC(LocalDateTime, 초 단위)로 변환
 * - 로그 레벨, 스레드, 로거, 메시지를 추출
 */
@Slf4j
public class SpringBootLogParser implements LogParser {

  private final DateTimeFormatter formatter;
  private final ZoneId defaultZoneId;

  // Spring Boot 기본 로그 패턴 (ISO-8601 기반: timestamp, level, thread, logger, message)
  private static final Pattern LOG_PATTERN = Pattern.compile(
      "^(\\d{4}-\\d{2}-\\d{2}[T\\s]\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?(?:Z|[+-]\\d{2}:\\d{2})?)\\s+" + // timestamp
          "(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)\\s+" +             // level
          "(\\d+)\\s+---\\s+\\[(.+?)]\\s+" +                          // thread
          "([^:]+)\\s+:\\s+" +                                        // logger
          "(.*)$"                                                     // message
  );

  public SpringBootLogParser(ParserConfig config) {
    // 사용자가 지정한 타임존 (예: Asia/Seoul)
    this.defaultZoneId = ZoneId.of(config.getConfig().getTimezone());
    // ISO-8601 포맷 + 선택적 요소(T/공백, 밀리초, 오프셋) 지원
    this.formatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd")
        .appendPattern("[['T'][' ']]HH:mm:ss")   // T 또는 공백 허용
        .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd() // 나노세컨드 파싱
        .optionalStart().appendOffsetId().optionalEnd() // Zone Id 파싱
        .toFormatter(Locale.ENGLISH); // Sep 같은 English 파싱 가능
  }

  /**
   * 로그 한 줄을 파싱하여 SpringBootParsedLogData로 변환
   * - 파싱 실패 시 fallback 처리
   */
  @Override
  public ParsedLogData parse(String rawLine) {
    try {
      if (rawLine == null || rawLine.isBlank()) {
        return fallback("BLANK_LINE");
      }

      Matcher matcher = LOG_PATTERN.matcher(rawLine);
      if (!matcher.find()) {
        return fallback(rawLine);
      }

      String timestampStr = matcher.group(1);
      Instant utcTime= parseDateTime(timestampStr);

      String level = matcher.group(2);
      String thread = matcher.group(3);
      String logger = matcher.group(5).trim();
      String message = matcher.group(6).trim();

      if (message.isEmpty()) {
        return fallback(rawLine);
      }

      return new SpringBootParsedLogData(
          true,
          utcTime,
          level,
          thread,
          logger,
          message,
          defaultZoneId.toString()
      );

    } catch (Exception e) {
      log.warn("[SpringBootLogParser] parse error: {}", e.getMessage(), e);
      return fallback(rawLine);
    }
  }

  @Override
  public boolean isFormatCorrect(String rawLine) {
    ParsedLogData parse = parse(rawLine);
    return parse.isFormatCorrect();
  }

  /**
   * 파싱 실패 시 fallback 객체 반환
   * - 현재 UTC 시간으로 timestamp 생성
   * - 나머지는 UNKNOWN 값
   */
  private ParsedLogData fallback(String raw) {
    return new SpringBootParsedLogData(false,
        Instant.now().truncatedTo(ChronoUnit.SECONDS), // UTC로 통일
        "UNKNOWN",
        "UNKNOWN",
        "UNKNOWN",
        raw,
        defaultZoneId.toString()
    );
  }

  /**
   * timestamp 문자열을 UTC(LocalDateTime)으로 변환
   * - ISO-8601 형식 지원
   * - 타임존 없는 경우 defaultZoneId로 해석
   * - 항상 소수점 이하 제거
   */
  private Instant parseDateTime(String dateTime) {
      try {
        // ISO 8601 표준(예: 2025-10-13T13:30:00Z, 2025-10-13T13:30:00+09:00)
        return Instant.parse(dateTime).truncatedTo(ChronoUnit.SECONDS);
      } catch (Exception e) {
        // 오프셋/타임존이 없는 경우 (예: 13/Oct/2025:22:30:00)
        LocalDateTime ldt = LocalDateTime.parse(
            dateTime,
            formatter
        );
        return ldt.atZone(defaultZoneId)
            .toInstant()
            .truncatedTo(ChronoUnit.SECONDS);
      }
  }
}
