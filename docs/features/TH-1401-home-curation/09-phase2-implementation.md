# TH-1401 2차 구현 기록

작성: 2026-10-08. [2차 실행 기록](08-phase2-ledger.md)의 승인 범위에 따라 최신 서버 계약과 실제 홈을 연결했다. 아래는 최종 코드를 확인한 구현 기록이다. 전체 빌드·유닛 테스트·에뮬레이터 QA의 실제 결과와 남은 범위는 [10 검증 기록](10-phase2-verification.md)에 기록했다.

## 공통 SDUI

- 홈 탭·캐러셀·카테고리·카드 응답 모델을 `core/common/.../sdui/model/section/home/SDHomeCurationModels.kt`, 렌더러를 `core/ui/.../sdui/`에 배치했다. 텍스트·이미지·칩·헤더·스타일·링크·로그는 기존 공통 primitive를 사용한다.
- 가게 카드는 기존 `ImagePreviewCardModel`을 사용한다. `SDImagePreviewCard`에 크기·modifier·이미지 표시 옵션을 받아 홈은 100dp 카드와 한 줄 제목을 표시하고 기존 상세의 기본 128dp를 유지한다. Preview와 debug 디자인 화면도 새 공통 경로를 사용한다.
- 공통 `SDAdMobCardModel`과 `SDCardType.ADMOB_CARD`를 추가해 `cardId/height/clickLog/impressionLog`를 표현한다. 기존 `SDCardDeserializer`에는 이 광고 분기만 추가했다. 기존 홈 `serverdriven` DTO/매퍼와 상세의 `SDUIGson` 파서를 하나로 통합하지 않았다.

## 서버 계약과 데이터 경계

- `ServerApi`의 기본 Gson 설정을 유지했다. `HomeCurationResponse.kt`의 concrete DTO를 `HomeCurationScreenMapper`에서 공통 SDUI 모델로 변환하고 `ScreenRemoteDataSource` → `ScreenRepository`로 제공한다. 네트워크 응답에 `SDCardModel` 인터페이스를 직접 선언하지 않는다.
- 섹션은 `items`, 캐러셀은 `carouselId/header/defaultCategoryId/categoryFilters/cards`를 받는다. `IMAGE_PREVIEW_CARD`의 HTML·`metricLabel/contextLabel`·링크·로그와 광고 높이, 서버 배열 순서를 보존한다. 캐러셀 노출 로그를 추가하거나 `refs`에서 링크·로그를 합성하지 않는다.
- `HOME_BOTTOM_SHEET_TAB`을 `HomeScreenSection.HomeBottomSheetTabSectionModel`로 전달한다. `CURATION/STORE_LIST` 이외의 `viewType`과 미지원 카드는 제외하고, 미지원 item은 `Unknown`으로 보존해 UI가 건너뛴다. 기존 홈 filter/map control/list와 cursor 매핑은 유지한다.
- `SDChipResponse`에 선택 필드 `imageAlignment/contentSpacing`, `SDImageStyleResponse`에 `dimmed`를 추가했다. 새 경로는 `SDClickLogResponse/SDImpressionLogResponse`를 재사용해 `eventType`과 `tabId/carouselId/categoryId/store_id/value` 등의 실제 문자열 extras를 보존한다.

| Repository 메서드 | GET 경로 | 전달 조건 |
|---|---|---|
| `getHomeCurationSection` | `/api/v1/screen/home/section/curation/{tabId}` | `mapLatitude/mapLongitude` query, 선택 `X-Device-Latitude/Longitude` header |
| `getHomeCurationCards` | `/api/v1/screen/home/section/curation/{tabId}/carousel/{carouselId}/cards` | 위 좌표 조건 + `categoryId` query |

실서버 fixture는 서울시청 공개 테스트 좌표로 캡처한 순수 `data` JSON이다. 사용자 위치나 인증 토큰을 사용하지 않았고, unknown 변형은 이 응답을 복사해 값만 변경했다.

## 실제 홈 연결

