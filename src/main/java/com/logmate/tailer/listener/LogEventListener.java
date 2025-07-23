package com.logmate.tailer.listener;

public interface LogEventListener {
  void onLogReceive(String[] lines);
}
