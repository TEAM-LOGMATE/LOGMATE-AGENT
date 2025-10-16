package com.logmate.bootstrap.auth;

import com.logmate.bootstrap.args.AgentArguments;
import com.logmate.config.data.AgentConfig;
import com.logmate.config.holder.AgentConfigHolder;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentAuthenticator {

  private final AuthClient client;

  @Inject
  public AgentAuthenticator(AuthClient client) {
    this.client = client;
  }

  public void authenticate(AgentArguments arguments) {
    log.info("[AgentAuthenticator] Authenticating agentId={}, email={} ...", arguments.getAgentId(),
        arguments.getEmail());
    Optional<LoginResponse> response = client.login(arguments.getEmail(), arguments.getPassword());

    if (response.isPresent()) {
      log.info("[AgentAuthenticator] Authentication succeeded.");
      AgentConfig agentConfig = AgentConfigHolder.get();
      agentConfig.setAccessToken(response.get().getToken());
      AgentConfigHolder.update(agentConfig);
      return;
    } else {
      log.warn("[AgentAuthenticator] Authentication failed.");
      throw new RuntimeException("Agent Authentication failed.");
    }
  }
}
