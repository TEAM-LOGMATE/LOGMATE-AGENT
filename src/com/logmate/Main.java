package com.logmate;

import com.logmate.component.ComponentRegistry;

public class Main {

  public static void main(String[] args) {
    ComponentRegistry componentRegistry = new ComponentRegistry("sample.log");
    Thread tailerThread = new Thread(componentRegistry.getLogTailer());
    tailerThread.start();
    System.out.println("log-mate-library is running");
    System.out.println("Log tailer started...");
  }
}