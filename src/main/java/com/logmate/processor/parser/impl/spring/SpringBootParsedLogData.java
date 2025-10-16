package com.logmate.processor.parser.impl.spring;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import com.logmate.processor.parser.ParsedLogData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class SpringBootParsedLogData implements ParsedLogData {
  @JsonProperty("formatCorrect")
  private boolean isFormatCorrect;
  private Instant timestamp;
  private String level;
  private String thread;
  private String logger;
  private String message;
  private String userTimezone;

  @Override
  public String getMessage() {
    return message;
  }
}
