package com.logmate.injection.puller;

import com.logmate.injection.config.AgentConfig;
import com.logmate.injection.config.PullerConfig;
import com.logmate.injection.config.WatcherConfig;
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
  private List<WatcherConfig> watcherConfigs;
}
