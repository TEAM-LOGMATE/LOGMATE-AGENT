package com.logmate.exporter.impl;

import com.logmate.exporter.LogExporter;
import java.util.List;
import com.logmate.parser.ParsedLogData;


public class ConsoleLogExporter implements LogExporter {

  @Override
  public void export(List<ParsedLogData> logDataList) {
    for (ParsedLogData exportData : logDataList) {
      System.out.println("Exported Log: " + exportData);
    }
  }
}
