package com.logmate.component;

import com.logmate.config.WatcherConfigHolder;

public class ComponentRegistryHolder {
  public static ComponentRegistry componentRegistry = new ComponentRegistry(WatcherConfigHolder.get());

  public static ComponentRegistry get() {
    return componentRegistry;
  }

  public static void remake() {
    componentRegistry = new ComponentRegistry(WatcherConfigHolder.get());
  }

}
