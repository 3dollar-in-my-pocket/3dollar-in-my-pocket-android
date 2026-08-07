# Agent Workflow

## Session Start

1. `AGENTS.md`를 읽는다.
2. 작업 범위에 맞는 `docs/context/` 문서를 확인한다.
3. Jira/feature 작업이면 `docs/features/`에 기존 문서가 있는지 찾는다.
4. 변경 전 `git status --short`로 사용자 변경사항을 확인한다.

## Feature Work

- 새 feature는 `docs/features/FEATURE-TEMPLATE.md`를 기준으로 문서를 만든다.
- 조사 결과와 구현 계획을 먼저 기록하고, 구현 후 verification을 채운다.
- Product/Server/iOS와 맞춘 정책은 feature 문서 또는 ADR에 남긴다.

## Documentation Updates

- 모듈 의존성이 바뀌면 `docs/context/module-dependencies-current.md`를 갱신한다.
- toolchain 또는 버전이 바뀌면 `docs/context/project-current.md`와 `AGENTS.md`를 확인한다.
- 검증 방식이 바뀌면 `docs/context/verification-matrix.md`와 `docs/harness/README.md`를 갱신한다.

## Completion Report

- 변경 요약
- 실행한 검증
- 실행하지 못한 검증과 이유
- 남은 리스크 또는 후속 작업
