package com.logmate.config.puller;

import com.logmate.bootstrap.args.AgentArguments;
import com.logmate.bootstrap.auth.AgentAuthenticator;
import com.logmate.config.data.AgentConfig;
import com.logmate.config.data.PullerConfig;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.PullerConfigHolder;
import com.logmate.config.puller.dto.ConfigPullResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigPuller implements Runnable {

  private final ConfigPullClient client;
  private final ConfigUpdater updater;
  private final AgentAuthenticator authenticator;
  private final AgentArguments agentArguments;
  private String etag;


  public ConfigPuller(ConfigPullClient client, ConfigUpdater updater,
      AgentAuthenticator authenticator, AgentArguments agentArguments) {
    this.client = client;
    this.updater = updater;
    this.authenticator = authenticator;
    this.agentArguments = agentArguments;
    this.etag = String.valueOf(UUID.randomUUID());
  }

  @Override
  public void run() {
    // 주기적으로 Config 서버로부터 설정 Pull
    while (true) {
      // Global 한 설정을 다시 가져온다.
      AgentConfig agentConfig = AgentConfigHolder.get();
      PullerConfig pullerConfig = PullerConfigHolder.get();

      // puller url (API Server)
      String baseUrl = pullerConfig.getPullURL() + "/api/config";
      String query = String.format(
          "agentId=%s&etag=%s",
          agentConfig.getAgentId(),
          etag
      );
      String requestURL = baseUrl + "?" + query;

      // HTTPs 요청
      ConfigPullResponse response = client.configPull(requestURL, agentConfig.getAccessToken());


      // Config 에 변경점이 있음
      if (response.getStatus() == 200) {
        log.info("[ConfigPuller] Received new configuration.");
        updater.apply(response.getBody());
        etag = response.getBody().getEtag();
        log.info("[ConfigPuller] Configuration updated.");
      }
      else if (response.getStatus() == 401) {
        authenticator.authenticate(agentArguments);
        log.info("[ConfigPuller] Authentication succeeded. Retrying pull...");
        ConfigPullResponse retryResponse = client.configPull(baseUrl, agentConfig.getAccessToken());
        if (retryResponse.getStatus() == 200) {
          log.info("[ConfigPuller] Retry succeeded. Updating configuration...");
          // todo: validation 먼저 하는 것 필요
          updater.apply(retryResponse.getBody());
          etag = retryResponse.getBody().getEtag();
        }
      }
      else if (response.getStatus() == 304) {
        // Config에 변경 없음
        log.debug("[ConfigPuller] No config changes. continuing...");
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
