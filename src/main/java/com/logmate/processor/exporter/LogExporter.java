package com.logmate.processor.exporter;

import java.util.List;
import com.logmate.processor.parser.ParsedLogData;

/**
 * LogExporter
 *
 * 로그를 외부 시스템으로 전달하기 위한 표준 인터페이스.
 *
 * 설계 의도:
 * - 파싱 및 필터링 과정을 거친 로그 데이터를
 *   최종적으로 전송(Export)하는 책임을 가진다.
 * - 다양한 Export 방식(HTTP, Kafka, S3 등)을
 *   유연하게 확장할 수 있도록 인터페이스로 정의.
 *
 * 주요 역할:
 * - export() 메서드는 로그 리스트를 입력받아 외부로 전송한다.
 * - 전송 대상은 HTTP API, 메시지 브로커, 파일 시스템 등 다양할 수 있다.
 * - Export 과정에서 직렬화(Serialization), 압축, 인증, 재시도 등의 정책을 구현체에서 자유롭게 적용 가능하다.
 *
 * 확장 가이드:
 * - 새로운 전송 대상이 필요하다면 LogExporter를 구현한다.
 *   예:
 *     - HttpLogExporter: REST API 서버로 로그 전송
 *     - KafkaLogExporter: Kafka 토픽으로 로그 발행
 *     - FileLogExporter: 로컬 파일 또는 S3에 저장
 *
 * 사용 흐름:
 * 1. Agent가 로그를 수집하고 Parser/Filter로 가공한다.
 * 2. Exporter가 export()를 통해 최종 목적지로 로그를 보낸다.
 */
public interface LogExporter {
  List<ParsedLogData> export(List<ParsedLogData> logDataList);
}
