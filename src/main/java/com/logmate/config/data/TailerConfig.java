package com.logmate.config.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TailerConfig {

  private String filePath;
  private int readIntervalMs;
  private String metaDataFilePathPrefix;
}
