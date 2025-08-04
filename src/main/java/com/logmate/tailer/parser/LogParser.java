package com.logmate.tailer.parser;


public interface LogParser {

  ParsedLogData parse(String rawLine);
  boolean isFormatCorrect(String rawLine);
}
