package com.logmate.init.config;

import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.PullerConfigHolder;
import com.logmate.init.config.loader.YamlConfigLoader;

public class ConfigInitializer {
  private YamlConfigLoader yamlConfigLoader;

  public void init() {
    AgentConfigHolder.update(yamlConfigLoader.loadAgentConfig());
    PullerConfigHolder.update(yamlConfigLoader.loadPullerConfig());
  }
}
