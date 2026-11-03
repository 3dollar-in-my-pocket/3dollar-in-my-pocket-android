# TH-1361 긴 정렬 칩 전체 노출

## Brief

[TH-1361](https://3dollarinmypocket.atlassian.net/browse/TH-1361): 긴 정렬 항목을 선택하면 문자와 칩 우측 경계가 화면 밖으로 잘린다. 선택한 칩 전체가 가로 viewport 안에 보이게 한다.

## Investigation

- develop `4d6d0606`, 4.30.0(137)의411dp 화면에서 `최신 업데이트순` 선택 후 우측 텍스트·surface가 잘리는 현상을 관측했다.
- 칩 폭이 고정된 문제가 아니라 선택 항목 변경 후 가로 목록 위치가 칩 전체를 노출하지 않는 경로였다.

## Plan

선택 칩과 측정된 가시영역을 기준으로 필요한 만큼 가로 스크롤한다. 라벨을 줄이거나 화면 밖 항목 전체의 레이아웃을 바꾸지 않는다.

## Implementation

`HomeFilterChips.kt`에서 선택/크기 변경을 관측해 선택 칩의 텍스트·padding·surface가 함께 드러나도록 목록 위치를 보정한다. API·정렬 의미·의존성은 변경하지 않는다.

## Verification

기존 수정 APK **4.30.0(137), Pixel7/API35**의411dp와360dp에서 긴 선택 칩 전체 노출을 확인했다. 아래 전후 표는411dp 비교다. 이전 7건 작업 APK의 증거이므로 최종4건 **4.31.0(138), release base `79c2afb2`**의 검증 결과를 별도로 기록한다.

| Before | After |
|---|---|
| [긴 라벨·우측 경계 잘림](https://github.com/3dollar-in-my-pocket/3dollar-in-my-pocket-android/releases/download/verification-assets/residual-four-20261015-TH-1361-before.png) | [선택 칩 전체 노출](https://github.com/3dollar-in-my-pocket/3dollar-in-my-pocket-android/releases/download/verification-assets/residual-four-20261015-TH-1361-after.png) |

최종4건만 적용한 **4.31.0(138)**에서 `testDebugUnitTest` **334 tests / failures 0 / errors 0 / skipped 0**, `:app:assembleDebug`, 모듈 의존성 검사(18간선, 신규 위반0), `git diff --check`를 통과했다. JDK17·Pixel7/API35 에뮬레이터를 사용했으며 Before는 develop baseline, 위 After 링크는 이번4건 전용 APK의 신규 캡처다.

411dp에서 인기순→거리순→최신 업데이트순으로 선택했다. 긴 칩 surface `[762,415][1080,504]` 및 텍스트 `[846,436][1054,484]`가 viewport 안에 완전히 노출됐다. 360dp는 이전7건 APK의 결과이며 최종4건에서 재실행하지 않았다.

실기기·태블릿/폴더블·접근성 큰 글꼴의 추가 검증은 남아 있다. 이번 변경은 별도 신규 순수 parser 테스트를 포함하지 않는다.
