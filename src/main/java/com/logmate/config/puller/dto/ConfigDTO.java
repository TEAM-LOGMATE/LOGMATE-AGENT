package com.logmate.config.puller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigDTO {
  private String etag;
  private AgentConfigDto agentConfig;
  private PullerConfigDto pullerConfig;
  private List<LogPipelineConfigDto> logPipelineConfigs;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AgentConfigDto {
    private String agentId;
    private String accessToken;
    private String etag;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class PullerConfigDto {
    private String pullURL;
    private Integer intervalSec;
    private String etag;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class LogPipelineConfigDto {
    private String etag;
    private Integer thNum;
    private TailerConfigDto tailer;
    private MultilineConfigDto multiline;
    private ExporterConfigDto exporter;
    private ParserConfigDto parser;
    private FilterConfigDto filter;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TailerConfigDto {
      private String filePath;
      private Integer readIntervalMs;
      private String metaDataFilePathPrefix;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true) // JSON에 없는 값은 무시
    public static class MultilineConfigDto {
      private Boolean enabled;
      private Integer maxLines;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExporterConfigDto {
      private String pushURL;
      private Boolean compressEnabled;
      private Integer retryIntervalSec;
      private Integer maxRetryCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ParserConfigDto {
      private String type;
      private ParserConfigDetailDto config;

      @Data
      @NoArgsConstructor
      @AllArgsConstructor
      @Builder
      public static class ParserConfigDetailDto {
        private String timezone;
      }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true) // JSON에 없는 값은 무시
    public static class FilterConfigDto {
      private Set<String> allowedMethods;
      private Set<String> allowedLevels;
      private Set<String> allowedKeywords;
    }
  }

}
