package com.logmate.filter.impl;


import com.logmate.filter.LogFilter;
import com.logmate.parser.ParsedLogData;

public class NonLogFilter implements LogFilter {

  @Override
  public boolean accept(ParsedLogData log) {
    return true;
  }
}
