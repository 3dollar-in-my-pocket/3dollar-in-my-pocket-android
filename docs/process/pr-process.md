# PR 프로세스 (한 장 요약)

AI가 코드를 많이 쓰는 환경에서 사람 리뷰어는 **diff 전체가 아니라 의도(TC) · 모듈 경계 · 검증 증거**만 본다.
나머지(스타일, 구조 규칙, 회귀)는 스크립트·테스트·AI가 맡는다. 이 문서는 전체 흐름과 도구 매핑, 그리고 PR 위험도별 요구 수준을 정한다.

iOS 레포(`3dollars-in-my-pocket-ios`)와 **같은 형태·같은 용어**를 쓴다. 테크스펙(노션)·지라 필드·TC 번호는 두 플랫폼이 공유한다.

## 흐름과 도구

| 단계 | 하는 일 | 도구 | 산출물이 가는 곳 |
|---|---|---|---|
| 1. 의도 정의 | 지라 티켓에서 테크스펙 생성, 요약·요구사항·TC 작성 | 지라 Actions "테크스펙 생성" → 노션 | 지라 `테크스펙` 필드 (단일 진실 소스) |
| 2. 코드 작성 | 모듈 경계·리소스 규칙 안에서 구현 | `AGENTS.md`, `docs/context/module-dependencies-current.md`, `docs/context/resource-rules-current.md`, `scripts/check-module-deps.sh` | 로컬 + `lint.yml` |
| 3. 테스트 | TC를 세 계층(유닛/자동화/수동)에 배정 → 승인 → 유닛만 코드 | `/3dollars:test-cases`, `docs/process/testing.md` | `*/src/test/**`, 메서드명 `` `TH1234_TC1_…` `` |
| 4. 동작 증거 | 유닛 결과·TC 커버리지 표 / 자동화 TC 스크린샷·영상 | `test.yml` + `scripts/test-summary.sh`, `3dollars:simulator-test`(에뮬레이터) | PR 코멘트(자동), 본문 "증거" |
| 5. 스코프 드리프트 | 요구사항 ↔ diff 대조, 스펙 밖 변경 목록 | `/3dollars:drift` | 본문 "스펙 밖 변경" |
| 6. 자동화·수동 범위 | 유닛으로 안 덮이는 TC를 자동화(에이전트·에뮬레이터)와 수동(사람·실기기)으로 가르고 UI 변경 공통 체크 추가 | `docs/process/e2e-and-manual-tests.md` (pr-body가 자동 삽입) | 본문 "TC" 체크리스트 |
| 7. 검증 장치 변경 | 스크립트·CI·규칙 문서를 건드린 PR 표시 | `.github/labeler.yml` + `labeler.yml`, `docs/process/verification-change.md` | 라벨 + 파일 목록 코멘트 |
| 8. 설명 못 하는 부분 | 비자명한 결정 3개를 작성자에게 질문 | `/3dollars:ask-author` | 본문 "설명이 필요한 결정" |
| 9. 반복 지적 → 규칙 | 리뷰 코멘트 집계, 3회↑ 승격 제안 | `/3dollars:review-digest` | 규칙 문서·스크립트 변경 PR |
| PR 생성 | 위 결과를 템플릿에 채워 생성/갱신 | `/3dollars:pr-body` (`.github/PULL_REQUEST_TEMPLATE.md`) | GitHub PR |
| 리뷰 | 의도·경계·증거 기준 리뷰 | `/3dollars:pr-code-review` + 사람 | PR 코멘트 |

작성자 기준 순서: **테크스펙 → 구현 → `/3dollars:test-cases` → `./gradlew testDebugUnitTest` + `scripts/check-module-deps.sh` → (UI면) `simulator-test` → `/3dollars:pr-body`** (pr-body가 drift·ask-author·체크리스트를 안에서 호출).

## 위험도: 경량 / 풀코스

PR 본문 첫 줄 `위험도:`에 적는다. `/3dollars:pr-body`가 아래 기준으로 판정하고, 작성자가 바꿀 수 있다(바꾸면 사유 한 줄).

