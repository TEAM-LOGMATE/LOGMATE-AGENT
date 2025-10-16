package com.logmate.config.puller;

import com.logmate.bootstrap.args.AgentArguments;
import com.logmate.bootstrap.auth.AgentAuthenticator;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ConfigPullerRunManager {

  private Thread pullerThread;

  public void start(AgentAuthenticator agentAuthenticator, AgentArguments agentArguments) {
    pullerThread = new Thread(
        new ConfigPuller(new ConfigPullClient(), new ConfigUpdater(new ConfigConverter()),
            agentAuthenticator, agentArguments)
    );
    pullerThread.start();
    log.info("[ConfigPullerRunManager] configuration puller started...");
  }
}
