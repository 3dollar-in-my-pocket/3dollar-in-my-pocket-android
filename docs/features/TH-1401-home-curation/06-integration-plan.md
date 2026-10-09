# TH-1401 공통 SDUI 정리와 API 연동 준비 계획

**상태:** 2026-10-07 분석과 2차 범위 설명 후 사용자가 구현 진행을 승인했다. 아래 준비 계획에 실홈 상태·링크·클릭 로그 연결을 포함해 실행한다. 로직은 테크스펙 TC 기반 유닛 검증, 화면은 에뮬레이터 검증으로 확인하며 기존 표시 UI TDD 예외를 유지한다. 실광고 SDK는 광고 형식·폭 확정 뒤 후속 범위다.

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox syntax for tracking.

**Goal:** 최신 서버 계약에 맞춘 공통 SDUI 모델/UI와 큐레이션 Repository를 실제 홈에 연결한다.

**Architecture:** 큐레이션의 새 모델과 UI는 `common.sdui.model` / `core.ui.sdui` 계층에 배치한다. 카드·텍스트·이미지·칩·로그는 기존 공통 모델을 재사용하고 구형 홈 filter/list 파싱은 유지한다. ServerApi는 concrete DTO를 받아 data mapper에서 공통 SDUI 모델로 변환한다.

**Tech Stack:** 기존 Kotlin/Compose/Gson/Retrofit/Hilt/JUnit4, JDK17. 의존성과 버전은 변경하지 않는다.

**Spec:** https://app.notion.com/p/3f07ad52990e8114b723e1d2d7a64d80 ; 최신 계약 사실은 `05-server-update-2026-10-07.md`.

## 범위와 승인

- 2차 승인 범위는 모델·UI 배치 정리, 최신 DTO/매퍼/API/Repository와 실홈 ViewModel·클릭 로그·상세 이동 연결이다. 실광고 SDK는 후속 범위다.
- 기존 UI 구현/표시 UI TDD 예외 승인은 유지한다. 사용자가 2차 구현 진행을 승인했으므로 아래 TC 계층 배정으로 API 파싱·조회 로직 테스트와 화면 검증까지 실행한다.
- 기존 사용자 변경과 artifacts/는 보존한다. 이동은 이번 작업에서 추가한 큐레이션 파일에만 적용한다. 구형 홈/상세 파서 전체 통합을 하지 않는다.
- 테크스펙 TC-1~12가 현재 ID 원천이다. 과거 UI 단계의 로컬 TC-1~5와 동일 번호로 간주하지 않는다.
- 원격 스펙/티켓/PR 수정, 커밋/푸시/배포는 이 계획에 포함하지 않는다.

## Review Focus

- 미지원 type/viewType: 해당 항목을 제외하고 구형 응답을 안전하게 처리한다(TC-1/8).
- 초기 cards: 섹션 응답의 cards/defaultCategoryId를 보존하고 초기 카드 API 중복 호출을 만들지 않는다(TC-4).
- 메타·링크·로그: HTML/metricLabel/contextLabel/refs/clickLog를 원형대로 보존한다(TC-4/6).
- 광고: items/cards 모두에서 cardId/height/clickLog/impressionLog를 보존한다(TC-4/7).
- 기존 UI: 상세128dp 기본값과 기존 filter/list 파싱·cursor를 유지한다(TC-10/11/12).

## Task 1: 공통 SDUI 모델과 UI 배치

**Files:**
- 신규 큐레이션 모델: `core/common/src/main/java/com/threedollar/common/sdui/model/section/home/`.
- 새 탭·카테고리: `core/ui/src/main/java/com/zion830/threedollars/core/ui/sdui/component/`.
- 캐러셀: `core/ui/src/main/java/com/zion830/threedollars/core/ui/sdui/section/home/SDHomeCurationCarousel.kt`.
- 전체 뷰: `core/ui/src/main/java/com/zion830/threedollars/core/ui/sdui/section/home/SDHomeCurationView.kt`.
- Preview/fixture: 기존 `core/ui/.../sdui/preview/`.
- 수정: 공통 `SDCardModel.kt`, `SDImagePreviewCard.kt`, debug `HomeCurationDesignActivity.kt`.

**Interfaces:** 새 `SDHomeCurationSectionModel.items`의 Carousel은 header/defaultCategoryId/categoryFilters/cards를 갖는다. 카드값은 기존 `ImagePreviewCardModel`을 사용하고 광고는 공통 `SDAdMobCardModel`로 표현한다. 제어 상태와 원본 모델 callback을 받는 현재 뷰 API의 역할은 유지한다.

- [x] 기존 공통 primitive와 최신 필드를 사용해 이번 큐레이션 모델만 정리한다. 이전 PreviewCard/chip group/캐러셀 impressionLog는 최신 계약으로 대체한다.
- [x] 공통 카드의 크기 옵션을 추가한다. 기존 상세 기본128dp를 유지하며 홈은100dp와 한 줄 제목으로 그린다.
- [x] UI를 위 공통 계층으로 옮기고 기존 SDText/SDImage/SDChip/SDHeader/sdSurface를 직접 조합한다. 필요 없는 홈 primitive 변환 wrapper를 정리한다.
- [x] Preview/debug fixture·imports를 함께 맞춘다. 초기 cards를 그대로 보여주고 카테고리 변경 결과만 override한다.
- [x] 승인된 UI 검증 방식으로 compile/assemble과 해당 화면 증거를 확인한다.

