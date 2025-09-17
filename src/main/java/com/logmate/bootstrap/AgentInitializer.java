package com.logmate.bootstrap;

import com.logmate.bootstrap.args.ArgsExtractor;
import com.logmate.bootstrap.auth.AgentAuthenticator;
import com.logmate.bootstrap.auth.AuthClient;
import com.logmate.bootstrap.auth.AuthToken;
import com.logmate.bootstrap.config.loader.impl.YamlConfigLoader;
import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.puller.ConfigPullerRunManager;
import com.logmate.bootstrap.args.AgentArguments;
import com.logmate.bootstrap.config.ConfigInitializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentInitializer {

  public void init(String[] args) {
    log.info("[AgentInitializer] Starting initialization...");
    // 추후 DI 컨테이너 사용 고려
    ArgsExtractor argsExtractor = new ArgsExtractor();
    ConfigInitializer configInitializer = new ConfigInitializer(new YamlConfigLoader());
    AgentAuthenticator authenticator = new AgentAuthenticator(new AuthClient());
    ConfigPullerRunManager configPullerRunManager = new ConfigPullerRunManager();
    
    // 1. args 로부터 DTO 변환
    AgentArguments agentArguments = argsExtractor.extract(args);
    // 2. 설정 초기화 (AgentConfig + PullerConfig)
    configInitializer.init(agentArguments);
    // 3. Agent 인증 검사
    AuthToken authToken = new AuthToken(AgentConfigHolder.get().getAgentId());
    authenticator.authenticate(authToken);
    // 3. ConfigPuller 시작 (동적 설정 주입)
    configPullerRunManager.start();

    log.info("[AgentInitializer] Initialization complete. Agent is running.");
  }
}
