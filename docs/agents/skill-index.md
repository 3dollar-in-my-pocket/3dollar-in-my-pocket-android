# Skill Index

## Local Android Skills

로컬 Android skill은 `.skills/android/` 아래에 vendoring 되어 있다.

- `devtools/android-cli/SKILL.md`: Android CLI, SDK, emulator, run, docs workflow
- `jetpack-compose/migration/migrate-xml-views-to-jetpack-compose/SKILL.md`: XML View to Compose migration
- `system/edge-to-edge/SKILL.md`: Compose edge-to-edge, system bar, IME inset
- `navigation/navigation-3/SKILL.md`: Navigation 3 setup and migration
- `performance/r8-analyzer/SKILL.md`: R8/ProGuard keep rule analysis
- `build/agp/agp-9-upgrade/SKILL.md`: AGP 9 upgrade guidance

## Usage Rules

- Android 구현, 마이그레이션, 빌드 도구, 성능/R8 작업 전 관련 skill을 확인한다.
- `android-cli`는 `android` 명령 설치가 필요하다. 없으면 Gradle wrapper와 adb workflow를 사용한다.
- 단순 문서 정리나 작은 텍스트 변경에는 Android skill을 불필요하게 적용하지 않는다.

## 3dollars 플러그인 스킬 (AI 개발 프로세스)

`~/.claude/skills/3dollars/` 에 있는 **개인 환경 플러그인**이다. 저장소에 포함되지 않으므로 팀원 환경에 없을 수 있다.
호출 시 `3dollars:` 접두어가 필요하다. 프로세스 정의는 `docs/process/` 에 있고, 스킬은 그 문서를 실행하는 도구다.

| 스킬 | 하는 일 | 관련 문서 |
|---|---|---|
| `3dollars:test-cases` | 테크스펙 TC → 계층 배정 → 유닛 테스트 코드 → 커버리지 표 | `docs/process/testing.md` |
| `3dollars:simulator-test` | 에뮬레이터를 조작해 자동화 TC 재현, 스크린샷·영상 증거 | `docs/process/e2e-and-manual-tests.md` |
| `3dollars:drift` | 테크스펙 요구사항 ↔ diff 대조, 스펙 밖 변경 목록 | `docs/process/pr-process.md` |
| `3dollars:ask-author` | diff에서 설명이 필요한 결정 3개를 작성자에게 질문 | `docs/process/pr-process.md` |
| `3dollars:pr-body` | 위 결과를 템플릿에 채워 PR 생성/갱신 | `.github/PULL_REQUEST_TEMPLATE.md` |
| `3dollars:pr-code-review` | PR 코드 리뷰(테크스펙·피그마 참고, 유저 플로우 중심) | — |
| `3dollars:review-digest` | 반복 리뷰 지적을 스크립트·문서 규칙으로 승격 제안 | `docs/process/pr-process.md` |
| `3dollars:code-cleanup` | 변경된 파일의 정리·코드 스멜·성능·메모리 점검 | — |
| `3dollars:bug-fix` / `3dollars:feature-implementer` | 지라 티켓 → 구현 → 검증 → PR 파이프라인 | `docs/process/pr-process.md` |
| `3dollars:deploy-dev-build` | 개발 빌드 배포 (AOS는 `firebase-distribution.yml` 실행) | `.github/workflows/firebase-distribution.yml` |

플랫폼별 명령·경로 차이는 플러그인의 `docs/platform-profiles.md` 에 정의되어 있다.

## External/Agent Skills

- `agents-md-generator`: `AGENTS.md`를 간결한 루트 내비게이션 문서로 유지할 때 사용한다.
- Serena: 코드 구조 탐색, 심볼 검색, 참조 추적, 리팩터링이 필요할 때만 사용한다.
