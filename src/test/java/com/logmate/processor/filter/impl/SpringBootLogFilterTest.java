package com.logmate.processor.filter.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.logmate.config.data.pipeline.FilterConfig;
import com.logmate.processor.parser.impl.spring.SpringBootParsedLogData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SpringBootLogFilterTest {

  @Test
  void testAccept_withAllowedLevelAndKeyword() {
    // given
    FilterConfig config = new FilterConfig(
        Set.of("INFO"),             // allowedLevels
        Set.of("error"),            // requiredKeywords (lower-case)
        Set.of()                    // allowedMethods
    );
    SpringBootLogFilter filter = new SpringBootLogFilter(config);

    SpringBootParsedLogData log = new SpringBootParsedLogData(
        true,
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        "INFO",
        "main",
        "com.example.Test",
        "This is an error message",
        "Asia/Seoul"
    );

    // when & then
    assertTrue(filter.accept(log));
  }

  @Test
  void testReject_dueToLevelMismatch() {
    FilterConfig config = new FilterConfig(
        Set.of("WARN"), // only WARN allowed
        Set.of(),
        Set.of()
    );
    SpringBootLogFilter filter = new SpringBootLogFilter(config);

    SpringBootParsedLogData log = new SpringBootParsedLogData(
        true,
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        "INFO", // not in allowedLevels
        "main",
        "com.example.Test",
        "Some message",
        "Asia/Seoul"
    );

    assertFalse(filter.accept(log));
  }

  @Test
  void testReject_dueToMissingKeyword() {
    FilterConfig config = new FilterConfig(
        Set.of("INFO"),
        Set.of("critical"), // required keyword
        Set.of()
    );
    SpringBootLogFilter filter = new SpringBootLogFilter(config);

    SpringBootParsedLogData log = new SpringBootParsedLogData(
        true,
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        "INFO",
        "main",
        "com.example.Test",
        "Normal message without keyword",
        "Asia/Seoul"
    );

    assertFalse(filter.accept(log));
  }

  @Test
  void testAccept_withNoFilteringSet() {
    FilterConfig config = new FilterConfig(
        Set.of(),
        Set.of(),
        Set.of()
    );
    SpringBootLogFilter filter = new SpringBootLogFilter(config);

    SpringBootParsedLogData log = new SpringBootParsedLogData(
        true,
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        "INFO",
        "main",
        "com.example.Test",
        "Normal message without keyword",
        "Asia/Seoul"
    );

    assertTrue(filter.accept(log));
  }
}