- `HomeViewModel`은 홈 설정에서 지원 탭 순서와 서버 기본 선택을 반영하고, 선택 탭이 `CURATION`일 때 section을 조회한다. section의 초기 `cards`를 바로 사용하며 초기 화면에서 캐러셀 cards API를 다시 호출하지 않는다.
- 탭·칩은 원본 `clickLog`를 `SDLogSender`로 전달한다. 카드와 헤더 링크 이벤트는 `SharedFlow`로 `HomeFragment`에 전달한다. `/store` APP_SCHEME은 기존 `StoreDetailSduiActivity`, 다른 지원 링크는 기존 홈 링크 처리 경로를 사용한다.
- `HomeFragment`는 상태를 lifecycle에 맞춰 수집하고 `HomeBottomSheetContent`에 탭·칩·카드·재시도 callback을 전달한다. 섹션 로딩/실패는 전체 상태 화면, 칩 로딩/실패는 해당 캐러셀의 상태와 재시도로 표시한다. 빈 결과도 정상 결과로 표시한다.
- 칩 조회 중에는 기존 선택과 카드를 유지한다. 성공하면 해당 캐러셀의 선택과 카드를 함께 교체하며, 실패하면 이전 표시를 유지하고 요청한 카테고리로 재시도할 수 있다.

## 조회 조건·응답 세대·복원 정책

- `HomeCurationQueryPolicy`는 지도/선택 기기 좌표를 `HomeCurationQuerySnapshot(location, revision)`으로 확정한다. 첫 위치 확정, 명시적 지도 재검색, 주소 선택은 새 snapshot/revision을 만든다. 주소 선택은 새 지도 좌표를 먼저 적용한 뒤 조회한다.
- 지도 이동만으로는 큐레이션 조회 조건을 바꾸지 않는다. 전역 필터의 비강제 조회는 기존 큐레이션 snapshot을 유지한다. 같은 revision·좌표·탭의 진행 중 요청 또는 성공 결과를 재사용하고 실패 결과는 재사용하지 않는다.
- 섹션 요청을 새로 시작하면 이전 섹션/칩 요청을 취소한다. `HomeCurationStateReducer`는 섹션 요청 ID와 캐러셀별 요청 ID를 검사해 늦은 응답과 이전 섹션의 칩 응답을 무시한다.
- 동일 조회 조건의 탭 왕복은 선택 카테고리·카드·스크롤을 유지한다. 새 section 조회 성공 시 서버 `defaultCategoryId`와 포함된 초기 cards로 초기화한다. 시트의 큐레이션 세로 스크롤은 새 섹션 요청 시 맨 위로 이동하고 가로 카드 스크롤 상태도 새 섹션에 맞춰 생성한다.

## 주변 목록·지도·시트

- 주변 목록은 기존 조회를 병행해 지도 마커를 계속 공급한다. 전역 정렬/카테고리/지도 필터는 기존 목록·마커에 적용하며 큐레이션 API에 임의의 filter query를 추가하지 않는다.
- 주변 탭은 기존 `HomeListContent`, `fetchNextHomeListSection`, cursor paging과 카드 → 지도 이동 → 가게 미리보기 흐름을 사용한다. 큐레이션 카드 링크는 마커 입력과 분리했다.
- 주변/큐레이션의 세로 스크롤 상태를 따로 유지하고 활성 탭의 상태로 시트 nested scroll 최상단을 판정한다. 기존 접힘/full 단계, 지도 보기 버튼과 가게 미리보기·상세 시트 경로를 유지한다.

## 검증 상태와 후속 범위

- API 경로, 광고 파서와 DTO 매퍼는 예상 RED 실패를 먼저 관측한 뒤 구현했다. `:data:testDebugUnitTest` 51개와 `:core:network:testDebugUnitTest` 14개가 통과했고, 이 중 TH-1401 신규 테스트는 12개다.
- 순수 상태 테스트 20개와 조회 정책 테스트 11개도 RED → GREEN으로 확인했다. 실제 ViewModel 연결을 포함한 전체 빌드·유닛·에뮬레이터 QA의 최종 결과는 후속 검증 기록에 별도로 남긴다.
- 최종 독립 코드 리뷰에서 중요한 미해결 결함이 보고되지 않았다. UI/통합 검증의 별도 증거는 10 검증 기록에 있다.
- 큐레이션 광고는 공통 모델과 서버 높이를 받는 슬롯까지 준비했다. 실제 홈은 `adMobContent`를 주입하지 않아 광고 item/card를 표시하지 않는다. 광고 SDK 공급·가로 슬롯 형식/폭·로드 실패 처리·실제 노출/클릭 이벤트·중복 방지와 실광고 확인은 후속 범위다. TC-7 전체를 완료로 처리하지 않는다.
- 의존성·toolchain 버전·원격 스펙·커밋·푸시·배포는 이 단계에서 변경하지 않았다.
