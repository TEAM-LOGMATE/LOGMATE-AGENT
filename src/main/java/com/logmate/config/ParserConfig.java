package com.logmate.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParserConfig {

  private String type;
  private ParserDetailConfig config;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ParserDetailConfig {

    private String timestampPattern;
    private String timezone;
  }
}