package com.logmate.injection.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatcherConfig {

  private String agentId;
  private String accessToken;
  private String etag;
  private Integer thNum;
  private PullerConfig puller;
  private TailerConfig tailer;
  private ExporterConfig exporter;
  private ParserConfig parser;
  private FilterConfig filter;
}
