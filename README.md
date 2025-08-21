# 🧩 LogMate Agent - Tail Mate
![License](https://img.shields.io/badge/license-Apache--2.0-green.svg)
![Java](https://img.shields.io/badge/java-17%2B-blue.svg)
![Build](https://img.shields.io/badge/build-Gradle-success.svg)
> 실시간 로그 수집과 전송을 위한 경량형 에이전트

**Tail Mate**는 LogMate 시스템의 **로그 수집 컴포넌트**로, 다양한 환경의 로그를 실시간으로 수집하고 중앙 로그 스트리밍 서버로 전송하는 **경량형 Agent**입니다. **Agent ID** 하나만 입력하면 실행 가능한 **Minimal Configuration** 구조를 기반으로 , 웹 UI를 통한 **동적 재설정**, **로그 필터링**, **보안 전송** 등을 지원합니다.

---
## 🧰 주요 기능
### 설정 기능
| 기능 분류 | 기능 설명 |
|----------|-----------|
| 에이전트 초기화 | - Agent ID 검증<br> - Agent ID 하나로 실행 가능 |
| 동적 재설정 | - 웹 대시보드에서의 설정 변경 사항 실시간 반영<br> - 에이전트 중단 없이 설정 재주입 가능 |

### 로그 파이프라인 기능
| 기능 분류 | 기능 설명 |
|----------|-----------|
| 로그 수집 | - 지정된 경로의 로그 파일 실시간 수집<br> - 다중 파일 로그 수집<br> - 수집 주기 설정  |
| 로그 처리 | - Springboot 로그, Tomcat Access 로그 포맷 파싱<br> - 멀티라인 로그 병합 처리<br> - 사용자 설정 기반 로그 필터링 |
| 로그 전송 | - HTTPS + JWT 기반 안전 전송<br> - 실패 시 재시도<br> - 압축 전송 지원 |

---

## 📂 클래스 다이어그램
<img width="1700" height="1411" alt="TailMateArchitecture" src="https://github.com/user-attachments/assets/a526ba34-56df-443a-8179-540c9757981e" />

---
## 📂 패키지 구조
```
/src/java/com/logmate/
│
├── config/ # Agent 전체 설정 담당 패키지
│ ├── holder/ # 설정 보관소 (AgentConfigHolder 등)
│ ├── loader/ # 설정 파일 로더
│ ├── puller/ # 원격 설정 동기화 모듈
│ │ └── dto/ # Puller 통신 데이터
│ └── validator/ # 설정 유효성 검증기
│
├── di/ # 의존성 주입 및 컴포넌트 등록 담담 패키지
│
├── processor/ # 로그 처리 파이프라인 구성 패키지
│ ├── exporter/ # 로그 전송 처리기
│ ├── filter/ # 로그 필터링 처리기
│ ├── merger/ # 멀티라인 병합 처리기
│ ├── parser/ # 로그 파서
│ └── listener/ # 로그 수집 이벤트 리스너
│
└── tailer/ # 로그 수집기
```

---

## 📦 설치 및 실행

### 1. 요구 사항

- Java 17+
- 운영 체제: Linux/macOS/Windows

### 2. 실행 방법

#### ✅ 빌드 및 직접 실행
```bash
git clone https://github.com/TEAM-LOGMATE/LOGMATE-AGENT.git
cd LOGMATE-AGENT
./gradlew build
java -jar build/libs/logmate-agent.jar --agentId=yourId
```

#### ✅ Jar 파일로 실행
```bash
java -jar logmate-agent.jar --agentId=yourId
```
---

### 📄 오픈소스 라이선스

본 프로젝트는 아래의 오픈소스 라이브러리를 사용합니다:

- **Jackson (Databind, Datatype JSR310)** - Apache License 2.0
- **SLF4J Simple** - MIT License
- **Lombok** - MIT License
- **Guice** - Apache License 2.0
- **SnakeYAML** - Apache License 2.0

각 라이브러리는 해당 라이선스에 따라 사용됩니다.

---

### 📄 라이선스
본 프로젝트는 **Apache License 2.0** 에 따라 라이선스가 부여됩니다.
자세한 내용은 [LICENSE](./LICENSE) 파일을 참조하세요.

---

### 🙏 기여 가이드
- PR 생성은 [pull_request_template.md](.github/pull_request_template.md) 문서를 참고해 주세요.
- Issue 생성은 [issue_report.md](.github/ISSUE_TEMPLATE/issue_report.md) 문서를 참고해 주세요.
- 로그파이프라인 API 구조/기능은 [로그 파이프라인 개발 가이드 Wiki](https://github.com/TEAM-LOGMATE/LOGMATE-AGENT/wiki/LogPipeline-Development-Guide)에서 확인하실 수 있습니다.

---

### 📲 연락처
email: kan0202@naver.com

---

