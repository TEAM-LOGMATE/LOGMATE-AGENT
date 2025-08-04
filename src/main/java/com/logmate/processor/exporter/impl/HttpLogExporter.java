package com.logmate.processor.exporter.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logmate.config.AgentConfig;
import com.logmate.config.ExporterConfig;
import com.logmate.processor.exporter.LogExporter;
import com.logmate.processor.parser.ParsedLogData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HttpLogExporter implements LogExporter {

  private final ExporterConfig exporterConfig;
  private final AgentConfig agentConfig;
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void export(List<ParsedLogData> logDataList) {
    String jsonBody;
    try {
      jsonBody = mapper.writeValueAsString(logDataList);
    } catch (IOException e) {
      log.error("Failed to serialize log data", e);
      return;
    }

    byte[] payload = exporterConfig.getCompressEnabled()
        ? compress(jsonBody)
        : jsonBody.getBytes(StandardCharsets.UTF_8);

    int attempt = 0;
    while (attempt <= exporterConfig.getMaxRetryCount()) {
      try {
        HttpURLConnection conn = (HttpURLConnection) new URL(exporterConfig.getPushURL()).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + agentConfig.getAccessToken());
        conn.setRequestProperty("Content-Type", "application/json");
        if (exporterConfig.getCompressEnabled()) {
          conn.setRequestProperty("Content-Encoding", "gzip");
        }

        try (OutputStream os = conn.getOutputStream()) {
          os.write(payload);
        }

        int responseCode = conn.getResponseCode();
        log.debug("log push response code: {}", responseCode);
        if (responseCode >= 200 && responseCode < 300) {
          log.debug("log push succeeded");
          break; // 성공
        }
        log.debug("log push failed");
        conn.disconnect();
        attempt++;
        Thread.sleep(exporterConfig.getRetryIntervalSec() * 1000L);
      } catch (IOException | InterruptedException e) {
        attempt++;
        if (attempt > exporterConfig.getMaxRetryCount()) {
          log.error("Failed to push logs after retries", e);
        }
      }
    }
    log.error("Failed to push logs after retries");
  }

  private byte[] compress(String input) {
    try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
      gzipStream.write(input.getBytes(StandardCharsets.UTF_8));
      gzipStream.close();
      return byteStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Failed to compress log data", e);
    }
  }
}
