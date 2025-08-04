package com.logmate.tailer.merger;

import static org.junit.jupiter.api.Assertions.*;

import com.logmate.injection.config.MultilineConfig;
import com.logmate.injection.config.ParserConfig;
import com.logmate.injection.config.ParserConfig.ParserDetailConfig;
import com.logmate.tailer.parser.impl.spring.SpringBootLogParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MultilineProcessorTest {

  private MultilineProcessor processor;
  private SpringBootLogParser parser;

  @BeforeEach
  public void setUp() {
    ParserConfig config = new ParserConfig(
        "springboot",
        new ParserDetailConfig("yyyy-MM-dd HH:mm:ss", "Asia/Seoul")
    );
    SpringBootLogParser parser = new SpringBootLogParser(config);
    this.parser = parser;
  }

  @Test
  void process_whenMultilineDisabled_returnsRawLines() {
    MultilineConfig multilineConfig = new MultilineConfig(true, 10);
    processor = new MultilineProcessor(parser, multilineConfig);

    String[] input = {
        "line1",
        "line2"
    };

    String[] result = processor.process(input);
    String[] expected = {
        "[MERGED-STACKTRACE]\nline1\nline2"
    };
    assertArrayEquals(expected, result);
  }

  @Test
  void process_mergesNonFormattedLinesUntilFormattedLine() {
    MultilineConfig config = new MultilineConfig(true, 10);
    MultilineProcessor processor = new MultilineProcessor(parser, config);

    String[] input = {
        "Exception in thread",
        "    at com.example.Main",
        "2025-08-04 15:10:01 [main] INFO com.example.MyService - Recovered"
    };

    String[] result = processor.process(input);

    assertEquals(2, result.length);
    assertTrue(result[0].startsWith("[MERGED-STACKTRACE]"));
    assertTrue(result[0].contains("Exception in thread"));
    assertEquals("2025-08-04 15:10:01 [main] INFO com.example.MyService - Recovered", result[1]);
  }

  @Test
  void process_flushesWhenMaxLinesExceeded() {
    MultilineConfig config = new MultilineConfig(true, 2); // 최대 2줄만 버퍼링
    MultilineProcessor processor = new MultilineProcessor(parser, config);

    String[] input = {
        "non-format-1",
        "non-format-2",
        "non-format-3",
        "2025-08-04 15:10:01 [main] INFO com.example.MyService - Next"
    };

    String[] result = processor.process(input);

    assertEquals(3, result.length);
    assertTrue(result[0].contains("non-format-1"));
    assertTrue(result[0].contains("non-format-2"));
    assertFalse(result[0].contains("non-format-3"));
    assertTrue(result[1].contains("non-format-3")); // 강제 flush 됨
    assertTrue(result[2].startsWith("2025-"));
  }

  @Test
  void process_flushesRemainingBufferAtEnd() {
    MultilineConfig config = new MultilineConfig(true, 10);
    MultilineProcessor processor = new MultilineProcessor(parser, config);

    String[] input = {
        "non-format-1",
        "non-format-2"
    };

    String[] result = processor.process(input);

    assertEquals(1, result.length);
    assertTrue(result[0].contains("non-format-1"));
    assertTrue(result[0].startsWith("[MERGED-STACKTRACE]"));
  }

  @Test
  void process_enabled_false() {
    MultilineConfig config = new MultilineConfig(false, 10);
    MultilineProcessor processor = new MultilineProcessor(parser, config);

    String[] input = {
        "non-format-1",
        "non-format-2"
    };

    String[] result = processor.process(input);

    assertEquals(2, result.length);
    assertTrue(result[0].contains("non-format-1"));
    assertTrue(result[1].contains("non-format-2"));
    assertFalse(result[0].startsWith("[MERGED-STACKTRACE]"));
  }
}