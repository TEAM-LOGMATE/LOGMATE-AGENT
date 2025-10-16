package com.logmate.processor.parser.impl.web;

import com.logmate.config.data.pipeline.ParserConfig;
import com.logmate.processor.parser.LogParser;
import com.logmate.processor.parser.ParsedLogData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * Tomcat Access Log 파서
 * - Common Log Format 확장 구조 지원
 * - timestamp를 UTC(LocalDateTime, 초 단위)로 변환
 */
@Slf4j
public class TomcatAccessLogParser implements LogParser {

  private final DateTimeFormatter formatter;
  private final ZoneId defaultZoneId;

  // 예시: 127.0.0.1 - - [05/Aug/2025:10:15:30 +0900] "GET /index.html HTTP/1.1" 200 1234 "-" "Mozilla/5.0" "-"
  private static final Pattern LOG_PATTERN = Pattern.compile(
      "(\\S+) \\S+ \\S+ \\[(.+?)\\] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) (\\d+|-) \"(.*?)\" \"(.*?)\" \"(.*?)\""
  );

  public TomcatAccessLogParser(ParserConfig config) {
    this.defaultZoneId = ZoneId.of(config.getConfig().getTimezone());
    this.formatter = new DateTimeFormatterBuilder()
        .appendPattern("dd/MMM/yyyy:HH:mm:ss Z")
        .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd() // 나노세컨드 파싱
        .optionalStart().appendOffsetId().optionalEnd() // Zone Id 파싱
        .toFormatter(Locale.ENGLISH); // Sep 같은 English 파싱 가능
  }

  @Override
  public ParsedLogData parse(String rawLine) {
    try {
      Matcher matcher = LOG_PATTERN.matcher(rawLine);
      if (!matcher.find()) {
        return fallback(rawLine);
      }
      //todo: 더 정확한 Validation 필요
      String ip = matcher.group(1);
      String timestampStr = matcher.group(2);
      Instant utcTime = parseDateTime(timestampStr);

      String method = matcher.group(3);
      String url = matcher.group(4);
      String protocol = matcher.group(5);
      int status = Integer.parseInt(matcher.group(6));
      int size = matcher.group(7).equals("-") ? 0 : Integer.parseInt(matcher.group(7));
      String referer = matcher.group(8);
      String userAgent = matcher.group(9);
      String extra = matcher.group(10);

      return new TomcatAccessLogParsedLogData(
          true,
          ip,
          utcTime,
          method,
          url,
          protocol,
          status,
          size,
          referer,
          userAgent,
          extra,
          defaultZoneId.toString()
      );

    } catch (Exception e) {
      log.warn("Exception: {}", e.getMessage());
      return fallback(rawLine);
    }
  }

  @Override
  public boolean isFormatCorrect(String rawLine) {
    return parse(rawLine).isFormatCorrect();
  }

  private ParsedLogData fallback(String raw) {
    return new TomcatAccessLogParsedLogData(
        false,
        "UNKNOWN",
        Instant.now().truncatedTo(ChronoUnit.SECONDS), // UTC로 통일
        "UNKNOWN",
        "UNKNOWN",
        "UNKNOWN",
        -1,
        0,
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
      // 오프셋(+0900) 포함된 경우
      ZonedDateTime zdt = ZonedDateTime.parse(dateTime, formatter);
      return zdt.toInstant().truncatedTo(ChronoUnit.SECONDS);
    } catch (Exception e1) {
      // 오프셋이 없는 경우
      DateTimeFormatter noZoneFormatter = new DateTimeFormatterBuilder()
          .parseCaseInsensitive()
          .appendPattern("dd/MMM/yyyy:HH:mm:ss")
          .toFormatter(Locale.ENGLISH);

      LocalDateTime ldt = LocalDateTime.parse(dateTime, noZoneFormatter);
      return ldt.atZone(defaultZoneId).toInstant().truncatedTo(ChronoUnit.SECONDS);
    }
  }



}
