package com.logmate.processor.parser.impl.spring;

import com.logmate.config.ParserConfig;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.logmate.processor.parser.LogParser;
import com.logmate.processor.parser.ParsedLogData;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringBootLogParser implements LogParser {

  private final ParserConfig config;
  private final DateTimeFormatter formatter;
  private final ZoneId zoneId;

  // Spring Boot 기본 로그 패턴 (ISO8601 기반)
  private static final Pattern LOG_PATTERN = Pattern.compile(
      "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})" +                // timestamp (초 단위까지만)
          "(?:\\.\\d{1,9})?" +                                        // 소수점 이하 (옵션)
          "(?:Z|[+-]\\d{2}:\\d{2})?\\s+" +                            // 타임존 (옵션)
          "(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)\\s+" +             // level
          "(\\d+)\\s+---\\s+\\[(.+?)]\\s+" +                          // thread
          "([^:]+)\\s+:\\s+" +                                        // logger
          "(.*)$"                                                     // message
  );

  public SpringBootLogParser(ParserConfig config) {
    this.config = config;
    this.zoneId = ZoneId.of(config.getConfig().getTimezone(), ZoneId.SHORT_IDS);
    // 초 단위까지만 파싱
    this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
  }

  /**
   * 1초마다 받아 온 로그들을 한 줄씩 parser -> filter 를 통과시켜 버퍼링해둔다.
   * 이후 exporter 로 버퍼링된 로그들을 외부로 전송한다.
   *
   * @param rawLine Spring Boot 로그 한 줄을 SpringBootParsedLogData 객체로 파싱한다.
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
      LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);
      ZonedDateTime zonedTimestamp = timestamp.atZone(zoneId);

      String level = matcher.group(2);
      String thread = matcher.group(4);
      String logger = matcher.group(5).trim();
      String message = matcher.group(6).trim();

      if (message.isEmpty()) {
        return fallback(rawLine);
      }

      return new SpringBootParsedLogData(
          true,
          zonedTimestamp.toLocalDateTime(),
          level,
          thread,
          logger,
          message
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

  private ParsedLogData fallback(String raw) {
    return new SpringBootParsedLogData(false,
        LocalDateTime.now(zoneId).withNano(0),
        "UNKNOWN",
        "UNKNOWN",
        "UNKNOWN",
        raw
    );
  }
}
