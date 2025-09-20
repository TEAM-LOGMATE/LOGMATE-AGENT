package com.logmate.config.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PullerConfig {

  private String pullURL;
  private int intervalSec;
  private String etag;
}
