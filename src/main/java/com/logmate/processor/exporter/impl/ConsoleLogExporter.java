package com.logmate.processor.exporter.impl;

import com.logmate.processor.exporter.LogExporter;
import java.util.List;
import com.logmate.processor.parser.ParsedLogData;


public class ConsoleLogExporter implements LogExporter {

  @Override
  public List<ParsedLogData> export(List<ParsedLogData> logDataList) {
    for (ParsedLogData exportData : logDataList) {
      System.out.println("Exported Log: " + exportData);
    }
    return logDataList;
  }
}
