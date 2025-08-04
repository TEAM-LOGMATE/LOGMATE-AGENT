package com.logmate.tailer.parser;

public interface ParsedLogData {
  String getMessage();
  boolean isFormatCorrect();
}
