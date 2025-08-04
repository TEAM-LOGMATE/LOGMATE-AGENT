package com.logmate.config.validator;

import com.logmate.config.AgentConfig;
import com.logmate.config.ExporterConfig;
import com.logmate.config.FilterConfig;
import com.logmate.config.MultilineConfig;
import com.logmate.config.ParserConfig;
import com.logmate.config.PullerConfig;
import com.logmate.config.TailerConfig;
import com.logmate.config.LogPiplineConfig;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConfigValidator {

  public static void validate(AgentConfig config) {
    if (config == null) {
      throw new IllegalArgumentException("AgentConfig is null.");
    }

    if (isNullOrBlank(config.getAgentId())) {
      throw new IllegalArgumentException("agent.agent-id must not be empty.");
    }

    if (isNullOrBlank(config.getAccessToken())) {
      throw new IllegalArgumentException("agent.accessToken must not be empty.");
    }

    if (isNullOrBlank(config.getEtag())) {
      throw new IllegalArgumentException("agent.eTag must not be empty.");
    }
  }

  public static void validate(PullerConfig config) {
    if (config == null) {
      throw new IllegalArgumentException("config-puller section is missing.");
    }

    if (isNullOrBlank(config.getPullURL()) || !validateURL(config.getPullURL())) {
      throw new IllegalArgumentException("config-puller.pullURL must be a valid URL.");
    }

    if (config.getIntervalSec() <= 10) {
      throw new IllegalArgumentException("config-puller.intervalSec must be greater than 10.");
    }
  }

  public static void validate(LogPiplineConfig config) {
    if (config == null) {
      throw new IllegalArgumentException("WatcherConfig is null.");
    }

    validateTailer(config.getTailer());
    validateExporter(config.getExporter());
    validateParser(config.getParser());
    validateFilter(config.getFilter());
    validateMultiline(config.getMultiline());
  }

  private static void validateTailer(TailerConfig tailer) {
    if (tailer == null) {
      throw new IllegalArgumentException("tailer section is missing.");
    }

    if (!validateFilePath(tailer.getFilePath())) {
      throw new IllegalArgumentException("tailer.filePaths must be a valid file path list.");
    }

    if (tailer.getReadIntervalMs() <= 500) {
      throw new IllegalArgumentException("tailer.readIntervalMs must be greater than 500 Ms.");
    }
  }

  private static void validateMultiline(MultilineConfig multi) {
    if (multi != null && multi.isEnabled()) {
      if (multi.getMaxLines() <= 0) {
        throw new IllegalArgumentException("multiline.maxLines must be greater than 0.");
      }
    }
  }

  private static void validateExporter(ExporterConfig exporter) {
    if (exporter == null) {
      throw new IllegalArgumentException("exporter section is missing.");
    }

    if (isNullOrBlank(exporter.getPushURL()) || !validateURL(exporter.getPushURL())) {
      throw new IllegalArgumentException("exporter.pushURL must be a valid URL.");
    }

    if (isNull(exporter.getCompressEnabled())) {
      throw new IllegalArgumentException("exporter.compressEnabled must not be empty.");
    }

    if (exporter.getRetryIntervalSec() < 0 || exporter.getMaxRetryCount() < 0) {
      throw new IllegalArgumentException(
          "exporter.retryIntervalSec and maxRetryCount must be non-negative.");
    }
  }

  private static void validateParser(ParserConfig parser) {
    if (parser == null) {
      throw new IllegalArgumentException("parser section is missing.");
    }

    if (isNullOrBlank(parser.getType())) {
      throw new IllegalArgumentException("parser.type must not be empty.");
    }

    ParserConfig.ParserDetailConfig detail = parser.getConfig();
    if (detail == null || isNullOrBlank(detail.getTimestampPattern()) || isNullOrBlank(
        detail.getTimezone())) {
      throw new IllegalArgumentException(
          "parser.config.timestampPattern and timezone must not be empty.");
    }
  }

  private static void validateFilter(FilterConfig filter) {
    if (filter == null) {
      throw new IllegalArgumentException("filter section is missing.");
    }

  }

  private static boolean isNullOrBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  private static boolean isNull(Integer i) {
    return i == null;
  }

  private static boolean isNull(Boolean compressEnabled) {
    return compressEnabled == null;
  }

  public static boolean validateURL(String url) {
    if (url == null || url.isBlank()) {
      return false;
    }

    try {
      new URL(url); // java.net.URL 클래스
    } catch (MalformedURLException e) {
      return false;
    }
    return true;
  }

  public static boolean validateFilePaths(List<String> filePaths) {
    if (filePaths == null || filePaths.isEmpty()) {
      return false;
    }

    for (String path : filePaths) {
      if (path == null || path.isBlank()) {
        return false;
      }

      Path filePath = Paths.get(path);

      // 금지 패턴
      if (path.contains("*")) {
        return false;
      }
    }
    return true;
  }

  public static boolean validateFilePath(String filePath) {
    if (filePath == null || filePath.isEmpty()) {
      return false;
    }

    Path path = Paths.get(filePath);

    // 금지 패턴
    if (filePath.contains("*")) {
      return false;
    }

    return true;
  }
}
