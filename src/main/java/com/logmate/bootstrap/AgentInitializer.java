package com.logmate.bootstrap;

import com.google.inject.Inject;
import com.logmate.bootstrap.args.ArgsExtractor;
import com.logmate.bootstrap.auth.AgentAuthenticator;
import com.logmate.config.puller.ConfigPullerRunManager;
import com.logmate.bootstrap.args.AgentArguments;
import com.logmate.bootstrap.config.ConfigInitializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentInitializer {

  private final ArgsExtractor argsExtractor;
  private final ConfigInitializer configInitializer;
  private final AgentAuthenticator authenticator;
  private final ConfigPullerRunManager configPullerRunManager;

  @Inject
  public AgentInitializer(
      ArgsExtractor argsExtractor,
      ConfigInitializer configInitializer,
      AgentAuthenticator authenticator,
      ConfigPullerRunManager configPullerRunManager
  ) {
    this.argsExtractor = argsExtractor;
    this.configInitializer = configInitializer;
    this.authenticator = authenticator;
    this.configPullerRunManager = configPullerRunManager;
  }

  public void init(String[] args) {
    log.info("[AgentInitializer] Starting initialization...");

    // 1. args 로부터 DTO 변환
    AgentArguments agentArguments = argsExtractor.extract(args);
    // 2. 설정 초기화 (AgentConfig + PullerConfig)
    configInitializer.init(agentArguments);
    // 3. Agent 인증 검사
    authenticator.authenticate(agentArguments);
    // 3. ConfigPuller 시작 (동적 설정 주입)
    configPullerRunManager.start(authenticator, agentArguments);

    log.info("[AgentInitializer] Initialization complete. Agent is running.");
  }
}
