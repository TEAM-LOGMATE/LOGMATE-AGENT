package com.logmate.tailer.parser.impl.spring;

import static org.junit.jupiter.api.Assertions.*;

import com.logmate.config.ParserConfig;
import com.logmate.config.ParserConfig.ParserDetailConfig;
import com.logmate.processor.parser.ParsedLogData;
import com.logmate.processor.parser.impl.spring.SpringBootLogParser;
import com.logmate.processor.parser.impl.spring.SpringBootParsedLogData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpringBootLogParserTest {

  private SpringBootLogParser parser;

  @BeforeEach
  void setUp() {
    ParserConfig config = new ParserConfig(
        "springboot",
        new ParserDetailConfig("yyyy-MM-dd HH:mm:ss", "Asia/Seoul")
    );
    parser = new SpringBootLogParser(config);
  }

  @Test
  void parse_validLog_returnsCorrectParsedLogData() {
    // given
    String log = "2025-08-03 17:33:12 [main] INFO com.example.MyService - Hello, World!";

    // when
    ParsedLogData parsed = parser.parse(log);

    // then
    assertInstanceOf(SpringBootParsedLogData.class, parsed);
    SpringBootParsedLogData springBootParsedLog = (SpringBootParsedLogData) parsed;

    assertTrue(springBootParsedLog.isFormatCorrect());
    assertEquals("2025-08-03T17:33:12", springBootParsedLog.getTimestamp().toString());
    assertEquals("INFO", springBootParsedLog.getLevel());
    assertEquals("main", springBootParsedLog.getThread());
    assertEquals("com.example.MyService", springBootParsedLog.getLogger());
    assertEquals("Hello, World!", springBootParsedLog.getMessage());
    assertEquals("correct", springBootParsedLog.getTag());
  }

  @Test
  void parse_invalidLog_returnsFallback1() {
    // given
    String log = "invalid log line without timestamp";

    // when
    ParsedLogData parsed = parser.parse(log);

    // then
    assertInstanceOf(SpringBootParsedLogData.class, parsed);
    SpringBootParsedLogData springBootParsedLog = (SpringBootParsedLogData) parsed;

    assertFalse(springBootParsedLog.isFormatCorrect());
    assertEquals("parse_fail", springBootParsedLog.getTag());
  }

  @Test
  void parse_invalidLog_returnsFallback2() {
    // given
    String log = "2025-08-03 17:33:12 [main] com.example.MyService - Hello, World!";

    // when
    ParsedLogData parsed = parser.parse(log);

    // then
    assertInstanceOf(SpringBootParsedLogData.class, parsed);
    SpringBootParsedLogData springBootParsedLog = (SpringBootParsedLogData) parsed;

    assertFalse(springBootParsedLog.isFormatCorrect());
    assertEquals("parse_fail", springBootParsedLog.getTag());
  }


  @Test
  void parse_blankLine_returnsFallback() {
    // when
    ParsedLogData parsed = parser.parse("  ");

    // then
    assertInstanceOf(SpringBootParsedLogData.class, parsed);
    SpringBootParsedLogData springBootParsedLog = (SpringBootParsedLogData) parsed;

    assertFalse(springBootParsedLog.isFormatCorrect());
    assertEquals("parse_fail", springBootParsedLog.getTag());
  }

  @Test
  void parse_null_returnsFallback() {
    // when
    ParsedLogData parsed = parser.parse(null);

    // then
    assertInstanceOf(SpringBootParsedLogData.class, parsed);
    SpringBootParsedLogData springBootParsedLog = (SpringBootParsedLogData) parsed;

    assertFalse(springBootParsedLog.isFormatCorrect());
    assertEquals("parse_fail", springBootParsedLog.getTag());
  }

  @Test
  void isFormatCorrect_shouldReturnTrue_forValidLog() {
    // given
    String log = "2025-08-04 15:10:01 [main] INFO com.example.MyService - Hello World";

    // when
    boolean result = parser.isFormatCorrect(log);

    // then
    assertTrue(result, "정상적인 로그 라인은 true를 반환해야 함");
  }

  @Test
  void isFormatCorrect_shouldReturnFalse_forInvalidLog() {
    // given
    String log = "invalid log line without any known structure";

    // when
    boolean result = parser.isFormatCorrect(log);

    // then
    assertFalse(result, "비정상적인 로그 라인은 false를 반환해야 함");
  }

  @Test
  void isFormatCorrect_shouldReturnFalse_forEmptyLine() {
    // given
    String log = " ";

    // when
    boolean result = parser.isFormatCorrect(log);

    // then
    assertFalse(result, "공백 로그는 false 반환");
  }

  @Test
  void isFormatCorrect_shouldReturnFalse_forUnsupportedLogLevel() {
    // given
    String log = "2025-08-04 15:10:01 [main] NOTICE com.example.MyService - Something happened";

    // when
    boolean result = parser.isFormatCorrect(log);

    // then
    assertFalse(result, "정의되지 않은 로그 레벨은 false 반환");
  }

  @Test
  void isFormatCorrect_shouldReturnFalse_whenThreadFormatIsInvalid() {
    String log = "2025-08-04 15:10:01 main INFO com.example.MyService - No brackets in thread";
    boolean result = parser.isFormatCorrect(log);
    assertFalse(result, "스레드 이름이 대괄호로 감싸지지 않으면 false");
  }

  @Test
  void isFormatCorrect_shouldReturnFalse_whenLoggerIsMissing() {
    String log = "2025-08-04 15:10:01 [main] INFO - Message only";
    boolean result = parser.isFormatCorrect(log);
    assertFalse(result, "로거 이름이 빠지면 false");
  }

  @Test
  void isFormatCorrect_shouldReturnTrue_whenMessageIsEmptyButFormatIsValid() {
    String log = "2025-08-04 15:10:01 [main] INFO com.example.MyService - ";
    boolean result = parser.isFormatCorrect(log);
    assertFalse(result, "메시지가 없으면 False");
  }

  @Test
  void isFormatCorrect_shouldReturnFalse_whenTimestampIsInvalid() {
    String log = "2025/08/04 15:10:01 [main] INFO com.example.MyService - Wrong timestamp format";
    boolean result = parser.isFormatCorrect(log);
    assertFalse(result, "타임스탬프 형식이 잘못되면 false");
  }

  @Test
  void isFormatCorrect_shouldReturnFalse_whenLevelIsLowercase() {
    String log = "2025-08-04 15:10:01 [main] info com.example.MyService - lowercase level";
    boolean result = parser.isFormatCorrect(log);
    assertFalse(result, "소문자 로그 레벨은 인식 못함");
  }
}