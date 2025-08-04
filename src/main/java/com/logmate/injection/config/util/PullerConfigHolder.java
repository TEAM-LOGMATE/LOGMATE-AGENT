package com.logmate.injection.config.util;

import com.logmate.injection.config.PullerConfig;
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
