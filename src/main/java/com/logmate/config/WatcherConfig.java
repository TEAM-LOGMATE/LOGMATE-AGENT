package com.logmate.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatcherConfig {

  private String etag;
  private Integer thNum;
  private TailerConfig tailer;
  private MultilineConfig multiline;
  private ExporterConfig exporter;
  private ParserConfig parser;
  private FilterConfig filter;
}
