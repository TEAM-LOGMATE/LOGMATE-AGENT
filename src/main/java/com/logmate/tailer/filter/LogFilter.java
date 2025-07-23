package com.logmate.tailer.filter;

import com.logmate.tailer.parser.ParsedLogData;

public interface LogFilter {
  boolean accept(ParsedLogData log);
}
