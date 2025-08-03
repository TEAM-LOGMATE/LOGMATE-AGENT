package com.logmate.injection.puller;

import com.logmate.injection.config.AgentConfig;
import com.logmate.injection.config.PullerConfig;
import com.logmate.injection.config.WatcherConfig;
import com.logmate.injection.config.util.AgentConfigHolder;
import com.logmate.injection.config.util.PullerConfigHolder;
import com.logmate.injection.config.util.WatcherConfigHolder;
import com.logmate.tailer.TailerRunManager;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class ConfigPuller implements Runnable {

  private final ConfigPullerClient client = new ConfigPullerClient();
  private volatile boolean running = true;
  private String etag = String.valueOf(UUID.randomUUID());

  @SneakyThrows
  @Override
  public void run() {
    while (running) {
      AgentConfig agentConfig = AgentConfigHolder.get();
      PullerConfig pullerConfig = PullerConfigHolder.get();

      String baseUrl = pullerConfig.getPullURL();
      String query = String.format(
          "eTag=%s",
          URLEncoder.encode(etag, StandardCharsets.UTF_8)
      );
      String requestURL = baseUrl + "?" + query;

      Optional<ConfigDTO> response = client.pull(requestURL, agentConfig.getAccessToken());

      // Config 에 변경점이 있음
      response.ifPresent(this::configsAndEtagUpdate);

      Thread.sleep(pullerConfig.getIntervalSec() * 1000L);
    }
  }

  private void configsAndEtagUpdate(ConfigDTO config) {
    boolean shouldRestart = false;
    AgentConfig responseAgentConfig = config.getAgentConfig();
    PullerConfig responsePullerConfig = config.getPullerConfig();

    if (!responseAgentConfig.getEtag().equals(AgentConfigHolder.get().getEtag())) {
      log.info("[ConfigPuller] AgentConfig changed. Restart required.");
      if (AgentConfigHolder.update(responseAgentConfig)) {
        shouldRestart = true;
      }
    }

    if (!responsePullerConfig.getEtag().equals(PullerConfigHolder.get().getEtag())) {
      log.info("[ConfigPuller] PullerConfig changed.");
      PullerConfigHolder.update(responsePullerConfig);
    }

    List<WatcherConfig> responseWatcherConfigs = config.getWatcherConfigs();

    for (WatcherConfig responseWatcherConfig : responseWatcherConfigs) {
      Optional<WatcherConfig> opWatcherConfig = WatcherConfigHolder.get(
          responseWatcherConfig.getThNum());

      if (opWatcherConfig.isPresent()) {
        WatcherConfig watcherConfig = opWatcherConfig.get();
        if (responseWatcherConfig.getEtag().equals(watcherConfig.getEtag())) {
          continue;
        }

        if (WatcherConfigHolder.update(responseWatcherConfig, watcherConfig.getThNum())) {
          log.info("[ConfigPuller] WatcherConfig #{} changed. Restart required.", watcherConfig.getThNum());
          shouldRestart = true;
        }
      }
      else {
        log.warn("[ConfigPuller] Unknown thNum {} in response. Ignoring WatcherConfig.", responseWatcherConfig.getThNum());
      }
    }

    etag = config.getEtag();
    if (shouldRestart) {
      //Todo: thNum 에 따른 restart 로직 구현
      TailerRunManager.restart();
    }
  }

  public void stop() {
    running = false;
  }
}
