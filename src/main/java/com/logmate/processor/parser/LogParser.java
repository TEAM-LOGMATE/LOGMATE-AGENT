package com.logmate.processor.parser;


public interface LogParser {

  ParsedLogData parse(String rawLine);
  boolean isFormatCorrect(String rawLine);
}
