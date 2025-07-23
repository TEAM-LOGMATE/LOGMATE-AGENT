package com.logmate.exporter.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logmate.exporter.LogExporter;
import com.logmate.parser.ParsedLogData;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpLogExporter implements LogExporter {
  private final String pushURL;

  public HttpLogExporter(String pushURL) {
    this.pushURL = pushURL;
  }

  @Override
  public void export(List<ParsedLogData> logDataList) {
    try {
      URL url = new URL(pushURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Type", "application/json");

      ObjectMapper mapper = new ObjectMapper();
      String jsonBody = mapper.writeValueAsString(logDataList);

      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }
      System.out.println("log-mate log push response code: " + conn.getResponseCode());
      //conn.connect();
      conn.disconnect();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
