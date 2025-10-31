package com.logmate.processor.filter.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.logmate.config.data.pipeline.FilterConfig;
import com.logmate.processor.parser.impl.web.TomcatAccessLogParsedLogData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TomcatAccessLogFilterTest {
  @Test
  void testAccept_withAllowedMethod() {
    FilterConfig config = new FilterConfig(
        Set.of(),
        Set.of(),
        Set.of("GET")
    );
    TomcatAccessLogFilter filter = new TomcatAccessLogFilter(config);

    TomcatAccessLogParsedLogData log = new TomcatAccessLogParsedLogData(
        true,
        "127.0.0.1",
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        "GET",
        "/index.html",
        "HTTP/1.1",
        200,
        1234,
        "-",
        "JUnit",
        "-",
        "Asia/Seoul"
    );

    assertTrue(filter.accept(log));
  }

  @Test
  void testReject_dueToMethodMismatch() {
    FilterConfig config = new FilterConfig(
        Set.of(),
        Set.of(),
        Set.of("POST")
    );
    TomcatAccessLogFilter filter = new TomcatAccessLogFilter(config);

    TomcatAccessLogParsedLogData log = new TomcatAccessLogParsedLogData(
        true,
        "127.0.0.1",
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        "GET", // not allowed
        "/index.html",
        "HTTP/1.1",
        200,
        1234,
        "-",
        "JUnit",
        "-",
        "Asia/Seoul"
    );

    assertFalse(filter.accept(log));
  }

  @Test
  void testAccept_whenAllowedMethodsEmpty() {
    FilterConfig config = new FilterConfig(
        Set.of(),
        Set.of(),
        Set.of() // empty means allow all
    );
    TomcatAccessLogFilter filter = new TomcatAccessLogFilter(config);

    TomcatAccessLogParsedLogData log = new TomcatAccessLogParsedLogData(
        true,
        "127.0.0.1",
        Instant.now().truncatedTo(ChronoUnit.SECONDS),
        "DELETE",
        "/test",
        "HTTP/1.1",
        404,
        0,
        "-",
        "JUnit",
        "-",
        "Asia/Seoul"
    );

    assertTrue(filter.accept(log));
  }
}