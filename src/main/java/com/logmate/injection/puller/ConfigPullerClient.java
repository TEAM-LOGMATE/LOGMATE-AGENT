package com.logmate.injection.puller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logmate.injection.config.WatcherConfig;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ConfigPullerClient {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public Optional<ConfigDTO> pull(String requestURL, String accessToken) {
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
        log.info("Pull succeeded with response code (Not Modified): {}", responseCode);
        return Optional.empty();
      }
      else if (responseCode != 200) {
        log.error("Pull failed with response code: {}", responseCode);
        return Optional.empty();
      }

      // Json to WatcherConfig
      StringBuilder json = new StringBuilder();
      try (Scanner scanner = new Scanner(conn.getInputStream())) {
        while (scanner.hasNextLine()) {
          json.append(scanner.nextLine());
        }
      }

      return Optional.of(objectMapper.readValue(json.toString(), ConfigDTO.class));
    } catch (MalformedURLException e) {
      log.error("Invalid pull URL: {}", requestURL);
      return Optional.empty();
    } catch (IOException e) {
      log.error("Failed to pull config from {}", requestURL);
      return Optional.empty();
    }
  }

}
