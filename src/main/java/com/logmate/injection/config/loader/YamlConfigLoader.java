package com.logmate.injection.config.loader;

import com.logmate.injection.config.WatcherConfig;
import java.io.InputStream;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YamlConfigLoader {

  public static WatcherConfig load(String path) {
    Yaml yaml = new Yaml(new Constructor(WatcherConfig.class, new LoaderOptions()));
    InputStream inputStream = YamlConfigLoader.class
        .getClassLoader()
        .getResourceAsStream(path);
    return yaml.load(inputStream);
  }
}
