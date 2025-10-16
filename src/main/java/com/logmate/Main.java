package com.logmate;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.logmate.bootstrap.AgentInitializer;
import com.logmate.di.AgentInitializerRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new AgentInitializerRegistry());
    AgentInitializer initializer = injector.getInstance(AgentInitializer.class);
    initializer.init(args);
  }
}