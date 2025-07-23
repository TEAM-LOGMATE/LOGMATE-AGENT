package com.logmate.listener;

public interface LogEventListener {
  void onLogReceive(String[] lines);
}
