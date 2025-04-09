package com.logmate.watcher.listener;

public interface LogEventListener {
  void onLogReceive(String[] lines);
}