| | 경량 | 풀코스 |
|---|---|---|
| **조건** | 아래 전부: 문서/테스트/CI만 바뀜 **또는** `:app` 안 단일 화면 패키지의 UI·로직 변경, `:core:*`·`:domain`·`:data`·`:common` 미변경, `gradle/libs.versions.toml`·`common.gradle`·`AndroidManifest.xml` 미변경, 로그인·결제·딥링크·푸시·권한 흐름 미변경 | 하나라도: `module-boundary`·`build-config` 라벨, `:core:*`·`:domain`·`:data`·`:common` 변경, 새 API·Repository, 로그인·결제·딥링크·푸시·권한·광고 흐름, Manifest/권한 변경, 3개 이상 모듈 동시 변경 |
| **자동 검증** | `lint.yml`, `test.yml` | 동일 |
| **본문 필수** | 의도 링크, 변경 불릿, 자동화·수동 TC 체크(해당 시) | + `drift` 요구사항 표 **전체**(스펙 밖 변경만이 아니라), `ask-author` Q/A, UI 변경이면 `simulator-test` Before/After 증거 |
| **사람 리뷰** | 본문만 보고 승인 가능. 스펙 밖 변경이 "없음"이면 diff를 열 필요 없음 | 라벨이 가리키는 파일(검증 장치·Gradle·모듈 경계)을 **먼저** 읽고, 그다음 drift 표의 "부분/없음" 항목 |
| **머지 조건** | CI 초록 + 자동화·수동 TC 전부 체크(자동화는 증거 링크 포함) | + 리뷰 승인 1명 |

라벨은 표시일 뿐이며 워크플로를 조건 실행하지 않는다. 풀코스 요구사항은 작성자와 리뷰어가 본문으로 확인한다.

## 모듈 경계 검사가 잡는 것 / 못 잡는 것

`scripts/check-module-deps.sh`는 **선형 계층 랭크**(`:app` 50 → `:data` 40 → `:domain` 30 → `:common` 20 → `:core:network` 15 → `:core:ui` 12 → `:core:common` 11 → `:core:abtest`·`:core:designsystem` 10)로 "자기보다 낮은 계층만 의존한다"를 검사한다.

- **잡는 것**: 상위로 거슬러 올라가는 의존. `:core:* → :app/:data/:domain/:common`, `:domain → :data`, `:core:ui → :core:network` 등
- **못 잡는 것**: 랭크상 합법이지만 아키텍처 의도에는 어긋나는 쌍. 현재 `:domain → :core:network`, `:core:network → :core:ui`, `:core:network → :core:designsystem` 3건이 여기 해당하며, 전부 "SDUI 렌더링 코드가 `:core:network` 안에 있다"는 한 원인에서 나온다. 목록과 사유는 `scripts/module-deps-baseline.txt` 주석에, 해소는 **TH-1355**에 있다
- 선형 랭크로는 "`:data`는 `:core:network`를 써도 되지만 `:domain`은 안 된다"를 표현할 수 없다. 명시적 금지쌍 규칙 도입도 TH-1355에서 함께 검토한다

## 예외

- **핫픽스**(`bugfix/`, `hotfix/`): 테크스펙 생략 가능. 대신 본문 "의도"에 장애 내용 1줄 + 재현 경로, 풀코스 취급.
- **릴리즈 브랜치**(`release/`): `versionCode`·`versionName`·릴리즈 노트 변경만이면 경량.
- **의존성 업데이트**: `gradle/libs.versions.toml`·`common.gradle` 변경은 **풀코스**이며, `AGENTS.md` "버전 변경이나 의존성 업그레이드는 사용자 승인 없이 하지 않는다"가 그대로 적용된다.

## 규칙을 바꾸고 싶을 때

이 문서, `AGENTS.md`/`CLAUDE.md`, `docs/context/*`, `scripts/check-module-deps.sh`는 **함께** 바뀐다.
입구는 `/3dollars:review-digest`(반복 지적) 또는 `verification-change` 라벨이 붙는 PR이며, 스크립트·CI 변경은 적용 전에 확인받는다.

## iOS 레포와 다른 점

| 항목 | iOS | AOS |
|---|---|---|
| 스타일 린트 | SwiftLint + 베이스라인 | **없음** — ktlint/detekt 도입은 의존성 추가라 별도 승인 필요(후속 과제) |
| SDUI 렌더링 위치 | `Modules/Core/SDU` (분리 완료) | `:core:network` 안 `sdui/ui/**` (분리 예정 — TH-1355) |
| 모듈 경계 검사 | `scripts/check-module-deps.sh` (Tuist `Project.swift`) | `scripts/check-module-deps.sh` (`build.gradle.kts`의 `project(":…")`) |
| 유닛 테스트 | `xcodebuild test` / XCTest | `./gradlew testDebugUnitTest` / JUnit4 |
| 자동화 TC 실행체 | iOS 시뮬레이터 | Android 에뮬레이터 |
| 아키텍처 규칙 문서 | `docs/architecture/RULES.md` (R1~R10) | `docs/context/architecture-current.md` + `docs/context/module-dependencies-current.md` |
| Dev 빌드 배포 | Xcode Cloud → TestFlight | `firebase-distribution.yml` → Firebase App Distribution |
