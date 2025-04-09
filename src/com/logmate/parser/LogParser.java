package com.logmate.parser;


public interface LogParser {

  ParsedLogData parse(String rawLine);
}
