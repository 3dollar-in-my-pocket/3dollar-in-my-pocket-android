# TH-1128 Store Detail Action Nudge Plan

## 02 Plan

## 구현 전략

1. iOS와 동일하게 display-items API를 Android `StoreApi`/`StoreRepository`에 추가한다.
2. `StoreDetailViewModel`에서 display item 응답을 필터링하고, session view count 조건을 적용한다.
3. 상세 Activity 루트에 `ComposeView` overlay를 추가해 하단 CTA 위에 플로팅 모달을 표시한다.
4. 방문 인증 유도는 모달에서 바로 `POST /api/v2/store/visit`를 호출한다.
5. 사라진 가게 신고는 `GET /api/v1/report/group/STORE/reasons`로 사유를 받고, 선택 사유의 `type`으로 기존 delete API를 호출한다.

## 주요 변경

- API/모델:
  - `StoreDisplayItemsResponse`, `StoreDisplayItemsRequest`
  - domain `StoreDisplayItemType`, `StoreDisplayTriggerModel`, `SessionViewCountRangeModel`
  - `StoreRepository.getStoreDisplayItems()`, `postStoreDisplayItemImpression()`
- 상세 상태:
  - `StoreDetailDisplayItemState`
  - `StoreDetailDisplayItemEffect`
  - process-local `StoreDetailViewSessionCounter`
- UI:
  - `activity_store_info.xml`에 `displayItemOverlay` 추가.
  - `StoreDetailDisplayItemOverlay` Compose UI 추가.
  - slide in 500ms, slide out 300ms, toast delay 800ms.
- 로그:
  - `visit_inducement_modal`
  - `disappearance_inquiry_modal`
  - `select_reason`
  - `reason_type`

## 하위 작업

- [x] iOS 구현 비교.
- [x] display-items API/DTO/domain model 추가.
- [x] ViewModel display item 큐와 session count 조건 적용.
- [x] 방문 인증 유도 inline API 호출.
- [x] 사라진 가게 신고 사유 동적 로드/제출.
- [x] Compose overlay UI 추가.
- [x] analytics/impression key 추가.
- [x] 단위 테스트 추가.
- [x] Gradle 검증.
- [x] 기기/에뮬레이터 수동 QA.

## 검증 계획

- 문서만 변경한 현재 단계:
  - `rg`로 문서 링크/코드 경로 확인.
- 구현 단계:
  - `./gradlew :domain:testDebugUnitTest`
  - `./gradlew :app:testDebugUnitTest`
  - `./gradlew :app:assembleDebug`
  - 에뮬레이터 또는 실제 기기 수동 QA:
    - 상세 진입 후 서버 trigger `displayAfterSeconds` 이후 유도 UI 표시.
    - 미클릭 시 서버 trigger `displayDurationSeconds` 이후 사라짐.
    - CTA 클릭 중 자동으로 사라지지 않음.
    - 방문 인증 성공/실패 inline API 흐름 정상.
    - STORE 사유 선택/제출 정상.
    - 상세 하단 고정 버튼과 overlay 겹침 없음.

## 리스크와 대응

- Figma의 정보 수정 유도는 iOS display item type에 없다.
  - 이번 구현에서는 iOS와 동일한 두 item type만 처리한다.
- 서버에서 두 item을 동시에 visible로 내려줄 수 있다.
  - Android는 응답 순서 큐로 하나씩 표시한다.
- 기존 Activity가 XML/ViewBinding 기반이고 방문 인증은 Fragment overlay 방식이다.
  - 화면 전체 Compose 전환 없이 overlay만 추가한다.
- 기존 상단 삭제 버튼과 새 사라진 가게 모달의 성공 후 동작이 다르다.
  - 기존 버튼은 현재 동작을 유지하고, 새 모달만 iOS처럼 상세 화면에 남긴다.
