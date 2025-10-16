package com.logmate.config.data.pipeline;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterConfig {

  // Spring boot 전용 필터 항목
  private Set<String> allowedLevels = new HashSet<>();
  private Set<String> requiredKeywords = new HashSet<>();

  // 웹로그 전용 필터 항목
  private Set<String> allowedMethods = new HashSet<>();       // GET, POST 등
}