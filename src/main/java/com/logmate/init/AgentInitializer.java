package com.logmate.init;

import com.logmate.config.holder.AgentConfigHolder;
import com.logmate.config.puller.ConfigPullerRunManager;
import com.logmate.init.argument.ApplicationArgumentHandler;
import com.logmate.init.config.ConfigInitializer;

public class AgentInitializer {

  private ApplicationArgumentHandler argumentHandler;
  private ConfigInitializer configInitializer;

  public void init(String[] args) {
    // args 검증
    argumentHandler.validate(args);
    configInitializer.init();
    argumentHandler.handle(args);
    ConfigPullerRunManager.start();


    // args 적용
    AgentConfigHolder.get().setAgentId(agentId);

  }
}
