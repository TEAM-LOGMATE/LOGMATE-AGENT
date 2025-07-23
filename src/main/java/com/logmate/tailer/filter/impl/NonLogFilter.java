package com.logmate.tailer.filter.impl;


import com.logmate.tailer.filter.LogFilter;
import com.logmate.tailer.parser.ParsedLogData;

public class NonLogFilter implements LogFilter {

  @Override
  public boolean accept(ParsedLogData log) {
    return true;
  }
}
