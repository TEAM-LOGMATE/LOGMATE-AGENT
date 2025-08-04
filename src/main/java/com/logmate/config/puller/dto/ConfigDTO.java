package com.logmate.config.puller.dto;

import com.logmate.config.AgentConfig;
import com.logmate.config.PullerConfig;
import com.logmate.config.LogPiplineConfig;
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