## Task 2: concrete DTO → mapper → Repository

**Files:**
- Create: `core/network/src/main/java/com/threedollar/network/data/screen/HomeCurationResponse.kt`.
- Modify: `core/network/.../api/ServerApi.kt`, `HomeFilterScreenResponse.kt`.
- Create: `data/src/main/java/com/threedollar/data/screen/HomeCurationScreenMapper.kt`.
- Modify: `data/.../screen/HomeFilterScreenMapper.kt`, `ScreenRemoteDataSource.kt`/`Impl`, `ScreenRepositoryImpl.kt`, `domain/.../screen/repository/ScreenRepository.kt`.
- Tests: `data/.../screen/HomeCurationScreenMapperTest.kt`, 기존 `HomeFilterScreenMapperTest.kt`, `core/network/.../api/ServerApiTest.kt` 및 실응답 resources.

**Interfaces:**
- `HomeCurationSectionResponse.asCurationModel(): SDHomeCurationSectionModel`.
- `HomeCurationCardsResponse.asCurationCardsModel(): SDHomeCurationCardsModel`.
- `ScreenRepository.getHomeCurationSection(tabId: String, mapLatitude: Double, mapLongitude: Double, deviceLatitude: Double?, deviceLongitude: Double?): Flow<BaseResponse<SDHomeCurationSectionModel>>`.
- `ScreenRepository.getHomeCurationCards(tabId: String, carouselId: String, categoryId: String, mapLatitude: Double, mapLongitude: Double, deviceLatitude: Double?, deviceLongitude: Double?): Flow<BaseResponse<SDHomeCurationCardsModel>>`.

- [x] 승인한 TC-1/4/8의 파싱 단언을 먼저 작성한다. 지원 탭 순서·default, categoryFilters·초기 cards, IMAGE_PREVIEW_CARD 메타·HTML·link/log, 광고·unknown 안전처리를 확인한다. fixture는 실제 개발 서버 응답을 사용한다.
- [x] 테스트가 미구현 mapper/필드 때문에 실패하는 것을 확인한다.
- [x] 신규 응답은 concrete DTO로 선언한다. 기본 Gson인 ServerApi 응답에 `SDCardModel` 인터페이스를 직접 넣거나 기존 ServerApi Gson을 전체 교체하지 않는다.
- [x] mapper에서 기존 공통 카드와 primitive로 변환한다. 구형 filter/list 매퍼는 보존하고 탭 섹션만 확장한다.
- [x] 실제 controller의 단수 `/carousel/{carouselId}/cards` 경로, categoryId query, 지도 좌표 query/선택 기기 헤더를 API/Repository에 전달한다.
- [x] 해당 mapper/API 기존 테스트와 `:app:assembleDebug`, 모듈 의존 검사를 실행한다. 통과 이후 새 실패나 변경이 없으면 반복하지 않는다.

## 실제 홈 연결 — 2차 포함

TC-2/3/5/6/11/12를 기준으로 HomeViewModel·HomeFragment·HomeBottomSheetContent를 연결한다. 캐러셀별 요청 취소/응답 세대, 클릭 로그, 상세 링크, 탭별 스크롤과 기존 시트 nested scroll을 다룬다. 지도 재검색·주소 확정 시 서버 기본값을 조회하고, 카메라 이동·전역 필터·동일 조건의 탭 왕복에는 확정된 큐레이션 조건과 선택을 유지한다. 빈 데이터·오류·재시도도 연결한다. TC-7은 광고 모델/슬롯까지이며 SDK 노출·클릭 검증은 후속이다.

## TC 계층 배정 — 2차 승인 범위

| 테크스펙 TC | 제안 계층 | 검증 내용 |
|---|---|---|
| 1 | 유닛 + 자동화 | 응답 탭 순서/default와 실제 초기 화면 |
| 2, 3 | 유닛 + 자동화 | 선택 상태/clickLog와 영역 전환 |
| 4 | 유닛 + 자동화 | items 순서·초기 cards·defaultCategoryId와 표시 |
| 5 | 유닛 + 자동화 | 캐러셀별 교체·로그·늦은 응답 방지와 칩 전환 |
| 6 | 유닛 + 자동화 | link/log 보존·전송과 실제 상세 이동 |
| 7 | 유닛 + 자동화 + 수동 | 광고 모델·노출 중복/높이·테스트 광고, 실광고는 실기기 |
| 8 | 유닛 + 자동화 | unknown 파싱·안전 제외 |
| 9, 10 | 자동화 | null header 옵션·빈 공간, 긴 제목 말줄임 |
| 11 | 유닛 + 자동화 | 기존 query/cursor와 실홈 재조회 |
| 12 | 자동화 | tip/full·지도 보기·마커/미리보기 |

미지원 입력·요청 좌표는 해당 요구사항의 기술 단언으로 검증하며 별도 임의 TC 번호를 만들지 않는다. ViewModel 테스트 라이브러리를 새로 추가하지 않고 순수 상태/요청 로직에 필요한 검증을 배치한다.

## 2차 실행 결과

공통 SDUI·데이터·실홈 연결을 구현하고 전체 유닛 253개, Debug 빌드, 모듈 의존 검사와 에뮬레이터 검증을 통과했다. 세부 구현은 [09 구현 기록](09-phase2-implementation.md), 검증 범위와 TC-7 후속 상태는 [10 검증 기록](10-phase2-verification.md)에 기록한다.
