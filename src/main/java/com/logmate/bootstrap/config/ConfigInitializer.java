package com.logmate.bootstrap.config;

import com.logmate.bootstrap.args.AgentArguments;
import com.logmate.bootstrap.config.loader.ConfigLoader;
import com.logmate.config.AgentConfig;
import com.logmate.config.PullerConfig;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.PullerConfigHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ConfigInitializer {
  private final ConfigLoader configLoader;

  public void init(AgentArguments agentArguments) {
    log.info("[ConfigInitializer] Configuration initialize started.");
    initAgentConfig(agentArguments.getAgentId());
    initPullerConfig();
    log.info("[ConfigInitializer] Configuration initialize completed.");
  }

  private void initAgentConfig(String agentId) {
    AgentConfig config = configLoader.loadAgentConfig();
    config.setAgentId(agentId);

    AgentConfigHolder.update(config);
    log.info("[ConfigInitializer] AgentConfig initialized: {}", config);
  }

  private void initPullerConfig() {
    PullerConfig config = configLoader.loadPullerConfig();

    PullerConfigHolder.update(config);
    log.info("[ConfigInitializer] PullerConfig initialized: {}", config);
  }
}
