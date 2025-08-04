package com.logmate.processor.parser.impl.spring;


import java.time.LocalDateTime;
import com.logmate.processor.parser.ParsedLogData;
import lombok.Getter;

@Getter
public class SpringBootParsedLogData implements ParsedLogData {

  private boolean isFormatCorrect;
  private LocalDateTime timestamp;
  private String level;
  private String thread;
  private String logger;
  private String message;
  private String tag;

  public SpringBootParsedLogData(boolean isFormatCorrect, LocalDateTime timestamp, String level,
      String thread,
      String logger,
      String message,
      String tag) {
    this.isFormatCorrect = isFormatCorrect;
    this.timestamp = timestamp;
    this.level = level;
    this.thread = thread;
    this.logger = logger;
    this.message = message;
    this.tag = tag;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "{" +
        "\"isFormatCorrect\":" + isFormatCorrect +
        ",\"timestamp\":\"" + timestamp + '\"' +
        ",\"level\":\"" + level + '\"' +
        ",\"thread\":\"" + thread + '\"' +
        ",\"logger\":\"" + logger + '\"' +
        ",\"message\":\"" + message + '\"' +
        ",\"tag\":\"" + tag + '\"' +
        '}';
  }
}
