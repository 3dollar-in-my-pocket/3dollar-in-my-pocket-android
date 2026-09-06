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
- 단위 테스트: `./gradlew test`
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

## Agent Workflow

- 구현 전 짧은 계획을 보고하고 승인된 범위의 작업과 검증을 이어간다. 계획 보고 자체는 승인 요청이 아니며, 적용 Skill의 명시적 설계·구현 승인 단계는 유지한다.
- 이미 받은 동일 범위의 명시적 승인은 재사용한다. 질문·실패가 일부 단계만 막으면 독립적인 작업은 계속한다. 세부 기준은 `docs/agents/codex-workflow.md`를 따른다.
- 변경은 작고 원자적인 단위로 수행한다.
- Jira나 feature 작업은 `docs/features/` 아래에 brief, investigation, plan, implementation log, verification 문서를 남긴다.
- 관련 없는 파일은 수정하지 않는다.
- 기존 사용자 변경사항을 되돌리지 않는다.
- 대규모 리팩터링, 의존성 추가/업그레이드, 포맷 전용 변경은 명시적 승인 없이 하지 않는다.
- 완료 전 변경 범위에 맞는 최소 검증을 직접 실행한다. 필수 요구사항이나 검증이 남으면 미완료로 보고하며, 반복 횟수나 검토 비용만으로 완료 처리하지 않는다.

## Skills And Tools

- Android 관련 로컬 skill은 `.skills/android/` 아래에 있다.
- Android 구현, 마이그레이션, 빌드 도구, 성능/R8 작업 전 `docs/agents/skill-index.md`와 관련 `SKILL.md`를 확인한다.
- Skill은 플랫폼의 시스템·도구 제약을 재정의하지 않는다. 사용자 지시와 이 프로젝트의 승인·완료 규칙을 우선하며, 중복 Skill 원본 선택은 `docs/agents/skill-index.md`를 따른다.
- `android-cli` skill은 `android` 명령 설치가 필요하다. 사용 전 `command -v android`로 확인하고, 없으면 기존 Gradle/adb workflow를 사용한다.
- `.claude/`는 Claude 전용 설정/플러그인 자료로 취급한다. 사용자가 요청하거나 Claude 전용 설정을 다룰 때만 참고한다.
- Serena는 코드 구조 탐색, 심볼 검색, 참조 추적, 리팩터링이 필요할 때만 사용한다.

## Architecture And Resources

- 기존 MVVM/Clean Architecture 흐름과 모듈 경계를 유지하되, 현재 구조와 목표 구조를 혼동하지 않는다.
- 공통 문자열은 `core:common`, 공통 UI는 `core:ui`, 디자인 리소스는 `core:designsystem` 우선 원칙을 따른다.
- 새 리소스를 만들기 전에 기존 리소스와 `docs/context/resource-rules-current.md`를 확인한다.
- 프로젝트 기존 유틸리티와 패턴을 새 추상화보다 우선한다.

## Safety

- 결제·예약·배포·전송·삭제 등 외부 상태 변경은 실행 전에 명시적 확인을 받는다. 로컬 조사·초안·검증의 승인을 외부 실행 승인으로 확대하지 않는다.
- `local.properties`, 키스토어, API 키, Firebase/AdMob/지도/소셜 로그인 키 등 민감정보를 노출하지 않는다.
- destructive git 명령은 사용자가 명시적으로 요청한 경우에만 수행한다.
- 문서 변경은 사실을 코드베이스에서 확인한 뒤 반영한다.
