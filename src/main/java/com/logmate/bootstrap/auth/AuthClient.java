package com.logmate.bootstrap.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logmate.config.puller.dto.TokenDTO;
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
public class AuthClient {

  private final ObjectMapper objectMapper;

  public AuthClient() {
    this.objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  public Optional<TokenDTO> request(String requestURL,
      AuthenticationRequestDTO requestDTO) {
    try {
      // Request Setting
      URL url = new URL(requestURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setConnectTimeout(3000);
      conn.setReadTimeout(3000);
      conn.setDoOutput(true);

      // Request
      String jsonBody = objectMapper.writeValueAsString(requestDTO); // DTO → JSON 변환
      try (OutputStream os = conn.getOutputStream()) {
        os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        os.flush();
      }

      // Response Mapping
      int responseCode = conn.getResponseCode();
      if (responseCode != 200) {
        log.error("[AuthClient] Failed Agent Authentication: {}", responseCode);
        return Optional.empty();
      }

      log.debug("[AuthClient] Authentication Request Success - code: {}", responseCode);
      StringBuilder json = new StringBuilder();
      try (Scanner scanner = new Scanner(conn.getInputStream())) {
        while (scanner.hasNextLine()) {
          json.append(scanner.nextLine());
        }
      }

      return Optional.of(objectMapper.readValue(json.toString(), TokenDTO.class));
    } catch (MalformedURLException e) {
      log.error("[AuthClient] Invalid Request URL: {}", requestURL);
      return Optional.empty();
    } catch (IOException e) {
      log.error("[AuthClient] Failed Authentication from {}", requestURL);
      log.error("[AuthClient] Exception: {}", e.getMessage());
      return Optional.empty();
    }

  }
}
