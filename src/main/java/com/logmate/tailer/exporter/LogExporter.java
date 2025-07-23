package com.logmate.tailer.exporter;

import java.util.List;
import com.logmate.tailer.parser.ParsedLogData;

public interface LogExporter {
  void export(List<ParsedLogData> logDataList);
}
