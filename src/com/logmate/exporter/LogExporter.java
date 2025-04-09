package com.logmate.exporter;

import java.util.List;
import com.logmate.parser.ParsedLogData;

public interface LogExporter {
  void export(List<ParsedLogData> logDataList);
}
