# TH-1020 리뷰 Dialog 복원 크래시

## Brief

[TH-1020](https://3dollarinmypocket.atlassian.net/browse/TH-1020): 리뷰 작성/수정 Dialog가 열린 상태에서 Activity가 재생성되면 빈 생성자를 찾지 못해 앱이 종료된다. 재생성 후에도 입력과 제출 대상을 보존한다.

## Investigation

- develop `4d6d0606`, 4.30.0(137)에서 실제 Activity relaunch 시 `NoSuchMethodException: AddReviewDialog.<init>[]` 및 Fragment 생성 실패를 관측했다.
- 기존 `AddReviewDialog`는 필수 생성자 인자와 callback에 가게·리뷰·작성 모드를 보관해 기본 Fragment 복원 계약을 충족하지 않았다.
- Before PNG는 **재생성 직전 Dialog**이며 크래시 화면 자체는 아니다. 위 오류는 별도의 baseline 실행 로그로 확인했다.

## Plan

기본 생성자와 primitive arguments로 복원 정보를 유지하고, 상세 v2 제출은 가게 ID를 포함한 FragmentResult로 전달한다. 복원 시 Dialog를 다시 추가하지 않는다.

## Implementation

- `AddReviewDialog.kt`: storeId/reviewId/초기 내용·별점/제출 모드를 arguments로 저장한다.
- `StoreDetailSduiNavigator.kt`, `StoreDetailSduiContract.kt`, `StoreDetailSduiViewModel.kt`: 복원 가능한 결과 listener와 명시적인 제출 storeId를 연결한다.
- `StoreReviewDetailActivity.kt`: 최초 진입에서만 자동 작성 Dialog를 열고 이미 복원된 Dialog는 중복 표시하지 않는다.

## Verification

기존 수정 APK **4.30.0(137), Pixel7/API35**에서 구성 변경 양방향 및 background process kill 후 task 복원 시 초안·별점 유지와 Dialog 중복 없음, 닫기를 확인했다. 이 기록은 이전 7건 작업 APK에서 수집했으며 최종4건 **4.31.0(138), release base `79c2afb2`**의 결과와 구분한다.

| Before | After |
|---|---|
| [재생성 직전 Dialog](https://github.com/3dollar-in-my-pocket/3dollar-in-my-pocket-android/releases/download/verification-assets/residual-four-20261015-TH-1020-before.png) | [process 복원 후 초안·별점 보존](https://github.com/3dollar-in-my-pocket/3dollar-in-my-pocket-android/releases/download/verification-assets/residual-four-20261015-TH-1020-after.png) |

최종4건만 적용한 **4.31.0(138)**에서 `testDebugUnitTest` **334 tests / failures 0 / errors 0 / skipped 0**, `:app:assembleDebug`, 모듈 의존성 검사(18간선, 신규 위반0), `git diff --check`를 통과했다. JDK17·Pixel7/API35 에뮬레이터를 사용했으며 Before는 develop baseline, 위 After 링크는 이번4건 전용 APK의 신규 캡처다.

다크모드 yes→no의 Activity 재생성 및 background process kill→기존 task 복원을 다시 실행했다. 새 PID에서 초안 `20261015`·별점3과 단일 Dialog가 유지됐다. 실제 리뷰 서버 제출은 실행하지 않았다.

실제 서버 리뷰 POST/PUT 및 SDUI 결과 제출은 미검증이다. UI 복원 확인을 실제 제출 성공으로 표현하지 않는다. 실기기·다른 API·Release/R8 검증도 별도다.

스코프 밖 기존 리스크: 이번4건은 MainActivity 보완을 제외하므로 popup StateFlow 수집 중 구성 변경 시 기존 NavController lookup FATAL 경로가 남는다. 이 문서의 Dialog 복원 TC를 홈 재생성/popup 문제까지 통과한 증거로 사용하지 않는다.
