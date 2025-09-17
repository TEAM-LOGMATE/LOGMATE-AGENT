package com.logmate.bootstrap.config.loader;

import com.logmate.config.AgentConfig;
import com.logmate.config.PullerConfig;

public interface ConfigLoader {
  PullerConfig loadPullerConfig();
  AgentConfig loadAgentConfig();
}
