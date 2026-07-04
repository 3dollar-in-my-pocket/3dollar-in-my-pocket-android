# 현재 리소스 규칙

## 문자열

- 기본 원칙: 공통 문자열은 `core/common/src/main/res/values/strings.xml`에 둔다.
- `app/src/main/res/values/strings.xml`에는 manifest reference용 앱 이름 등 앱 모듈에 남아야 하는 값만 둔다.
- `common/src/main/res/values/strings.xml`에는 legacy shared module용 문자열이 일부 남아 있다.
- 다른 모듈에서 `core:common` 문자열을 사용할 때는 기존 패턴을 따라 `com.threedollar.common.R` alias를 우선 확인한다.

## 디자인 리소스

- colors, dimens, fonts, themes, styles, drawable selector/icon/background는 `core:designsystem`을 우선 확인한다.
- 새 drawable/color를 만들기 전에 `core/designsystem/src/main/res/` 전체를 검색한다.
- debug 전용 디자인 검증 asset은 `app/src/debug/res/`에 둘 수 있다.

## 공통 UI

- 재사용 가능한 View/Compose component는 `core:ui`를 우선 확인한다.
- feature 전용 XML layout과 Compose screen은 기존 feature package 주변에 둔다.
- `SimpleRatingBar`는 `core:ui`에서 `api`로 노출 중이므로 feature 모듈에 중복 추가하지 않는다.

## 변경 시 체크

- 문자열, 색상, drawable 이동은 빌드뿐 아니라 runtime resource namespace를 확인한다.
- resource migration 후에는 관련 모듈 최소 빌드와 해당 화면 smoke를 수행한다.
- 리소스 원칙을 바꿨다면 `docs/context/migration-rules.md`와 이 문서를 함께 갱신한다.
