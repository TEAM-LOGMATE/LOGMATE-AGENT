package com.logmate.bootstrap.config;

import com.logmate.bootstrap.args.AgentArguments;
import com.logmate.bootstrap.config.loader.ConfigLoader;
import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.PullerConfig;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.PullerConfigHolder;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigInitializer {

  private final ConfigLoader configLoader;

  @Inject
  public ConfigInitializer(ConfigLoader configLoader) {
    this.configLoader = configLoader;
  }

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
