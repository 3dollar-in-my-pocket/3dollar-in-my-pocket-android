# UI 구현 기록

## 변경

- `HomeCurationModels.kt`: 홈 namespace에서 탭·카테고리·캐러셀·카드와 공통 광고 입력 선언. 캐러셀 목록과 카드 응답을 분리하고 원본 type/viewType/link/log를 유지한다.
- 기존 홈 SDHeader/SDChip/SDButton에는 subTitle/trailingAction, imageAlignment/contentSpacing, clickLog를 기본값이 있는 optional 필드로 추가했다. 기존 DTO 매퍼와 파서는 변경하지 않았다.
- `SDImagePreviewCardLayout`: 모델 없는 image/content slot 레이아웃을 추출했다. 기존 상세 카드의 128dp 기본값·테두리·사진·텍스트·클릭은 유지한다.
- `HomeCurationElements.kt`: 홈 표시 모델을 기존 SDText/SDImage/sdSurface 요소의 입력으로 변환한다. HTML span, 서버 색/두께/테두리, 이미지 dimmed와 정규화된 빈 텍스트를 처리한다.
- `SDHomeCurationComponents.kt`: 48dp 탭, SDChip 상태별 카테고리, 100dp 프리뷰 카드와 캐러셀. 모델 원본을 callback으로 돌려준다.
- `SDHomeCurationView.kt`: 선택 상태를 외부 입력으로 받는다. 미지원 viewType/item/card는 제외하고 탭이 없는 입력은 nearby slot으로 표현한다. 카드·광고·빈 상태·주변 목록을 주입할 수 있다.
- Preview는 기존 element/component/section/screen 그룹과 AppTheme 패턴을 따르며 Figma 예시는 표시 fixture에만 둔다.
- `HomeCurationDesignActivity`: debug 진입점과 Figma 원본 로컬 PNG. 카드 클릭은 debug Toast/Log이며 실제 내비게이션이나 analytics는 연결하지 않는다.

## 확인 중 수정

- 기본 sandbox의 Gradle lock 접근 실패는 기존 cache 접근 권한으로 해결했다.
- offline build의 기존 viewbinding 캐시 누락은 버전을 바꾸지 않고 정상 의존성 다운로드로 해결했다.
- Debug Activity의 Toast Context를 명시해 Compose receiver와 구분했다.
- 시각 비교에서 placeholder asset 크기와 Figma 원본 emoji를 바로잡았다.
- 코드 리뷰에서 기존 홈 mapper의 null → 빈 문자열 정규화를 확인하고 image-only chip/button은 빈 Text를 생략한다.

## 후속 범위

홈 DTO/매퍼/API·HomeViewModel 연결, 캐러셀별 요청 취소/로딩/오류, 로그 노출 기준, 실제 광고 SDK·상세 이동은 구현하지 않았다. 실홈과 주변 목록의 기존 소스는 수정하지 않았다.

## 2026-10-08 2차 후속

위 기록은 1차 UI 범위의 당시 결과다. 최신 공통 SDUI·서버 API·실홈 연결은 [09 구현 기록](09-phase2-implementation.md), 전체 253개 유닛·빌드와 실홈 QA 결과 및 광고 후속 상태는 [10 검증 기록](10-phase2-verification.md)에 별도로 기록했다.
