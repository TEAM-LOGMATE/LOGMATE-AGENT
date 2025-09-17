package com.logmate.bootstrap.config.loader.impl;

import com.logmate.bootstrap.config.loader.ConfigLoader;
import com.logmate.config.AgentConfig;
import com.logmate.config.PullerConfig;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@RequiredArgsConstructor
public class YamlConfigLoader implements ConfigLoader {

  public final String PULLER_CONFIG_FILE_NAME;
  public final String AGENT_CONFIG_FILE_NAME;

  public YamlConfigLoader() {
    PULLER_CONFIG_FILE_NAME = "default-puller-config.yml";
    AGENT_CONFIG_FILE_NAME = "default-agent-config.yml";
  }

  public PullerConfig loadPullerConfig() {
    Yaml yaml = new Yaml(new Constructor(PullerConfig.class, new LoaderOptions()));
    InputStream inputStream = YamlConfigLoader.class
        .getClassLoader()
        .getResourceAsStream(PULLER_CONFIG_FILE_NAME);
    return yaml.load(inputStream);
  }

  public AgentConfig loadAgentConfig() {
    Yaml yaml = new Yaml(new Constructor(AgentConfig.class, new LoaderOptions()));
    InputStream inputStream = YamlConfigLoader.class
        .getClassLoader()
        .getResourceAsStream(AGENT_CONFIG_FILE_NAME);
    return yaml.load(inputStream);
  }
}
