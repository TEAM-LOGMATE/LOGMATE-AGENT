package com.logmate.processor.parser.impl.spring;

import static org.junit.jupiter.api.Assertions.*;

import com.logmate.config.data.pipeline.ParserConfig;
import com.logmate.config.data.pipeline.ParserConfig.ParserDetailConfig;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpringBootLogParserTest {

  private SpringBootLogParser parser;
  private Instant expectTime;

  @BeforeEach
  void setup() {
    // config mock: timezone = Asia/Seoul
    ParserConfig config = new ParserConfig();
    ParserDetailConfig detailConfig = new ParserDetailConfig();
    detailConfig.setTimezone("Asia/Seoul");
    config.setConfig(detailConfig);
    parser = new SpringBootLogParser(config);
    expectTime = Instant.parse(LocalDateTime.of(2025, 9, 21, 10, 15, 30).toString() + "Z" );
  }

  @Test
  void testParse_withUtcZ() {
    String log = "2025-09-21T10:15:30Z INFO 123 --- [main] o.s.boot.SpringApp : Started application";

    SpringBootParsedLogData parsed = (SpringBootParsedLogData) parser.parse(log);

    // UTC 그대로
    assertTrue(parsed.isFormatCorrect());
    assertEquals("INFO", parsed.getLevel());
    assertEquals("123", parsed.getThread());
    assertEquals("o.s.boot.SpringApp", parsed.getLogger());
    assertEquals("Started application", parsed.getMessage());
    assertEquals(parsed.getTimestamp(), expectTime);
  }

  @Test
  void testParse_withOffset() {
    String log = "2025-09-21T19:15:30+09:00 INFO 123 --- [main] o.s.boot.SpringApp : Started application";

    SpringBootParsedLogData parsed = (SpringBootParsedLogData) parser.parse(log);

    // +09:00 → UTC 변환
    assertTrue(parsed.isFormatCorrect());
    assertEquals("INFO", parsed.getLevel());
    assertEquals("123", parsed.getThread());
    assertEquals("o.s.boot.SpringApp", parsed.getLogger());
    assertEquals("Started application", parsed.getMessage());
    assertEquals(parsed.getTimestamp(), expectTime);
  }

  @Test
  void testParse_withMillisAndOffset() {
    String log = "2025-09-21T19:15:30.987+09:00 INFO 123 --- [main] o.s.boot.SpringApp : Started application";

    SpringBootParsedLogData parsed = (SpringBootParsedLogData) parser.parse(log);

    // 밀리초 제거되고 UTC 변환
    assertTrue(parsed.isFormatCorrect());
    assertEquals("INFO", parsed.getLevel());
    assertEquals("123", parsed.getThread());
    assertEquals("o.s.boot.SpringApp", parsed.getLogger());
    assertEquals("Started application", parsed.getMessage());
    assertEquals(parsed.getTimestamp(), expectTime);
  }

  @Test
  void testParse_withoutTimezone() {
    String log = "2025-09-21T19:15:30 INFO 123 --- [main] o.s.boot.SpringApp : Started application";

    SpringBootParsedLogData parsed = (SpringBootParsedLogData) parser.parse(log);

    assertTrue(parsed.isFormatCorrect());
    assertEquals("INFO", parsed.getLevel());
    assertEquals("123", parsed.getThread());
    assertEquals("o.s.boot.SpringApp", parsed.getLogger());
    assertEquals("Started application", parsed.getMessage());
    // Asia/Seoul 해석 후 UTC 변환 → 10:15:30
    assertEquals(parsed.getTimestamp(), expectTime);

  }

  @Test
  void testParse_blankLine() {
    SpringBootParsedLogData parsed = (SpringBootParsedLogData) parser.parse(" ");
    assertEquals(LocalDateTime.now(ZoneOffset.UTC).withNano(0).toInstant(ZoneOffset.UTC), parsed.getTimestamp());
    assertFalse(parsed.isFormatCorrect());
  }

  @Test
  void testParse_invalidFormat() {
    SpringBootParsedLogData parsed = (SpringBootParsedLogData) parser.parse("SOMETHING INVALID LOG");
    assertEquals(LocalDateTime.now(ZoneOffset.UTC).withNano(0).toInstant(ZoneOffset.UTC), parsed.getTimestamp());
    assertFalse(parsed.isFormatCorrect());
  }
}