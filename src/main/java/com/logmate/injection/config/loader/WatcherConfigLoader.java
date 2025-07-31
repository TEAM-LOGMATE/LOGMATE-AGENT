package com.logmate.injection.config.loader;

import com.logmate.injection.config.WatcherConfig;
import java.io.InputStream;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class WatcherConfigLoader {

  private static String defaultConfigPath = "default-config.yml";

  public static WatcherConfig loadDefaultConfig() {
    InputStream inputStream = WatcherConfigLoader.class
        .getClassLoader()
        .getResourceAsStream(defaultConfigPath);

    if (inputStream == null) {
      throw new RuntimeException("Default config file not found.");
    }

    Yaml yaml = new Yaml(new Constructor(WatcherConfig.class, new LoaderOptions()));
    return yaml.load(inputStream);
  }
}
