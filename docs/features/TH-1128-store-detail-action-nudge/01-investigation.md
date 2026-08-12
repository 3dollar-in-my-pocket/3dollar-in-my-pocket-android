# TH-1128 Store Detail Action Nudge Investigation

## 01 Investigation

## 접근 상태

- Jira URL은 현재 세션의 브라우징 도구에서 열리지 않았다.
- Figma MCP로 `882:10754` 노드의 metadata와 screenshot을 확인했다.
- API Jira는 별도 권한/링크가 필요하다. 이 문서는 현재 코드와 Figma에서 확인한 사실만 확정으로 기록한다.

## 현재 코드 경로

- 상세 화면:
  - `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreDetailActivity.kt`
  - `app/src/main/res/layout/activity_store_info.xml`
- 상세 ViewModel:
  - `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/viewModel/StoreDetailViewModel.kt`
- 방문 인증:
  - `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationActivity.kt`
  - `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationFragment.kt`
  - `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationAvailableFragment.kt`
  - `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/viewModel/StoreCertificationViewModel.kt`
- 삭제 요청 바텀시트:
  - `app/src/main/java/com/zion830/threedollars/ui/dialog/DeleteStoreDialog.kt`
  - `app/src/main/res/layout/dialog_delete.xml`
- 정보 수정:
  - `app/src/main/java/com/zion830/threedollars/ui/edit/ui/EditStoreFragment.kt`
- API:
  - `core/network/src/main/java/com/threedollar/network/api/ServerApi.kt`
  - `data/src/main/java/com/threedollar/data/home/datasource/HomeRemoteDataSourceImpl.kt`
  - `data/src/main/java/com/threedollar/data/home/repository/HomeRepositoryImpl.kt`
  - `domain/src/main/java/com/threedollar/domain/home/repository/HomeRepository.kt`

## 현재 데이터/API 흐름

- 상세 조회:
  - `GET /api/v4/store/{storeId}`
  - `StoreDetailViewModel.getUserStoreDetail()`에서 호출.
  - 요청 헤더: `X-Device-Latitude`, `X-Device-Longitude`
  - 주요 query: `storeImagesCount`, `reviewsCount`, `visitHistoriesCount`, `filterVisitStartDate`
- 서버드리븐 상세 보조 섹션:
  - `GET /api/v1/screen/store/{storeId}`
  - 현재 `StoreDetailViewModel.getUserStoreDetail()` 내부에서 `StoreRepository.getScreenStore()`로 호출하고 `RELATED_STORES` 섹션만 사용한다.
- iOS TH-1128/TH-1129 기준 display item:
  - `GET /api/v1/store/{storeId}/display-items`
  - query: `itemTypes`
  - 요청 헤더: `X-Device-Latitude`, `X-Device-Longitude`
  - item type:
    - `DISAPPEARANCE_INQUIRY_MODAL`
    - `VISIT_CERTIFICATION_INDUCEMENT_MODAL`
  - 응답 trigger:
    - `type`
    - `displayAfterSeconds`
    - `displayDurationSeconds`
    - `conditions.sessionViewCountRange.min/max`
  - 노출 기록:
    - `POST /api/v1/store/{storeId}/display-items/impression`
    - body: `itemTypes`
- 방문 인증:
  - `POST /api/v2/store/visit`
  - body: `storeId`, `type`
  - `type`: 현재 클라이언트는 `EXISTS`, `NOT_EXISTS` 사용.
- 삭제 요청:
  - `DELETE /api/v2/store/{storeId}`
  - query: `deleteReasonType`
  - 현재 enum: `NOSTORE`, `WRONGNOPOSITION`, `OVERLAPSTORE`, `NONE`
- 신고 사유:
  - `GET /api/v1/report/group/STORE/reasons`
  - iOS는 응답의 `reason.type`을 `deleteReasonType`으로 그대로 전달한다.
- 정보 수정:
  - `PATCH /api/v3/store/{storeId}`
  - 현재 `EditStoreFragment`/`EditStoreViewModel` 흐름에서 사용.

## 현재 UI 흐름

- 상세 하단 고정 영역:
  - `bottomFavoriteButton`
  - `addCertificationButton`
- 상세 상단/본문 액션:
  - `deleteButton` 클릭 시 `DeleteStoreDialog` 표시.
  - `editStoreInfoButton` 클릭 시 `EditStoreFragment` 진입.
  - `reviewButton`, `writeReviewTextView` 클릭 시 `AddReviewDialog` 표시.
  - `addCertificationButton` 클릭 시 현재 위치와 가게 위치 거리를 계산해 방문 인증 가능/불가능 화면으로 분기.
- 방문 인증 분기:
  - `StoreCertificationAvailableFragment.MIN_DISTANCE = 100`
  - 거리 기준 안이면 성공/실패 선택 화면 표시.
  - 거리 기준 밖이면 거리 안내 화면 표시 후 2초마다 위치 갱신.
