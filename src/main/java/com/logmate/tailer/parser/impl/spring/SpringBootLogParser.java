package com.logmate.tailer.parser.impl.spring;

import com.logmate.injection.config.ParserConfig;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.logmate.tailer.parser.LogParser;
import com.logmate.tailer.parser.ParsedLogData;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class SpringBootLogParser implements LogParser {

  private final ParserConfig config;
  private final DateTimeFormatter formatter;
  private final ZoneId zoneId;
  private final Set<String> SpringbootLogLevels = new HashSet<>() {
    {
      add("INFO");
      add("DEBUG");
      add("WARN");
      add("ERROR");
      add("TRACE");
      add("FATAL");
      add("OFF");
    }
  };

  public SpringBootLogParser(ParserConfig config) {
    this.formatter = DateTimeFormatter.ofPattern(config.getConfig().getTimestampPattern());
    this.zoneId = ZoneId.of(config.getConfig().getTimezone(), ZoneId.SHORT_IDS);
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
      StringTokenizer st = new StringTokenizer(rawLine, " ");

      // 날짜 + 시간
      String timestampStr = st.nextToken() + " " + st.nextToken();
      LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);
      ZonedDateTime zonedTimestamp = timestamp.atZone(zoneId);

      // 스레드 이름
      String thread = st.nextToken();
      thread = thread.substring(thread.indexOf('[') + 1, thread.indexOf(']'));

      // 로그 레벨
      String level = st.nextToken();

      if (!SpringbootLogLevels.contains(level)) {
        throw new IllegalArgumentException();
      }

      // 로거 이름과 메시지
      String logger = st.nextToken();
      if (!st.nextToken().equals("-")) {
        throw new IllegalArgumentException();
      }

      StringBuilder messageBuilder = new StringBuilder();
      while (st.hasMoreTokens()) {
        messageBuilder.append(st.nextToken()).append(" ");
      }
      String message = messageBuilder.toString().trim();
      if (message.isEmpty()) {
        throw new IllegalArgumentException();
      }
      return new SpringBootParsedLogData(true, zonedTimestamp.toLocalDateTime(), level, thread, logger, message, "correct");
    } catch (Exception e) {
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
        LocalDateTime.now(zoneId),
        "UNKNOWN",
        "UNKNOWN",
        "UNKNOWN",
        raw,
        "parse_fail"
    );
  }
}
