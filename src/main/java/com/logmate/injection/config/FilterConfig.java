package com.logmate.injection.config;

import com.logmate.tailer.parser.ParsedLogData;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Predicate;
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