package com.logmate.config.holder;

import com.logmate.config.PullerConfig;
import com.logmate.config.validator.ConfigValidator;
import com.logmate.config.loader.YamlConfigLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PullerConfigHolder {

  private static PullerConfig pullerConfig = createDefault();

  public static PullerConfig get() {
    return pullerConfig;
  }

  private static PullerConfig createDefault() {
    PullerConfig load = YamlConfigLoader.loadPullerConfig();
    log.info("Default puller config loaded.{}", load);
    return load;
  }

  public static boolean update(PullerConfig pullerConfig) {
    try {
      ConfigValidator.validate(pullerConfig);
    } catch (IllegalArgumentException e) {
      log.error("[PullerConfigHolder] Invalid puller config: {}", e.getMessage());
      return false;
    }

    PullerConfigHolder.pullerConfig = pullerConfig;
    return true;
  }
}
