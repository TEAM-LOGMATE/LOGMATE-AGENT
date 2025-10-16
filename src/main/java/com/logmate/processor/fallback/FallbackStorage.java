package com.logmate.processor.fallback;

import com.logmate.processor.parser.LogType;
import com.logmate.processor.parser.ParsedLogData;
import java.util.List;

/**
 * FallbackStorage
 *
 * 로그 전송 실패 시 데이터를 임시 보관하는 저장소.
 * - 네트워크 장애, 서버 다운 등으로 전송 실패했을 때 로그 유실을 막는다.
 * - 저장된 로그는 이후 재전송(retry)할 수 있다.
 */
public interface FallbackStorage {
  void save(List<ParsedLogData> logs);
  List<ParsedLogData> loadAll();
  void clear();
}
