package com.logmate.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExporterConfig {

  private String pushURL;
  private Boolean compressEnabled;
  private int retryIntervalSec;
  private int maxRetryCount;
}
