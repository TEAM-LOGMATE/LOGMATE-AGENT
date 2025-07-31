package com.logmate.injection.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParserConfig {

  private String type;
  private ParserDetailConfig config;
  private FallbackConfig fallback;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ParserDetailConfig {

    private String timestampPattern;
    private String timezone;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FallbackConfig {

    private String unstructuredTag;
  }
}