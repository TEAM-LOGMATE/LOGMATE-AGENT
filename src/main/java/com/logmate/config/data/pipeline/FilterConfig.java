package com.logmate.config.data.pipeline;

import java.time.LocalDateTime;
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
  private Set<String> allowedLoggers = new HashSet<>();
  private Set<String> requiredKeywords = new HashSet<>();
  private LocalDateTime after;

  // 웹로그 전용 필터 항목
  private Set<String> allowedMethods = new HashSet<>();       // GET, POST 등
  private Set<Integer> allowedStatusCodes  = new HashSet<>();  // 200, 500 등
  private Set<String> urlPrefix = new HashSet<>();                 // 특정 경로로 시작하는 URL만 허용
}