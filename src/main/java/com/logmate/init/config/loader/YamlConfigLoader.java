package com.logmate.init.config.loader;

import com.logmate.config.AgentConfig;
import com.logmate.config.PullerConfig;
import java.io.InputStream;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YamlConfigLoader {

  public static PullerConfig loadPullerConfig() {
    Yaml yaml = new Yaml(new Constructor(PullerConfig.class, new LoaderOptions()));
    InputStream inputStream = YamlConfigLoader.class
        .getClassLoader()
        .getResourceAsStream("default-puller-config.yml");
    return yaml.load(inputStream);
  }

  public static AgentConfig loadAgentConfig() {
    Yaml yaml = new Yaml(new Constructor(AgentConfig.class, new LoaderOptions()));
    InputStream inputStream = YamlConfigLoader.class
        .getClassLoader()
        .getResourceAsStream("default-agent-config.yml");
    return yaml.load(inputStream);
  }
}
