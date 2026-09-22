# Project Agent Instructions

## Language

- 기본 사용자 응답은 한국어로 작성한다.
- 코드, 경로, 명령어, API 이름은 프로젝트의 기존 표기를 유지한다.

## Repository Purpose

이 저장소는 "가슴속삼천원" Android 앱이다. 위치 기반 가게 탐색, 리뷰, 커뮤니티, 로그인/사용자 관리, 광고, 푸시, 지도 연동을 포함한다.

## Project Shape

- Gradle Kotlin DSL 기반 Android multi-module 프로젝트다.
- 모듈 목록은 `settings.gradle.kts`를 원천으로 확인한다.
- 현재 주요 모듈: `:app`, `:common`, `:core:network`, `:core:common`, `:core:ui`, `:core:designsystem`, `:core:abtest`, `:domain`, `:data`.
- 세션을 새로 시작할 때는 먼저 `docs/README.md`를 읽고 필요한 context 문서를 따라간다.

## Current Toolchain

- 버전 원천은 `gradle/libs.versions.toml`이다.
- 현재 확인된 값: AGP `8.13.2`, Kotlin `2.2.20`, compileSdk `36`, targetSdk `36`, minSdk `24`.
- Java/Kotlin toolchain은 JDK 17 기준이다.
- 버전 변경이나 의존성 업그레이드는 사용자 승인 없이 하지 않는다.

## Canonical Commands

- 전체 빌드: `./gradlew build`
- Debug APK: `./gradlew assembleDebug`
- Release APK: `./gradlew assembleRelease`
- 단위 테스트: `./gradlew testDebugUnitTest` (CI 기준. `./gradlew test`는 전체 variant)
- 모듈 의존 방향 검사: `scripts/check-module-deps.sh`
- 테스트 결과 요약(PR 코멘트 형식): `scripts/test-summary.sh`
- 기기/에뮬레이터 테스트: `./gradlew connectedAndroidTest`
- 의존성 확인: `./gradlew dependencies`
- 문서화된 검증 하네스: `docs/harness/README.md`
- 필요한 범위의 최소 Gradle task를 우선 사용하고, full clean build는 필요한 경우에만 실행한다.

## Documentation Map

- 프로젝트 현황: `docs/context/project-current.md`
- 현재 아키텍처: `docs/context/architecture-current.md`
- 모듈 의존성: `docs/context/module-dependencies-current.md`
- 리소스 규칙: `docs/context/resource-rules-current.md`
- 마이그레이션 규칙: `docs/context/migration-rules.md`
- 변경 유형별 검증: `docs/context/verification-matrix.md`
- Jira/feature 작업 문서: `docs/features/`
- 에이전트 작업 흐름과 skill 인덱스: `docs/agents/`
- AI 개발 프로세스(테크스펙 → 테스트 → 증거 → PR): `docs/process/`

## AI Development Process

- 전체 흐름과 PR 위험도(경량/풀코스) 기준은 `docs/process/pr-process.md` 한 장에 있다. iOS 레포와 같은 형태를 쓴다.
- 의도 문서(테크스펙)는 `docs/process/tech-spec-process.md`. 지라 `테크스펙` 필드가 단일 진실 소스이고 iOS/AOS가 스펙을 공유한다.
- 테스트는 diff가 아니라 테크스펙 TC에서 도출한다. 세 계층(유닛/자동화/수동)과 네이밍은 `docs/process/testing.md`.
- 유닛으로 못 덮는 TC의 자동화·수동 분류와 화면 변경 공통 체크는 `docs/process/e2e-and-manual-tests.md`.
- 검증 장치(CI·스크립트·규칙 문서)를 바꾸는 PR은 `docs/process/verification-change.md`의 라벨 규칙을 따른다.
- PR은 `/3dollars:pr-body`로 만들고, 본문 형식은 `.github/PULL_REQUEST_TEMPLATE.md`를 따른다.

## Agent Workflow

- 작업 전 짧은 구현 계획을 먼저 보고한다.
- 변경은 작고 원자적인 단위로 수행한다.
- Jira나 feature 작업은 `docs/features/` 아래에 brief, investigation, plan, implementation log, verification 문서를 남긴다.
- 관련 없는 파일은 수정하지 않는다.
- 기존 사용자 변경사항을 되돌리지 않는다.
- 대규모 리팩터링, 의존성 추가/업그레이드, 포맷 전용 변경은 명시적 승인 없이 하지 않는다.
- 완료 전 변경 범위에 맞는 최소 검증을 실행하고, 실행하지 못한 검증은 보고한다.

## Skills And Tools

- Android 관련 로컬 skill은 `.skills/android/` 아래에 있다.
- Android 구현, 마이그레이션, 빌드 도구, 성능/R8 작업 전 `docs/agents/skill-index.md`와 관련 `SKILL.md`를 확인한다.
- `android-cli` skill은 `android` 명령 설치가 필요하다. 사용 전 `command -v android`로 확인하고, 없으면 기존 Gradle/adb workflow를 사용한다.
- `.claude/`는 Claude 전용 설정/플러그인 자료로 취급한다. 사용자가 요청하거나 Claude 전용 설정을 다룰 때만 참고한다.
- Serena는 코드 구조 탐색, 심볼 검색, 참조 추적, 리팩터링이 필요할 때만 사용한다.

## Architecture And Resources

- 기존 MVVM/Clean Architecture 흐름과 모듈 경계를 유지하되, 현재 구조와 목표 구조를 혼동하지 않는다.
- 공통 문자열은 `core:common`, 공통 UI는 `core:ui`, 디자인 리소스는 `core:designsystem` 우선 원칙을 따른다.
- 새 리소스를 만들기 전에 기존 리소스와 `docs/context/resource-rules-current.md`를 확인한다.
- 프로젝트 기존 유틸리티와 패턴을 새 추상화보다 우선한다.

## Safety

- `local.properties`, 키스토어, API 키, Firebase/AdMob/지도/소셜 로그인 키 등 민감정보를 노출하지 않는다.
- destructive git 명령은 사용자가 명시적으로 요청한 경우에만 수행한다.
- 문서 변경은 사실을 코드베이스에서 확인한 뒤 반영한다.
