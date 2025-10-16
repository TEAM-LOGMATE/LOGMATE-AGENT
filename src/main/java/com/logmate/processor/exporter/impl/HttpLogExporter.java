package com.logmate.processor.exporter.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.pipeline.ExporterConfig;
import com.logmate.processor.exporter.LogExporter;
import com.logmate.processor.exporter.util.PayloadBatcher;
import com.logmate.processor.parser.ParsedLogData;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HttpLogExporter implements LogExporter {

  private final PayloadBatcher batcher;
  private final boolean compressEnabled;
  private final int retryIntervalSec;
  private final int maxRetryCount;
  private final String pushUrl;
  private final String accessToken;
  private final ObjectMapper mapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  public HttpLogExporter(ExporterConfig exporterConfig, AgentConfig agentConfig) {
    this.compressEnabled = exporterConfig.getCompressEnabled();
    this.retryIntervalSec = exporterConfig.getRetryIntervalSec();
    this.maxRetryCount = exporterConfig.getMaxRetryCount();
    this.pushUrl = exporterConfig.getPushURL();
    this.accessToken = agentConfig.getAccessToken();
    this.batcher = new PayloadBatcher(mapper, 1024*1024);
  }

  @Override
  public List<ParsedLogData> export(List<ParsedLogData> logDataList) {
    List<ParsedLogData> exportFailLogDataList = new ArrayList<>();
    for (List<ParsedLogData> batch : batcher.split(logDataList)) {
      if (!sendBatch(batch)) {
        exportFailLogDataList.addAll(batch);
      }
    }
    return exportFailLogDataList;
  }

  private boolean sendBatch(List<ParsedLogData> logDataList) {
    String jsonBody;
    try {
      jsonBody = mapper.writeValueAsString(logDataList);
    } catch (IOException e) {
      log.error("[export] Failed to serialize log data", e);
      return false;
    }

    byte[] payload = compressEnabled
        ? compress(jsonBody)
        : jsonBody.getBytes(StandardCharsets.UTF_8);

    int attempt = 0;
    boolean isSuccess = false;
    while (attempt <= maxRetryCount) {
      HttpURLConnection conn = null;
      try {
        conn = (HttpURLConnection) new URL(pushUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "application/json");
        if (compressEnabled) {
          conn.setRequestProperty("Content-Encoding", "gzip");
        }

        try (OutputStream os = conn.getOutputStream()) {
          os.write(payload);
        }

        int responseCode = conn.getResponseCode();
        log.info("[export] log push response code: {}", responseCode);
        if (responseCode >= 200 && responseCode < 300) {
          log.info("[export] log push succeeded");
          isSuccess = true;
          break; // 성공
        }
        log.debug("[export] log push failed");
        attempt++;
        Thread.sleep(retryIntervalSec * 1000L);
      } catch (IOException | InterruptedException e) {
        attempt++;
        if (attempt > maxRetryCount) {
          log.info("[export] Failed to push logs after {} retries",maxRetryCount, e);
        }
      }
      finally {
        if (conn != null) {
          conn.disconnect();
        }
      }
    }
    return isSuccess;
  }

  private byte[] compress(String input) {
    try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
      gzipStream.write(input.getBytes(StandardCharsets.UTF_8));
      gzipStream.close();
      return byteStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("[export] Failed to compress log data", e);
    }
  }
}
