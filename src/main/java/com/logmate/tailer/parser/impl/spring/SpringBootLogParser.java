package com.logmate.tailer.parser.impl.spring;

import com.logmate.injection.config.ParserConfig;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.logmate.tailer.parser.LogParser;
import com.logmate.tailer.parser.ParsedLogData;
import lombok.RequiredArgsConstructor;


public class SpringBootLogParser implements LogParser {

  private final ParserConfig config;
  private final DateTimeFormatter formatter;
  private final ZoneId zoneId;

  public SpringBootLogParser(ParserConfig config) {
    this.formatter = DateTimeFormatter.ofPattern(config.getConfig().getTimestampPattern());
    this.zoneId = ZoneId.of(config.getConfig().getTimezone());
    this.config = config;
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
      // 날짜 + 시간
      String timestampStr = rawLine.substring(0, 19);
      LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);
      ZonedDateTime zonedTimestamp = timestamp.atZone(zoneId);

      // 스레드 이름
      int threadStart = rawLine.indexOf('[');
      int threadEnd = rawLine.indexOf(']');
      String thread = rawLine.substring(threadStart + 1, threadEnd);

      // 로그 레벨
      String afterThread = rawLine.substring(threadEnd + 2).trim(); // 공백 제거
      String level = afterThread.substring(0, afterThread.indexOf(' '));

      // 로거 이름과 메시지
      int dashIndex = rawLine.indexOf(" - ");
      String logger = rawLine.substring(threadEnd + 2 + level.length(), dashIndex).trim();
      String message = rawLine.substring(dashIndex + 3).trim();

      return new SpringBootParsedLogData(true, zonedTimestamp.toLocalDateTime(), level, thread, logger, message);
    } catch (Exception e) {
      return fallback(rawLine);
    }
  }

  private ParsedLogData fallback(String raw) {
    return new SpringBootParsedLogData(false,
        LocalDateTime.now(zoneId),
        "UNKNOWN",
        "UNKNOWN",
        config.getFallback().getUnstructuredTag(),
        raw
    );
  }
}
