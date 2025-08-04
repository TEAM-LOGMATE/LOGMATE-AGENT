package com.logmate.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultilineConfig {

  private boolean enabled;
  private int maxLines;
}
