# 현재 아키텍처

## 큰 흐름

현재 앱은 MVVM과 Clean Architecture 지향 구조가 섞여 있다. 모듈은 `app`, `data`, `domain`, `core:*`, legacy `common`으로 나뉘지만, 완전히 순수한 layer 분리는 아니다.

```text
UI / Presentation
  app
    -> ViewBinding, DataBinding, Compose interop, Activity/Fragment/ViewModel

Feature package layers
  data
    -> repository implementation, datasource, mapper, DI
  domain
    -> repository interface, usecase, model

Shared layers
  core:network
    -> Retrofit API, DTO, interceptors, SDUI network model
  core:common
    -> strings, analytics, common model, base, utils
  core:ui
    -> reusable UI and Compose components
  core:designsystem
    -> colors, drawables, fonts, themes, styles, Compose theme
  core:abtest
    -> Firebase Remote Config based AB test support
  common
    -> legacy shared base classes and resources
```

## 현재와 목표의 차이

- `:domain`은 현재 Android library이며 Retrofit, AndroidX, Hilt에 의존한다. 프로젝트 모듈은 `:core:common`만 의존한다. Retrofit은 `HomeRepository`의 `okhttp3.MultipartBody` 때문에 남아 있다.
- 서버 주도 UI는 모델을 `:core:common`, 렌더러를 `:core:ui`에 둔다(홈·가게 상세 공통). `:core:network`에는 Gson 디시리얼라이저만 남는다. iOS의 `Modules/Core/SDU`와 역할이 같다.
- `common.gradle`은 여러 모듈에 공통 Android 설정과 외부 라이브러리를 주입한다.
- feature가 독립 Gradle module로 쪼개진 구조가 아니라, `app`, `data`, `domain` 내부 package로 나뉜 구조다.

## 작업 원칙

- 새 기능은 현재 모듈 구조를 존중하고, 대규모 layer 정리는 별도 migration 작업으로 분리한다.
- UI 공유 컴포넌트는 `core:ui`, 디자인 리소스는 `core:designsystem`, 문자열은 `core:common`을 우선 확인한다.
- 서버드리븐 홈 관련 작업은 `core/network/data/screen`, `data/screen`, `core/common/serverdriven`, `core/ui/serverdriven`, `app/ui/home` 흐름을 함께 확인한다.
- 서버드리븐 가게 상세(관련 가게 섹션) 작업은 `core/network/sdui/core`(Gson 디시리얼라이저), `core/common/sdui/model`, `core/ui/sdui`, `app/ui/storeDetail` 흐름을 함께 확인한다.
- 지도/위치 관련 작업은 `app/ui/map`, `app/ui/home`, `NaverMapUtils`, 권한 흐름을 같이 확인한다.
