package com.logmate.config.puller.dto;

import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.PullerConfig;
import com.logmate.config.data.pipeline.LogPiplineConfig;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfigDTO {
  private String etag;
  private AgentConfig agentConfig;
  private PullerConfig pullerConfig;
  private List<LogPiplineConfig> logPiplineConfigs;
}
