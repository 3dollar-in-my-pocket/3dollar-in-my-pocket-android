# 현재 모듈 의존성

이 문서는 실제 `settings.gradle.kts`와 각 `build.gradle.kts` 기준의 현재 상태를 기록한다. 목표 아키텍처나 마이그레이션 방향은 `docs/context/migration-rules.md`에 둔다.

의존 방향은 `scripts/check-module-deps.sh`가 CI(`lint.yml`)에서 강제한다. 규칙은 두 가지다.

- **랭크**: 자기보다 낮은 계층만 의존한다 (`:app` → `:data` → `:domain` → `:common` → `:core:*`).
- **금지쌍**: 랭크로는 표현할 수 없어 명시적으로 막는 간선.
  - `:domain` → `:core:network` — domain 은 네트워크 DTO·Retrofit 타입을 모른다.
  - `:domain`·`:data`·`:core:network` → `:core:ui`·`:core:designsystem` — UI 계층은 UI 를 그리는 모듈만 의존한다.

아래 그래프를 바꾸면 그 스크립트의 랭크 표와 금지쌍도 함께 확인한다.

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

:common
  -> :core:common

:core:network
  -> :core:common
  -> :core:abtest

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
- `:domain`은 현재 순수 Kotlin domain이 아니다. Android library이고 `:core:common`, AndroidX, Retrofit, Hilt에 의존한다. 프로젝트 모듈은 `:core:common` 하나만 의존한다(TH-1355에서 `:core:network` 제거).
  - Retrofit 번들은 아직 남아 있다. `HomeRepository`가 이미지 업로드에 `okhttp3.MultipartBody`를 인터페이스에 노출하기 때문이다. 이것을 걷어내야 Retrofit 의존을 뗄 수 있다.
- **서버드리븐 시스템이 두 벌 있다.** 둘 다 모델은 `:core:common`, 렌더러는 `:core:ui`에 있다.

  | | 홈 화면용 | 가게 상세용 |
  |---|---|---|
  | 모델 | `com.threedollar.common.serverdriven.model` | `com.threedollar.common.sdui.model` |
  | 렌더러 | `com.zion830.threedollars.core.ui.serverdriven` | `com.zion830.threedollars.core.ui.sdui` |
  | 파싱 | 네트워크 DTO(`core/network/data/screen`) → `:data` 매퍼 → 모델 | Gson 커스텀 디시리얼라이저(`core/network/sdui/core`)가 모델로 바로 파싱 |

  `SDScreenModel`·`SDSectionModel`·`SDHeaderModel`·`SDChipModel`·`SDImageModel`·`SDTextModel`은 패키지만 다르게 양쪽에 존재한다. 파싱 계약이 달라(홈은 `text: String`·`type: String`+sealed, 가게 상세는 `text: String?`·`enum`+`valueOf`) 합치면 파싱 동작이 바뀌므로 TH-1355에서는 배치만 맞추고 통합하지 않았다.
- `:common`은 legacy shared module로 남아 있으며, 신규 공통 코드는 우선 `:core:*` 계층을 확인한다.

## 작업 시 주의

- 문서나 계획에서 `home/domain`, `my/data` 같은 feature module 이름이 나오면 현재 실제 모듈이 아니라 목표 또는 과거 설계로 간주한다.
- 모듈 추가, 의존성 방향 변경, `common.gradle` 분리 작업은 사용자 승인 없이 진행하지 않는다.
- 의존성 변경이 발생하면 이 문서와 `docs/context/architecture-current.md`를 함께 갱신한다.
- 새 의존을 넣기 전에 `scripts/check-module-deps.sh`를 돌린다. 실패하면 **먼저 방향을 뒤집는 설계를 검토하고**, 베이스라인 추가는 마지막 수단이다 (`scripts/module-deps-baseline.txt`는 줄어들기만 해야 한다).
