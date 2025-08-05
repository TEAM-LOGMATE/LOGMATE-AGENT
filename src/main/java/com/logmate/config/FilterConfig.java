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

  // Spring boot 전용 필터 항목
  private Set<String> allowedLevels;
  private Set<String> allowedLoggers;
  private Set<String> requiredKeywords;
  private LocalDateTime after;

  // 웹로그 전용 필터 항목
  private Set<String> allowedMethods;       // GET, POST 등
  private Set<Integer> allowedStatusCodes;  // 200, 500 등
  private Set<String> urlPrefix;                 // 특정 경로로 시작하는 URL만 허용
}