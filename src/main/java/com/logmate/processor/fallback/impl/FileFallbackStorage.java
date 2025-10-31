package com.logmate.processor.fallback.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logmate.config.data.pipeline.FallbackStorageConfig;
import com.logmate.processor.fallback.FallbackStorage;
import com.logmate.processor.parser.LogType;
import com.logmate.processor.parser.ParsedLogData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileFallbackStorage implements FallbackStorage {

  private final File file;
  private final ObjectMapper mapper;
  private final LogType logType;

  public FileFallbackStorage(FallbackStorageConfig config) {
    this.logType = config.getLogType();
    this.file = new File(config.getFilePath());
    this.mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    File parentDir = file.getParentFile();
    if (parentDir != null && !parentDir.exists()) {
      parentDir.mkdirs();
    }
  }

  @Override
  public void save(List<ParsedLogData> logs) {
    try (FileWriter fw = new FileWriter(file, true)) {
      for (ParsedLogData logData : logs) {
        String line = mapper.writeValueAsString(logData);
        fw.write(line + System.lineSeparator());
      }
    } catch (IOException e) {
      log.error("[save] Failed to save logs", e);
    }
  }

  @Override
  public List<ParsedLogData> loadAll() {
    if (!file.exists()) return Collections.emptyList();
    List<ParsedLogData> logs = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.isBlank()) continue;
        try {
          ParsedLogData logData = mapper.readValue(line, logType.getLogDataClass());
          logs.add(logData);
        } catch (Exception e) {
          log.warn("[loadAll] Skipped invalid line: {}", line, e);
        }
      }
    } catch (IOException e) {
      log.error("[loadAll] Failed to load logs", e);
    }
    return logs;
  }

  @Override
  public void clear() {
    if (file.exists() && file.delete()) {
      log.debug("[clear] Cleared {}", file.getAbsolutePath());
    }
  }
}

