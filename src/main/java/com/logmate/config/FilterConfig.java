package com.logmate.config;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterConfig {

  private Set<String> allowedLevels;
  private Set<String> allowedLoggers;
  private Set<String> requiredKeywords;
  private LocalDateTime after;
}