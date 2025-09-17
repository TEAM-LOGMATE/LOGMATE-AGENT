package com.logmate.config.puller;

import com.logmate.config.AgentConfig;
import com.logmate.config.PullerConfig;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.PullerConfigHolder;
import com.logmate.config.puller.dto.ConfigDTO;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigPuller implements Runnable {

  private final ConfigPullerClient client;
  private final ConfigUpdater updater;
  private String etag;

  public ConfigPuller(ConfigPullerClient client, ConfigUpdater updater) {
    this.client = client;
    this.updater = updater;
    this.etag = String.valueOf(UUID.randomUUID());
  }

  @Override
  public void run() {
    // 2. 주기적으로 Config 서버로부터 설정 Pull
    while (true) {
      AgentConfig agentConfig = AgentConfigHolder.get();
      PullerConfig pullerConfig = PullerConfigHolder.get();

      String baseUrl = pullerConfig.getPullURL() + "/config";
      /*String query = String.format(
          "eTag=%s",
          URLEncoder.encode(etag, StandardCharsets.UTF_8)
      );
      String requestURL = baseUrl + "?" + query;


       */
      // HTTPs 요청
      Optional<ConfigDTO> response = client.configPull(baseUrl, agentConfig.getAccessToken());

      // Config 에 변경점이 있음
      if (response.isPresent()) {
        log.info("[ConfigPuller] Received new configuration update.");
        updater.apply(response.get());
        etag = response.get().getEtag();
        log.info("[ConfigPuller] ETag updated: {}", etag);
        log.info("[ConfigPuller] Configuration puller completed.");
      } else {
        // Config에 변경 없음
        log.debug("[ConfigPuller] No config changes. ETag=, continuing...");
      }

      try {
        Thread.sleep(pullerConfig.getIntervalSec() * 1000L);
      } catch (InterruptedException e) {
        log.warn("[ConfigPuller] ConfigPuller thread interrupted. Shutting down...");
        log.warn("[ConfigPuller] Exception: {}", e.getMessage());
        break;
      }
    }
  }
}
