package com.logmate.component;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.logmate.injection.config.WatcherConfig;
import com.logmate.tailer.LogTailer;

public class ComponentRegistryHolder {

  private static Injector injector;

  public static void create(WatcherConfig config) {
    ComponentRegistryHolder.injector = Guice.createInjector(new ComponentRegistry(config));
  }

  public static void remake(WatcherConfig config) {
    ComponentRegistryHolder.injector = Guice.createInjector(new ComponentRegistry(config));
  }

  public static LogTailer getTailer() {
    return injector.getInstance(LogTailer.class);
  }

  public static <T> T getInstance(Class<T> clazz) {
    return injector.getInstance(clazz);
  }
}
