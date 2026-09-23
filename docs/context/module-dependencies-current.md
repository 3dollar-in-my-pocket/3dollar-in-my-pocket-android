# 현재 모듈 의존성

이 문서는 실제 `settings.gradle.kts`와 각 `build.gradle.kts` 기준의 현재 상태를 기록한다. 목표 아키텍처나 마이그레이션 방향은 `docs/context/migration-rules.md`에 둔다.

의존 방향은 `scripts/check-module-deps.sh`가 CI(`lint.yml`)에서 강제한다. 아래 그래프를 바꾸면 그 스크립트의 랭크 표도 함께 확인한다.

## 모듈 목록

원천: `settings.gradle.kts`

- `:app`
- `:common`
- `:core:network`
- `:core:common`
- `:core:ui`
- `:core:designsystem`
- `:core:abtest`
- `:domain`
- `:data`

## 프로젝트 의존성 그래프

```text
:app
  -> :common
  -> :core:network
  -> :core:common
  -> :core:ui
  -> :core:abtest
  -> :domain
  -> :data
  -> :core:designsystem

:data
  -> :core:network
  -> :core:common
  -> :domain

:domain
  -> :core:common
  -> :core:network

:common
  -> :core:common

:core:network
  -> :core:common
  -> :core:abtest
  -> :core:designsystem
  -> :core:ui

:core:common
  -> :core:designsystem

:core:ui
  -> :core:common
  -> :core:designsystem

:core:designsystem
  -> no project module dependency

:core:abtest
  -> no project module dependency
```

## 현재 구조 해석

- `:app`은 화면, Activity, Fragment, ViewModel, navigation, legacy datasource가 모여 있는 실질 presentation 모듈이다.
- `:data`와 `:domain`은 feature별 하위 패키지로 `home`, `screen`, `my`, `community`, `login`, `store` 등을 나눈다.
- `:domain`은 현재 순수 Kotlin domain이 아니다. Android library이고 `:core:network`, AndroidX, Retrofit, Hilt에 의존한다.
- `:core:network`는 네트워크 인프라 외에 **서버 주도 UI(SDUI) 렌더링 Compose 코드**(`sdui/ui/**`, 7개 파일)를 함께 갖고 있다. 이 때문에 네트워크 모듈이 UI 레이어를 역으로 의존한다 — `:core:ui`(`noRippleClickable`)와 `:core:designsystem`(`base.compose.{Gray50,Gray10,Gray0,dpToSp}`)을 각각 `sdui/ui/**` 4개 파일이 쓴다. 분리는 TH-1355.
- `:domain`의 `LoginRepository.kt`·`StoreRepository.kt` 2개 파일이 `:core:network`의 DTO(`SignUser`, `LoginRequest`, `SignUpRequest`, `PushInformationRequest`, `SDScreenModel`)를 인터페이스 시그니처에 노출한다. 같은 뿌리의 부채이며 TH-1355에서 함께 다룬다.
- `:common`은 legacy shared module로 남아 있으며, 신규 공통 코드는 우선 `:core:*` 계층을 확인한다.

## 작업 시 주의

- 문서나 계획에서 `home/domain`, `my/data` 같은 feature module 이름이 나오면 현재 실제 모듈이 아니라 목표 또는 과거 설계로 간주한다.
- 모듈 추가, 의존성 방향 변경, `common.gradle` 분리 작업은 사용자 승인 없이 진행하지 않는다.
- 의존성 변경이 발생하면 이 문서와 `docs/context/architecture-current.md`를 함께 갱신한다.
- 새 의존을 넣기 전에 `scripts/check-module-deps.sh`를 돌린다. 실패하면 **먼저 방향을 뒤집는 설계를 검토하고**, 베이스라인 추가는 마지막 수단이다 (`scripts/module-deps-baseline.txt`는 줄어들기만 해야 한다).
