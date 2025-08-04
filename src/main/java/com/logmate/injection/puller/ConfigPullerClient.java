package com.logmate.injection.puller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigPullerClient {

  private final ObjectMapper objectMapper;

  public ConfigPullerClient() {
    this.objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  public Optional<TokenDTO> authenticationRequest(String requestURL, AuthenticationRequestDTO requestDTO) {
    try {
      // Request Setting
      URL url = new URL(requestURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setConnectTimeout(3000);
      conn.setReadTimeout(3000);

      conn.setDoOutput(true);
      String jsonBody = objectMapper.writeValueAsString(requestDTO); // DTO → JSON 변환
      try (OutputStream os = conn.getOutputStream()) {
        os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        os.flush();
      }
      // Response Mapping
      int responseCode = conn.getResponseCode();
      if (responseCode != 200) {
        log.error("[ConfigPullerClient] Failed Agent Authentication: {}", responseCode);
        return Optional.empty();
      }
      log.debug("[ConfigPullerClient] Authentication Request Success - code: {}", responseCode);
      // Json to WatcherConfig
      StringBuilder json = new StringBuilder();
      try (Scanner scanner = new Scanner(conn.getInputStream())) {
        while (scanner.hasNextLine()) {
          json.append(scanner.nextLine());
        }
      }

      return Optional.of(objectMapper.readValue(json.toString(), TokenDTO.class));
    } catch (MalformedURLException e) {
      log.error("[ConfigPullerClient] Invalid Request URL: {}", requestURL);
      return Optional.empty();
    } catch (IOException e) {
      log.error("[ConfigPullerClient] Failed Authentication from {}", requestURL);
      log.error("[ConfigPullerClient] Exception: {}", e.getMessage());
      return Optional.empty();
    }

  }

  public Optional<ConfigDTO> configPull(String requestURL, String accessToken) {
    try {
      // Request Setting
      URL url = new URL(requestURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", "Bearer " + accessToken);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setConnectTimeout(3000);
      conn.setReadTimeout(3000);

      // Response Mapping
      int responseCode = conn.getResponseCode();
      if (responseCode == 304) {
        log.info("[ConfigPullerClient] Pull succeeded with response code (Not Modified): {}", responseCode);
        return Optional.empty();
      }
      else if (responseCode != 200) {
        log.error("[ConfigPullerClient] Pull failed with response code: {}", responseCode);
        return Optional.empty();
      }
      log.debug("[ConfigPullerClient] Config pull Request Success - code: {}", responseCode);
      // Json to WatcherConfig
      StringBuilder json = new StringBuilder();
      try (Scanner scanner = new Scanner(conn.getInputStream())) {
        while (scanner.hasNextLine()) {
          json.append(scanner.nextLine());
        }
      }

      return Optional.of(objectMapper.readValue(json.toString(), ConfigDTO.class));
    } catch (MalformedURLException e) {
      log.error("[ConfigPullerClient] Invalid pull URL: {}", requestURL);
      return Optional.empty();
    } catch (IOException e) {
      log.error("[ConfigPullerClient] Failed to pull config from {}", requestURL);
      log.error("[ConfigPullerClient] Exception: {}", e.getMessage());
      return Optional.empty();
    }
  }

}
