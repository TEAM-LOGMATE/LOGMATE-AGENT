package com.logmate.injection.puller;

import com.logmate.injection.config.PullerConfig;
import com.logmate.injection.config.WatcherConfig;
import com.logmate.injection.config.util.WatcherConfigHolder;
import com.logmate.tailer.TailerRunManager;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor
public class ConfigPuller implements Runnable {

  private final ConfigPullerClient client = new ConfigPullerClient();
  private volatile boolean running = true;

  @SneakyThrows
  @Override
  public void run() {
    while (running) {
      WatcherConfig watcherConfig = WatcherConfigHolder.get();
      PullerConfig pullerConfig = WatcherConfigHolder.get().getPuller();

      String baseUrl = pullerConfig.getPullURL();
      String query = String.format(
          "eTag=%s&thNum=%d",
          URLEncoder.encode(watcherConfig.getEtag(), StandardCharsets.UTF_8),
          watcherConfig.getThNum()
      );
      String requestURL = baseUrl + "?" + query;

      Optional<WatcherConfig> config = client.pull(requestURL, watcherConfig.getAccessToken());

      if (config.isPresent()) {
        if (WatcherConfigHolder.update(config.get())) {
          TailerRunManager.restart();
        }
      }

      Thread.sleep(pullerConfig.getIntervalSec() * 1000L);
    }
  }

  public void stop() {
    running = false;
  }
}
