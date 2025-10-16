package com.logmate.config.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentConfig {

  private String agentId;
  private String accessToken;
  private String etag;
}
