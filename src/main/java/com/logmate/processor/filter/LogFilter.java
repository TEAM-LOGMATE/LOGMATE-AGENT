package com.logmate.processor.filter;

import com.logmate.processor.parser.ParsedLogData;

public interface LogFilter {
  boolean accept(ParsedLogData log);
}
