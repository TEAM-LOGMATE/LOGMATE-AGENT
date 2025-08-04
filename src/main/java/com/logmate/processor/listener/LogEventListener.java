package com.logmate.processor.listener;

public interface LogEventListener {
  void onLogReceive(String[] lines);
}
