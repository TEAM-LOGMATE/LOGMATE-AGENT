package com.logmate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logmate.tailer.TailerRunManager;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigInjectionServer {

  private static final Logger log = LoggerFactory.getLogger(ConfigInjectionServer.class);

  public static void start(int port) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/config", exchange -> {
      // CORS 헤더 추가
      exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "");
      exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
      exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

      // OPTIONS 요청은 바로 200 OK 응답
      if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(200, -1);
        return;
      }

      if ("POST".equals(exchange.getRequestMethod())) {
        ObjectMapper mapper = new ObjectMapper();
        WatcherConfig config = mapper.readValue(exchange.getRequestBody(), WatcherConfig.class);
        if (!WatcherConfigHolder.update(config)) {
         log.info("New Configuration is not valid");
          String response = "설정 정보가 유효하지 않습니다.";
          exchange.sendResponseHeaders(403, response.length());
          return;
        }
        TailerRunManager.restart();

        String response = "설정 적용 완료";
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
          os.write(response.getBytes());
        }
      } else {
        log.error("설정 전송 커넥션 중 오류 발생");
        exchange.sendResponseHeaders(405, -1);
      }
    });

    server.setExecutor(null);
    server.start();
    log.info("Config injection server started... port : {}", port);
    log.info("{}/config endpoint is hearing", port);
  }
}
