package com.logmate;

import com.logmate.bootstrap.AgentInitializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) {
    AgentInitializer initializer = new AgentInitializer();
    initializer.init(args);
  }
}