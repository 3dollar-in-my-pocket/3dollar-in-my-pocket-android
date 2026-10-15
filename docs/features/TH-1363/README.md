# TH-1363 필터 변경 후 목록 시작 위치 복귀

## Brief

[TH-1363](https://3dollarinmypocket.atlassian.net/browse/TH-1363): 목록 아래에서 정렬/필터를 바꾸면 새 결과가 성공해도 이전 스크롤 위치에 머문다. 새 첫 페이지 교체 성공 시에만 목록을 처음으로 돌린다.

## Investigation

- develop `4d6d0606`, 4.30.0(137)에서 LATEST→OLDEST 성공 응답 후에도 응답 index7 하단부터 보이는 현상을 확인했다.
- Before는 요청 직후 과거 목록이 남은 프레임이 아니라 새 응답을 기다린 settled 화면이다.

## Plan

첫 페이지의 성공적인 교체와 append/실패/선택 가게 보존 refresh를 구분한다. 실패나 추가 로딩 때문에 사용자의 읽던 위치를 잃지 않게 한다.

## Implementation

- `HomeViewModel.kt`: 해당 첫 페이지 교체 성공을 reset revision으로 전달한다.
- `HomeBottomSheetContent.kt`, `HomeFragment.kt`: revision을 실제 목록 state에 연결해 시작 위치로 이동한다.
- 주소·내 위치·카메라 이동 정책 변경은 포함하지 않는다.

## Verification

기존 수정 APK **4.30.0(137), Pixel7/API35**에서 새 정렬 성공 후 첫 카드 복귀, cursor append 시 기존 위치 유지, 실제 연결 실패 시 기존 위치 유지 및 연결 복구를 확인했다. 전후 데이터·정렬은 서로 다른 시점이므로 동일 응답의 A/B 비교로 표현하지 않는다. 이전 7건 작업 APK의 증거와 최종4건 **4.31.0(138), release base `79c2afb2`**의 결과를 구분한다.

| Before | After |
|---|---|
| [새 응답에도 목록 하단 위치 유지](https://github.com/3dollar-in-my-pocket/3dollar-in-my-pocket-android/releases/download/verification-assets/residual-four-20261015-TH-1363-before.png) | [새 첫 페이지 성공 후 첫 카드](https://github.com/3dollar-in-my-pocket/3dollar-in-my-pocket-android/releases/download/verification-assets/residual-four-20261015-TH-1363-after.png) |

최종4건만 적용한 **4.31.0(138)**에서 `testDebugUnitTest` **334 tests / failures 0 / errors 0 / skipped 0**, `:app:assembleDebug`, 모듈 의존성 검사(18간선, 신규 위반0), `git diff --check`를 통과했다. JDK17·Pixel7/API35 에뮬레이터를 사용했으며 Before는 develop baseline, 위 After 링크는 이번4건 전용 APK의 신규 캡처다.

스크롤 후 UPDATED_DESC→LATEST의 첫 페이지 성공에서 응답 첫 카드 `S:12806143`(핫코베이커리)가 목록 상단에 표시됐다. cursor append 성공 후 하단 위치가 유지됐고, default network=none에서 OLDEST 조회 실패 후에도 같은 가게·화면 좌표가 유지됐다. wifi/data 복구 후 정상 GET도 확인했다. 네트워크 response/opaque cursor는 공개 문서에 첨부하지 않는다.

별도 refresh 없는 process-death scroll 복원을 검증했다고 주장하지 않는다. 실기기·태블릿/폴더블 추가 확인은 남아 있다.
