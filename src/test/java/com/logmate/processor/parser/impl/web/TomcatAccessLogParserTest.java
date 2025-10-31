package com.logmate.processor.parser.impl.web;

import static org.junit.jupiter.api.Assertions.*;

import com.logmate.config.data.pipeline.ParserConfig;
import com.logmate.config.data.pipeline.ParserConfig.ParserDetailConfig;
import com.logmate.processor.parser.ParsedLogData;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TomcatAccessLogParserTest {

  private TomcatAccessLogParser parser;

  @BeforeEach
  void setup() {
    ParserConfig config = new ParserConfig();
    ParserDetailConfig detailConfig = new ParserDetailConfig();
    detailConfig.setTimezone("Asia/Seoul");
    config.setConfig(detailConfig);
    parser = new TomcatAccessLogParser(config);
  }

  @Test
  void testParse_withOffsetPlus0900() {
    String log = "127.0.0.1 - - [05/Aug/2025:10:15:30 +0900] \"GET /index.html HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\" \"-\"";

    TomcatAccessLogParsedLogData parsed = (TomcatAccessLogParsedLogData) parser.parse(log);

    assertTrue(parsed.isFormatCorrect());
    // 10:15:30 +09:00 → 01:15:30 UTC
    assertEquals(LocalDateTime.of(2025, 8, 5, 1, 15, 30), parsed.getTimestamp());
    assertEquals("/index.html", parsed.getUrl());
  }

  @Test
  void testParse_withUtcZ() {
    String log = "127.0.0.1 - - [05/Aug/2025:01:15:30 +0000] \"POST /api/test HTTP/1.1\" 404 5678 \"-\" \"JUnit\" \"extra-info\"";

    TomcatAccessLogParsedLogData parsed = (TomcatAccessLogParsedLogData) parser.parse(log);

    assertTrue(parsed.isFormatCorrect());
    assertEquals(LocalDateTime.of(2025, 8, 5, 1, 15, 30), parsed.getTimestamp());
    assertEquals("/api/test", parsed.getUrl());
  }

  @Test
  void testParse_withoutOffset() {
    // 타임존 없음 → Asia/Seoul 적용 후 UTC 변환
    String log = "127.0.0.1 - - [05/Aug/2025:10:15:30] \"GET /no-offset HTTP/1.1\" 200 100 \"-\" \"JUnit\" \"-\"";

    TomcatAccessLogParsedLogData parsed = (TomcatAccessLogParsedLogData) parser.parse(log);

    assertTrue(parsed.isFormatCorrect());
    // 10:15:30 KST → 01:15:30 UTC
    assertEquals(LocalDateTime.of(2025, 8, 5, 1, 15, 30), parsed.getTimestamp());
    assertEquals("/no-offset", parsed.getUrl());
  }

  @Test
  void testParse_sizeDash() {
    String log = "127.0.0.1 - - [05/Aug/2025:10:15:30 +0900] \"GET /dash-size HTTP/1.1\" 500 - \"-\" \"JUnit\" \"-\"";

    TomcatAccessLogParsedLogData parsed = (TomcatAccessLogParsedLogData) parser.parse(log);

    assertTrue(parsed.isFormatCorrect());
    assertEquals(LocalDateTime.of(2025, 8, 5, 1, 15, 30), parsed.getTimestamp());
    assertEquals("/dash-size", parsed.getUrl());
    // size "-" → 0 확인
    // 만약 ParsedLogData에 getSize() 구현이 있다면:
    // assertEquals(0, ((TomcatAccessLogParsedLogData) parsed).getSize());
  }

  @Test
  void testParse_invalidLog() {
    String log = "INVALID LOG ENTRY";

    TomcatAccessLogParsedLogData parsed = (TomcatAccessLogParsedLogData) parser.parse(log);

    assertFalse(parsed.isFormatCorrect());
    assertEquals(LocalDateTime.now(ZoneOffset.UTC).withNano(0),  parsed.getTimestamp());
    assertNotNull(parsed.getTimestamp());
    assertEquals("UNKNOWN", parsed.getUrl());
  }
}