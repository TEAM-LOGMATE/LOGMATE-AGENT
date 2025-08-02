package com.logmate.injection.config.util;

import com.logmate.injection.config.ExporterConfig;
import com.logmate.injection.config.FilterConfig;
import com.logmate.injection.config.ParserConfig;
import com.logmate.injection.config.PullerConfig;
import com.logmate.injection.config.TailerConfig;
import com.logmate.injection.config.WatcherConfig;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class WatcherConfigValidator {

  public static void validate(WatcherConfig config) {
    if (config == null) {
      throw new IllegalArgumentException("WatcherConfig is null.");
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

    if (isNull(config.getThNum())) {
      throw new IllegalArgumentException("agent.tNum must not be empty.");
    }

    validateTailer(config.getTailer());
    validateExporter(config.getExporter());
    validateParser(config.getParser());
    validateFilter(config.getFilter());
    validatePuller(config.getPuller());
  }

  private static void validateTailer(TailerConfig tailer) {
    if (tailer == null) {
      throw new IllegalArgumentException("tailer section is missing.");
    }

    if (!validateFilePaths(tailer.getFilePaths())) {
      throw new IllegalArgumentException("tailer.filePaths must be a valid file path list.");
    }

    if (tailer.getReadIntervalMs() <= 500) {
      throw new IllegalArgumentException("tailer.readIntervalMs must be greater than 500 Ms.");
    }

    TailerConfig.MultilineConfig multi = tailer.getMultiline();
    if (multi != null && multi.isEnabled()) {
      if (isNullOrBlank(multi.getPattern())) {
        throw new IllegalArgumentException(
            "tailer.multiline.pattern must not be empty when multiline is enabled.");
      }
      if (multi.getTimeoutMs() <= 0) {
        throw new IllegalArgumentException("tailer.multiline.timeoutMs must be greater than 0.");
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

    ParserConfig.FallbackConfig fallback = parser.getFallback();
    if (fallback != null && isNullOrBlank(fallback.getUnstructuredTag())) {
      throw new IllegalArgumentException(
          "parser.fallback.unstructuredTag must not be empty if provided.");
    }
  }

  private static void validateFilter(FilterConfig filter) {
    if (filter == null) {
      throw new IllegalArgumentException("filter section is missing.");
    }

    if (isNullOrBlank(filter.getType())) {
      throw new IllegalArgumentException("filter.type must not be empty.");
    }

    List<String> rules = filter.getRules();
    if (rules == null || rules.isEmpty()) {
      throw new IllegalArgumentException("filter.rules must not be empty.");
    }
  }

  private static void validatePuller(PullerConfig puller) {
    if (puller == null) {
      throw new IllegalArgumentException("config-puller section is missing.");
    }

    if (isNullOrBlank(puller.getPullURL()) || !validateURL(puller.getPullURL())) {
      throw new IllegalArgumentException("config-puller.pullURL must be a valid URL.");
    }

    if (puller.getIntervalSec() <= 10) {
      throw new IllegalArgumentException("config-puller.intervalSec must be greater than 10.");
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
}
