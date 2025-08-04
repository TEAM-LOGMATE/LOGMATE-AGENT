package com.logmate.tailer.listener.impl;


import com.logmate.tailer.exporter.LogExporter;
import com.logmate.tailer.filter.LogFilter;
import com.logmate.tailer.listener.LogEventListener;
import com.logmate.tailer.merger.MultilineProcessor;
import java.util.ArrayList;
import java.util.List;
import com.logmate.tailer.parser.LogParser;
import com.logmate.tailer.parser.ParsedLogData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultLogEventListener implements LogEventListener {

  // 로그 파서 인터페이스
  private final LogParser logParser;
  // 로그 필터 인터페이스
  private final LogFilter logFilter;
  // 로그 익스포터 인터페이스
  private final LogExporter logExporter;
  private final MultilineProcessor multilineProcessor;

  /**
   * 1초마다 받아 온 로그들을 한 줄씩 parser -> filter 를 통과시켜 버퍼링해둔다. 이후 exporter 로 버퍼링된 로그들을 외부로 전송한다.
   *
   * @param lines 1초동안 모은 로그 데이터들
   */
  @Override
  public void onLogReceive(String[] lines) {
    // export 할 로그들의 저장소(버퍼)
    List<ParsedLogData> exportLogs = new ArrayList<>();
    lines = multilineProcessor.process(lines);

    // 한 줄씩 parser 와 filter 를 통과시킨다.
    for (String line : lines) {
      ParsedLogData parse = logParser.parse(line);
      if (logFilter.accept(parse)) {
        exportLogs.add(parse); // filter 에서 처리가 accept 되었을 경우 버퍼에 추가
      }
    }
    // export 로그 버퍼가 비어있지 않을 경우 버퍼 flush (export)
    if (!exportLogs.isEmpty()) {
      logExporter.export(exportLogs);
    }
  }
}
