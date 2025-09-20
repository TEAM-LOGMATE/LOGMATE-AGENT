package com.logmate.processor.merger;

import com.logmate.config.data.pipeline.MultilineConfig;
import com.logmate.processor.parser.LogParser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MultilineProcessor {

  private final LogParser parser;
  private final MultilineConfig config;
  List<String> multilineBuffer = new ArrayList<>();

  public String[] process(String[] rawLines) {
    if (!config.isEnabled()) {
      return rawLines;
    }

    List<String> result = new ArrayList<>();

    for (String rawLine : rawLines) {
      if (!parser.isFormatCorrect(rawLine)) {
        multilineBuffer.add(rawLine);
        if (multilineBuffer.size() >= config.getMaxLines()) {
          result.add(flushBuffer());
        }
      }
      else {
        if (!multilineBuffer.isEmpty()) {
          result.add(flushBuffer());
        }
        result.add(rawLine);
      }
    }

    // 마지막에 버퍼 남은 게 있다면 플러시
    if (!multilineBuffer.isEmpty()) {
      result.add(flushBuffer());
    }

    return result.toArray(new String[0]);
  }

  private String flushBuffer() {
    String merged = "[MERGED-STACKTRACE]\n" + String.join("\n", multilineBuffer);
    multilineBuffer.clear();
    return merged;
  }
}
