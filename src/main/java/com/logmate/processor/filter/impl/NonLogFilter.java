package com.logmate.processor.filter.impl;


import com.logmate.processor.filter.LogFilter;
import com.logmate.processor.parser.ParsedLogData;

public class NonLogFilter implements LogFilter {

  @Override
  public boolean accept(ParsedLogData log) {
    return true;
  }
}
