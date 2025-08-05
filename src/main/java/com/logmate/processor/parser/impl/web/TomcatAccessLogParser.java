package com.logmate.processor.parser.impl.web;

import com.logmate.config.ParserConfig;
import com.logmate.processor.parser.LogParser;
import com.logmate.processor.parser.ParsedLogData;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TomcatAccessLogParser implements LogParser {

  private final ParserConfig config;
  private final DateTimeFormatter formatter;
  private final ZoneId zoneId;

  // 예시: 127.0.0.1 - - [05/Aug/2025:10:15:30 +0900] "GET /index.html HTTP/1.1" 200 1234 "-" "Mozilla/5.0" "-"
  private static final Pattern LOG_PATTERN = Pattern.compile(
      "(\\S+) \\S+ \\S+ \\[(.+?)\\] \"(\\S+) (\\S+) (\\S+)\" (\\d{3}) (\\d+|-) \"(.*?)\" \"(.*?)\" \"(.*?)\""
  );

  public TomcatAccessLogParser(ParserConfig config) {
    this.config = config;
    this.zoneId = ZoneId.of(config.getConfig().getTimezone(), ZoneId.SHORT_IDS);
    this.formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
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
      LocalDateTime timestamp = LocalDateTime.parse(timestampStr, formatter);
      ZonedDateTime zonedDateTime = timestamp.atZone(zoneId);

      String method = matcher.group(3);
      String url = matcher.group(4);
      String protocol = matcher.group(5);
      int status = Integer.parseInt(matcher.group(6));
      int size = matcher.group(7).equals("-") ? 0 : Integer.parseInt(matcher.group(7));
      String referer = matcher.group(8);
      String userAgent = matcher.group(9);
      String extra = matcher.group(10);
      return new TomcatAccessLogParsedLogData(true, ip, zonedDateTime.toLocalDateTime(), method, url, protocol,
          status, size, referer, userAgent, extra);

    } catch (Exception e) {
      log.warn("Exception: {}", e.getMessage());
      return fallback(rawLine);
    }
  }

  private ParsedLogData fallback(String raw) {
    return new TomcatAccessLogParsedLogData(
        false,
        "UNKNOWN",
        LocalDateTime.now(),
        "UNKNOWN",
        "UNKNOWN",
        "UNKNOWN",
        -1,
        0,
        "UNKNOWN",
        "UNKNOWN",
        raw
    );
  }

  @Override
  public boolean isFormatCorrect(String rawLine) {
    return parse(rawLine).isFormatCorrect();
  }
}
