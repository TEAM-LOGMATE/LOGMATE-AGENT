package com.logmate.bootstrap.config.loader;

import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.PullerConfig;

public interface ConfigLoader {
  PullerConfig loadPullerConfig();
  AgentConfig loadAgentConfig();
}
