package com.logmate.injection.config.util;

import com.logmate.injection.config.AgentConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentConfigHolder {

  private static AgentConfig agentConfig = createDefault();

  public static AgentConfig get() {
    return agentConfig;
  }

  private static AgentConfig createDefault() {
    AgentConfig load = YamlConfigLoader.loadAgentConfig();
    log.info("Default agent config loaded.{}", load);
    return load;
  }

  public static boolean update(AgentConfig agentConfig) {
    try {
      ConfigValidator.validate(agentConfig);
    } catch (IllegalArgumentException e) {
      log.error("Invalid agent config: {}", e.getMessage());
      return false;
    }

    AgentConfigHolder.agentConfig = agentConfig;
    return true;
  }
}
