package com.logmate.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.logmate.bootstrap.args.ArgsExtractor;
import com.logmate.bootstrap.auth.AgentAuthenticator;
import com.logmate.bootstrap.auth.AuthClient;
import com.logmate.bootstrap.config.ConfigInitializer;
import com.logmate.bootstrap.config.loader.ConfigLoader;
import com.logmate.bootstrap.config.loader.impl.YamlConfigLoader;
import com.logmate.config.puller.ConfigPullerRunManager;

public class AgentInitializerRegistry extends AbstractModule {
  @Override
  protected void configure() {
    // ConfigLoader 인터페이스로 바인딩 → 추후 JsonConfigLoader 등으로 쉽게 교체 가능
    bind(ConfigLoader.class).to(YamlConfigLoader.class).in(Singleton.class);

    // 유틸/헬퍼 클래스는 매번 인스턴스화 필요 없으므로 Singleton
    bind(ArgsExtractor.class).in(Singleton.class);
    bind(AuthClient.class).in(Singleton.class);

    // 주입 시점에 필요한 의존성까지 함께 관리
    bind(ConfigInitializer.class).in(Singleton.class);
    bind(AgentAuthenticator.class).in(Singleton.class);
    bind(ConfigPullerRunManager.class).in(Singleton.class);
  }
}
