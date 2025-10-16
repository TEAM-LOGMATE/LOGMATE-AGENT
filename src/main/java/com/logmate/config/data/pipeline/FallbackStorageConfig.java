package com.logmate.config.data.pipeline;

import com.logmate.processor.parser.LogType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FallbackStorageConfig {
  private String filePath;
  private LogType logType;
}
