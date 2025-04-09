package com.logmate.filter;

import com.logmate.parser.ParsedLogData;

public interface LogFilter {
  boolean accept(ParsedLogData log);
}
