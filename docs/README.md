# Project Documentation

이 디렉터리는 세션이 끊겨도 프로젝트 상태와 작업 맥락을 복구할 수 있게 유지하는 canonical 문서 위치다.

## 새 세션 시작 순서

1. `AGENTS.md`를 읽어 응답 언어, 안전 규칙, 검증 원칙을 확인한다.
2. `docs/context/project-current.md`에서 현재 toolchain과 앱 구조를 확인한다.
3. 변경 범위에 따라 아래 문서를 읽는다.
   - 모듈/아키텍처: `docs/context/architecture-current.md`
   - 의존성: `docs/context/module-dependencies-current.md`
   - 리소스: `docs/context/resource-rules-current.md`
   - 검증: `docs/context/verification-matrix.md`, `docs/harness/README.md`
4. Jira나 feature 작업이면 `docs/features/` 아래에 작업 문서를 만들거나 기존 문서를 갱신한다.

## Directory Map

- `context/`: 현재 프로젝트 사실, 아키텍처, 모듈 의존성, 리소스 규칙, 검증 매트릭스
- `features/`: Jira/feature별 brief, investigation, plan, implementation log, verification
- `decisions/`: ADR과 장기 설계 결정
- `harness/`: Gradle, adb, 디자인 검증 하네스
- `agents/`: 에이전트 작업 흐름, skill 인덱스, 문서 업데이트 규칙

## Documentation Rules

- 현재 사실과 목표 계획을 같은 문서에서 섞지 않는다.
- 코드베이스에서 확인한 내용만 현재 상태로 기록한다.
- 완료된 Jira/feature 문서는 구현 결과와 검증 명령을 남긴다.
- 루트의 과거 문서 파일은 호환용 포인터로만 유지하고, 본문은 이 디렉터리에서 관리한다.
