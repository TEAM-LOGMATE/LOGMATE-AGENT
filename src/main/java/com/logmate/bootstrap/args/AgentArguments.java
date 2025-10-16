package com.logmate.bootstrap.args;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public class AgentArguments {

  private final String agentId;
  private final String email;
  private final String password;
}