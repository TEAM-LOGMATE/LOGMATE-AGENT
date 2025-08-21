package com.logmate.processor.parser.impl.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.logmate.processor.parser.ParsedLogData;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TomcatAccessLogParsedLogData implements ParsedLogData {
  @JsonProperty("formatCorrect")
  private boolean isFormatCorrect;
  private String ip;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime timestamp;
  private String method;
  private String url;
  private String protocol;
  private int statusCode;
  private int responseSize;
  private String referer;
  private String userAgent;
  private String extra;

  @Override
  public String getMessage() {
    return String.format(
        "%s -- [%s] \"%s %s %s\" %d %d \"%s\" \"%s\" %s",
        ip,
        timestamp,
        method,
        url,
        protocol,
        statusCode,
        responseSize,
        referer,
        userAgent,
        extra
    );
  }

  @Override
  public String toString() {
    return "{" +
        "\"isFormatCorrect\":" + isFormatCorrect +
        ",\"ip\":\"" + ip + '\"' +
        ",\"timestamp\":\"" + timestamp + '\"' +
        ",\"method\":\"" + method + '\"' +
        ",\"url\":\"" + url + '\"' +
        ",\"protocol\":\"" + protocol + '\"' +
        ",\"statusCode\":" + statusCode +
        ",\"responseSize\":" + responseSize +
        ",\"referer\":\"" + referer + '\"' +
        ",\"userAgent\":\"" + userAgent + '\"' +
        ",\"extra\":\"" + extra + '\"' +
        '}';
  }
}
