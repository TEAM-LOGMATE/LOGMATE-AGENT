package com.logmate.bootstrap.auth;

import com.logmate.config.AgentConfig;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.holder.PullerConfigHolder;
import com.logmate.config.puller.dto.TokenDTO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentAuthenticator {

  private final AuthClient client;

  public AgentAuthenticator(AuthClient client) {
    this.client = client;
  }

  public void authenticate(AuthToken authToken) {
    log.info("[AgentAuthenticator] Authenticating agentId={} ...", authToken.agentId());
    AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO(authToken);
    String url = PullerConfigHolder.get().getPullURL() + "/auth";
    Optional<TokenDTO> response = client.request(url, requestDTO);

    if (response.isPresent()) {
      log.info("[AgentAuthenticator] Authentication succeeded.");
      AgentConfig agentConfig = AgentConfigHolder.get();
      agentConfig.setAccessToken(response.get().getAccessToken());
      AgentConfigHolder.update(agentConfig);
      return;
    } else {
      log.warn("[AgentAuthenticator] Authentication failed.");
      throw new RuntimeException("Agent Authentication failed.");
    }
  }
}
