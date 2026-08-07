# 현재 모듈 의존성

이 문서는 실제 `settings.gradle.kts`와 각 `build.gradle.kts` 기준의 현재 상태를 기록한다. 목표 아키텍처나 마이그레이션 방향은 `docs/context/migration-rules.md`에 둔다.

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
  -> :common
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
- `:core:network`는 네트워크 인프라 외에 SDUI DTO와 일부 Compose/공통 UI 의존성을 가진다.
- `:common`은 legacy shared module로 남아 있으며, 신규 공통 코드는 우선 `:core:*` 계층을 확인한다.

## 작업 시 주의

- 문서나 계획에서 `home/domain`, `my/data` 같은 feature module 이름이 나오면 현재 실제 모듈이 아니라 목표 또는 과거 설계로 간주한다.
- 모듈 추가, 의존성 방향 변경, `common.gradle` 분리 작업은 사용자 승인 없이 진행하지 않는다.
- 의존성 변경이 발생하면 이 문서와 `docs/context/architecture-current.md`를 함께 갱신한다.