- 방문 인증 내역:
  - 상세 응답의 `visits.counts.existsCounts`, `visits.counts.notExistsCounts`, `visits.counts.isCertified` 사용.
  - 방문 인증 내역이 없으면 “방문 인증으로 가게의 최근 활동을 알려주세요!” 안내 영역 표시.

## Figma에서 확인한 정책

- 방문 인증 유도:
  - AS-IS는 홈에서 상세 진입 후 방문 인증하기 클릭까지 이동해야 함.
  - TO-BE는 조건이 맞으면 상세 진입 후 약 `500ms` 뒤 플로팅 유도 UI를 노출.
  - 미클릭 시 약 `3000ms` 뒤 `Slide out`.
  - 인증 완료 후 완료 피드백을 노출.
- 신고/삭제/정보 수정 유도:
  - AS-IS는 툴팁 CTA 클릭 후 바텀시트 버튼 클릭 흐름.
  - TO-BE는 불필요한 과정을 줄이고 원탭 구조로 접근성을 높임.
  - 조건 충족 시 상세 진입 약 `500ms` 뒤 플로팅 `Slide in`.
  - 미클릭 시 약 `3000ms` 뒤 `Slide out`.
  - 신고하기 완료 후 완료 피드백을 노출.
- Figma의 플로팅 신고/수정 후보 문구:
  - `없어진 가게예요.`
  - `위치가 잘못됐어요`
  - `중복 제보된 가게에요`
  - `부적절한 내용이 있어요`
- Figma의 삭제 요청 바텀시트 문구:
  - `삭제 요청 하시는 이유가 궁금해요!`
  - `3건 이상의 요청이 들어오면 자동 삭제됩니다`
  - `없어진 가게예요.`
  - `허위 제보로 요청된 가게예요`
  - `가게가 중복으로 등록되어 있어요.`

## 현재 코드와 Figma 차이

- 현재는 방문 인증 유도 UI가 상세 하단 고정 버튼으로만 제공된다.
- 현재는 신고/삭제 요청이 상단 `deleteButton`에서 바텀시트를 열어 진행된다.
- 현재 삭제 요청 사유는 3개이고, Figma 플로팅 후보는 4개다.
- 현재 삭제 요청 성공 후 `DeleteStoreDialog`는 `deleteStore()` 호출 뒤 Activity를 바로 종료한다. Figma는 완료 피드백이 상세 화면 위에서 표시되는 상태를 포함한다.
- 현재 방문 인증 성공은 `StoreCertificationAvailableFragment`에서 toast를 띄우고 fragment를 닫는다. Figma는 상세 화면 위 완료 피드백 상태를 포함한다.

## iOS 비교 결과

- iOS 로컬 `develop`은 `origin/develop`보다 2커밋 뒤였지만, 원격 추가 커밋은 Store/Network/Model/Log 관련 파일을 바꾸지 않았다.
- iOS는 서버 display item 응답의 `isVisible`, trigger delay/duration, session view count 조건을 그대로 사용한다.
- 방문 인증 유도 모달은 기존 위치 기반 인증 화면으로 이동하지 않고, 모달 안에서 바로 `POST /api/v2/store/visit`를 호출한다.
- 사라진 가게 모달은 `STORE` 신고 사유 API를 호출하고, 선택된 `reason.type`으로 기존 삭제 API를 호출한다.
- iOS는 성공 후 상세 화면을 종료하지 않고 slide out 뒤 공통 thanks toast를 표시한다.
- iOS 로그 key:
  - `visit_inducement_modal`
  - `disappearance_inquiry_modal`
  - `select_reason`
  - `report`
  - `reason_type`

## Android 구현 결정

- Android도 iOS의 display-items API와 모델을 기준으로 구현한다.
- Android는 서버 응답이 여러 개 visible일 때 같은 위치에 겹치지 않도록 응답 순서 큐로 하나씩 표시한다.
- 기존 상단 삭제 버튼과 `DeleteStoreDialog`는 유지한다.
- Figma의 정보 수정 유도는 iOS display item type에 없으므로 이번 Android 구현 범위에서 제외한다.

## 리스크

- 노출 조건을 클라이언트 추정으로 구현하면 Jira/API 정책과 어긋날 수 있다.
- 삭제 요청 사유를 고정 enum으로 구현하면 서버 정책과 어긋날 수 있으므로 `STORE` 사유 API 응답의 `type`을 사용한다.
- 상세 진입 직후 자동 노출 UI가 기존 하단 CTA, 바텀시트, fragment container, soft keyboard, snackbar/toast와 겹칠 수 있다.
- 방문 인증 완료 후 상세 데이터를 즉시 갱신하지 않으면 방문 인증 내역과 유도 UI 상태가 오래된 상태로 남을 수 있다.
