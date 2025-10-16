package com.logmate.config.puller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logmate.config.puller.dto.ConfigDTO;
import com.logmate.config.puller.dto.ConfigPullResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigPullClient {

  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(30))
      .build();

  private final ObjectMapper objectMapper;

  public ConfigPullClient() {
    this.objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  public ConfigPullResponse configPull(String requestURL, String accessToken) {
    try {
      // === 1. 요청 객체 생성 ===
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(requestURL))
          .GET()
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/json")
          .timeout(Duration.ofSeconds(30))
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      log.debug("[ConfigPullClient] Pulling config from {}", response.body());

      int responseCode = response.statusCode();
      if (responseCode == 304) {
        log.info("[ConfigPullClient] Pull succeeded with response code (Not Modified): {}", responseCode);
        return new ConfigPullResponse(null, responseCode);
      }
      else if (responseCode == 401) {
        log.error("[ConfigPullClient] Pull failed with response code (Unauthorized): {}", responseCode);
        return new ConfigPullResponse(null, responseCode);
      }
      else if (responseCode != 200) {
        log.error("[ConfigPullClient] Pull failed with response code: {}", responseCode);
        return new ConfigPullResponse(null, responseCode);
      }
      log.debug("[ConfigPullClient] Pull succeeded with response code (OK): {}", responseCode);

      // ConfigPullResponse 객체로 변환하여 Return
      return new ConfigPullResponse(
          objectMapper.readValue(response.body(), ConfigDTO.class), responseCode);
    } catch (MalformedURLException e) {
      log.error("[ConfigPullClient] Invalid pull URL: {}", requestURL);
      throw new RuntimeException(e);
    } catch (IOException e) {
      log.error("[ConfigPullClient] Failed to pull config from {}", requestURL);
      log.error("[ConfigPullClient] Exception: {}", e.getMessage());
      return new ConfigPullResponse(null, 500);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
