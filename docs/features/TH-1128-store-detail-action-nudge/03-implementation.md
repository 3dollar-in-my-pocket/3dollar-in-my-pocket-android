# TH-1128 Store Detail Action Nudge Implementation

## 03 Implementation

- 구현 브랜치: `feature/TH-1128`
- 구현 완료일: 2026-07-12

## API와 데이터

- `GET /api/v1/store/{storeId}/display-items`를 추가했다.
- `POST /api/v1/store/{storeId}/display-items/impression`을 추가했다.
- network DTO를 domain model로 변환하고 알 수 없는 item type은 `UNKNOWN`으로 처리한다.
- 지원 item type은 다음 두 가지다.
  - `DISAPPEARANCE_INQUIRY_MODAL`
  - `VISIT_CERTIFICATION_INDUCEMENT_MODAL`

## 노출 상태와 정책

- 서버의 `isVisible`, `displayAfterSeconds`, `displayDurationSeconds`를 적용한다.
- process-local store 상세 조회 횟수에 `sessionViewCountRange`를 적용한다.
- 여러 item이 노출 대상이면 서버 응답 순서대로 하나씩 표시한다.
- 실제 표시 시 impression API와 analytics를 전송한다.
- Compose effect key는 `storeId + itemType`으로 유지해 제출, 신고 사유 로딩 및 선택 상태 변경 시 impression이 중복되지 않게 했다.

## 사용자 액션

- 방문 유도에서 `열었어요`/`닫았어요`를 선택하면 기존 방문 인증 API에 `EXISTS`/`NOT_EXISTS`를 전달한다.
- 방문 인증 성공 후 상세 데이터를 다시 조회하고 공통 감사 토스트를 표시한다.
- 사라진 가게 유도는 `STORE` 신고 사유를 동적으로 조회한다.
- 선택된 `reason.type`으로 기존 가게 삭제 요청 API를 호출한다.
- 기존 상단 삭제 버튼과 기존 바텀시트 흐름은 유지한다.

## UI

- `activity_store_info.xml`에 `ComposeView` overlay를 추가했다.
- `StoreDetailDisplayItemOverlay`가 방문 유도와 사라진 가게 유도 UI를 표시한다.
- iOS와 동일하게 overlay는 모달 높이만 차지하며, 모달 바깥 영역의 탭과 스크롤은 상세 화면에 전달한다.
- 모달 카드 내부의 비버튼 영역은 터치를 소비해 아래 상세 화면으로 이벤트가 전달되지 않는다.
- 모달은 바깥 영역 터치로 닫히지 않고 서버 유지 시간 만료 또는 사용자 액션 API 성공 시 닫힌다.
- 방문 유도, 신고 사유 로딩, 신고 사유 선택 상태 Preview를 제공한다.

## 테스트

- item type 변환과 session view count 범위를 단위 테스트한다.
- store별 session counter를 단위 테스트한다.
- transient UI 상태가 display effect key를 바꾸지 않는지 단위 테스트한다.
