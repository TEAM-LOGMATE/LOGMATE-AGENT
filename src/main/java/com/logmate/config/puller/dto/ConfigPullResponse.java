package com.logmate.config.puller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigPullResponse {
  private ConfigDTO body;
  private Integer status;
}
