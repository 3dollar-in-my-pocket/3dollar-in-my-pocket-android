# Feature Documentation

Jira 또는 feature 단위 작업은 이 디렉터리에 남긴다.

## Directory Naming

- Jira가 있으면 `TH-####-short-title`
- Jira가 없으면 `short-feature-name`

## Recommended Files

- `00-brief.md`: 문제, 목표, 성공 기준, in/out of scope
- `01-investigation.md`: 코드 조사, API/디자인 확인, 원인 분석
- `02-plan.md`: 구현 계획, 파일 범위, 검증 계획
- `03-implementation-log.md`: 실제 변경 요약, 의사결정, 중간 이슈
- `04-verification.md`: 실행한 테스트, 수동 QA, 남은 리스크
- `assets/`: PDF, screenshot, 비교 이미지 등 필요한 자료

## Rules

- 문서에는 민감정보를 넣지 않는다.
- 현재 사실, 추정, Product/Server 결정사항을 구분한다.
- 완료 후에는 구현 결과와 검증 명령을 `03` 또는 `04` 문서에 남긴다.
