package com.logmate.processor.fallback.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logmate.config.data.pipeline.FallbackStorageConfig;
import com.logmate.processor.parser.LogType;
import com.logmate.processor.parser.ParsedLogData;
import com.logmate.processor.parser.impl.spring.SpringBootParsedLogData;
import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileFallbackStorageTest {

  private FileFallbackStorage storage;

  @BeforeEach
  void setUp() {
    storage = new FileFallbackStorage(new FallbackStorageConfig("fallback/fallback.log_fallback.jsonl", LogType.SPRINGBOOT));
  }

  @Test
  void testSaveAndLoadAll() {
    // given
    ParsedLogData log1 = new SpringBootParsedLogData(true, Instant.now().truncatedTo(ChronoUnit.SECONDS), "INFO","thread", "logger", "message","Asia/Seoul");
    ParsedLogData log2 = new SpringBootParsedLogData(true, Instant.now().truncatedTo(ChronoUnit.SECONDS), "INFO","thread", "logger", "message2","Asia/Seoul");

    // when
    storage.save(List.of(log1, log2));
    List<ParsedLogData> loaded = storage.loadAll();

    // then
    assertEquals(2, loaded.size());
    assertEquals("message", loaded.get(0).getMessage());
    assertEquals("message2", loaded.get(1).getMessage());
  }

  @Test
  void testClear() {
    // given
    ParsedLogData log1 = new SpringBootParsedLogData(true, Instant.now().truncatedTo(ChronoUnit.SECONDS), "INFO","thread", "logger", "message","Asia/Seoul");
    storage.save(List.of(log1));

    // when
    storage.clear();

    // then
    List<ParsedLogData> loaded = storage.loadAll();
    assertEquals(0, loaded.size());
  }
}