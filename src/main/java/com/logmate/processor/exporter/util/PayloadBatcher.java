package com.logmate.processor.exporter.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logmate.processor.parser.ParsedLogData;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * PayloadBatcher
 *
 * 로그 리스트를 직렬화된 JSON 바이트 크기 기준으로 분할하는 유틸리티 클래스.
 * - 네트워크 전송 시 HTTP body 크기 제한(예: 1MB)을 넘지 않도록 안전하게 나눔.
 * - 최소 단위는 로그 1건이며, 한 건이 MAX_SIZE를 초과해도 강제로 단독 배치로 전송된다.
 */
@RequiredArgsConstructor
public class PayloadBatcher {

  private final ObjectMapper mapper;
  private final int maxPayloadBytes;

  /**
   * 주어진 로그 리스트를 maxPayloadBytes 기준으로 분할한다.
   *
   * @param logs 전체 로그 리스트
   * @return 분할된 로그 리스트들의 리스트
   */
  public List<List<ParsedLogData>> split(List<ParsedLogData> logs) {
    List<List<ParsedLogData>> batches = new ArrayList<>();
    List<ParsedLogData> currentBatch = new ArrayList<>();

    for (ParsedLogData log : logs) {
      currentBatch.add(log);

      if (isTooLarge(currentBatch)) {
        if (currentBatch.size() == 1) {
          // 단일 로그 자체가 너무 큰 경우 → 그대로 전송
          batches.add(new ArrayList<>(currentBatch));
          currentBatch.clear();
        } else {
          // 직전 로그까지 확정 → 새 배치 시작
          currentBatch.remove(currentBatch.size() - 1);
          batches.add(new ArrayList<>(currentBatch));

          // 새 배치에 현재 로그 추가
          currentBatch.clear();
          currentBatch.add(log);
        }
      }
    }

    if (!currentBatch.isEmpty()) {
      batches.add(currentBatch);
    }

    return batches;
  }

  /**
   * 현재 배치가 허용 크기를 초과하는지 확인
   */
  private boolean isTooLarge(List<ParsedLogData> batch) {
    try {
      String json = mapper.writeValueAsString(batch);
      byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
      return bytes.length > maxPayloadBytes;
    } catch (IOException e) {
      throw new RuntimeException("[isTooLarge] Failed to measure payload size", e);
    }
  }


}
