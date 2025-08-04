package com.logmate.processor.exporter;

import java.util.List;
import com.logmate.processor.parser.ParsedLogData;

public interface LogExporter {
  void export(List<ParsedLogData> logDataList);
}
