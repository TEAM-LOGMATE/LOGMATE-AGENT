package com.logmate.config.holder;

import com.logmate.config.AgentConfig;
import com.logmate.config.validator.ConfigValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentConfigHolder {

  private static AgentConfig agentConfig;

  public static AgentConfig get() {
    return agentConfig;
  }

  public static boolean update(AgentConfig agentConfig) {
    try {
      ConfigValidator.validate(agentConfig);
    } catch (IllegalArgumentException e) {
      log.error("[AgentConfigHolder] Invalid agent config: {}", e.getMessage());
      return false;
    }

    AgentConfigHolder.agentConfig = agentConfig;
    return true;
  }
}
