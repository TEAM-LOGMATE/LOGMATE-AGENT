package com.logmate.bootstrap.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logmate.config.holder.PullerConfigHolder;
import com.logmate.config.puller.dto.ConfigDTO.PullerConfigDto;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthClient {

  private final ObjectMapper objectMapper;
  private final HttpClient httpClient;

  public AuthClient() {
    this.httpClient = HttpClient.newHttpClient();
    this.objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  public Optional<LoginResponse> login(String email, String password) {
    String url = PullerConfigHolder.get()
        .getPullURL() + "/users/login";

    try {
      LoginRequest request = new LoginRequest(email, password);
      String requestBody = objectMapper.writeValueAsString(request);

      HttpRequest httpRequest = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("Content-Type", "application/json")
          .timeout(java.time.Duration.ofSeconds(30))
          .POST(HttpRequest.BodyPublishers.ofString(requestBody))
          .build();

      HttpResponse<String> response = httpClient.send(httpRequest,
          HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        log.error("[AuthClient] Authentication Request Success: {}", response.statusCode());
        return Optional.ofNullable(objectMapper.readValue(response.body(), LoginResponse.class));
      } else {
        log.error("[AuthClient] Failed Agent Authentication: {}", response.statusCode());
        throw new RuntimeException("Login failed: " + response.body());
      }

    } catch (MalformedURLException e) {
      log.error("[AuthClient] Invalid Request URL");
      return Optional.empty();
    } catch (IOException e) {
      log.error("[AuthClient] Failed Authentication cause IOException");
      log.error("[AuthClient] Exception: {}", e.getMessage());
      return Optional.empty();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
