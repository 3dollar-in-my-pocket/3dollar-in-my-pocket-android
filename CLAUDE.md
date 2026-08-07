# Project Agent Instructions

## Language

- 기본 사용자 응답은 한국어로 작성한다.
- 코드, 경로, 명령어, API 이름은 프로젝트의 기존 표기를 유지한다.

## Repository Purpose

이 저장소는 "가슴속삼천원" Android 앱이다. 위치 기반 가게 탐색, 리뷰, 커뮤니티, 로그인/사용자 관리, 광고/푸시/지도 연동을 포함한다.

## Project Shape

- Gradle Kotlin DSL 기반 Android multi-module 프로젝트다.
- 모듈 목록은 `settings.gradle.kts`를 원천으로 확인한다.
- 현재 주요 모듈: `:app`, `:common`, `:core:network`, `:core:common`, `:core:ui`, `:core:designsystem`, `:core:abtest`, `:domain`, `:data`.
- 아키텍처/리소스 세부 규칙은 `DEPENDENCY_MAP.md`와 `MIGRATION_RULES.md`를 참고한다.

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
- 필요한 범위의 최소 Gradle task를 우선 사용하고, full clean build는 필요한 경우에만 실행한다.

## Agent Workflow

- 작업 전 짧은 구현 계획을 먼저 보고한다.
- 변경은 작고 원자적인 단위로 수행한다.
- 관련 없는 파일은 수정하지 않는다.
- 기존 사용자 변경사항을 되돌리지 않는다.
- 대규모 리팩터링, 의존성 추가/업그레이드, 포맷 전용 변경은 명시적 승인 없이 하지 않는다.
- 완료 전 변경 범위에 맞는 최소 검증을 실행하고, 실행하지 못한 검증은 보고한다.

## Project Skills

- Android 관련 로컬 skill은 `.skills/android/` 아래에 있다.
- Android 구현, 마이그레이션, 빌드 도구, 성능/R8 작업 전 관련 `SKILL.md`를 확인한다.
- 현재 포함된 Android skills: `android-cli`, `migrate-xml-views-to-jetpack-compose`, `edge-to-edge`, `navigation-3`, `r8-analyzer`, `agp-9-upgrade`.
- `android-cli` skill은 `android` 명령 설치가 필요하다. 사용 전 `command -v android`로 확인하고, 없으면 기존 Gradle/adb workflow를 사용한다.
- `.claude/`는 Claude 전용 설정/플러그인 자료로 취급한다. 매 작업마다 필수로 읽지 말고, 사용자가 요청하거나 Claude 전용 설정을 다룰 때만 참고한다.

## Serena

- Serena는 코드 구조 탐색, 심볼 검색, 참조 추적, 리팩터링이 필요할 때만 사용한다.
- 단순 문서 수정, 작은 텍스트 변경, 디자인/기획/자료 정리 작업에서는 Serena를 사용하지 않는다.
- `/Users/jeongjin-yong` 같은 넓은 상위 폴더를 Serena 프로젝트로 activate하지 않는다.

## Architecture And Resources

- 기존 MVVM/Clean Architecture 흐름과 모듈 경계를 유지한다.
- 공통 문자열은 `core:common`, 공통 UI는 `core:ui`, 디자인 리소스는 `core:designsystem` 우선 원칙을 따른다.
- 새 리소스를 만들기 전에 기존 리소스와 마이그레이션 규칙을 확인한다.
- 프로젝트 기존 유틸리티와 패턴을 새 추상화보다 우선한다.

## Coding Style

- Kotlin coding conventions와 주변 코드 스타일을 따른다.
- 의미 있는 이름과 작은 함수 단위를 선호한다.
- 공개 API나 설명이 필요한 타입/함수에는 KDoc을 사용한다.
- production code에 의미 없는 `//` 주석을 추가하지 않는다.

## Safety

- `local.properties`, 키스토어, API 키, Firebase/AdMob/지도/소셜 로그인 키 등 민감정보를 노출하지 않는다.
- destructive git 명령은 사용자가 명시적으로 요청한 경우에만 수행한다.
- 문서 변경은 사실을 코드베이스에서 확인한 뒤 반영한다.
