package com.logmate.bootstrap.args;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class AgentArguments {

  private final String agentId;

  public AgentArguments(String agentId) {
    this.agentId = agentId;
  }
}