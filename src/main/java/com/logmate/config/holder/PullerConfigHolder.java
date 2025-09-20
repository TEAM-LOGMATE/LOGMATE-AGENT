package com.logmate.config.holder;

import com.logmate.config.data.PullerConfig;
import com.logmate.config.validator.ConfigValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PullerConfigHolder {

  private static PullerConfig pullerConfig;

  public static PullerConfig get() {
    return pullerConfig;
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
