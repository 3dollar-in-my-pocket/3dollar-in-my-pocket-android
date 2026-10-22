# TH-901 Notion 본문 공백

## Brief

[TH-901](https://3dollarinmypocket.atlassian.net/browse/TH-901): 인앱 WebView에서 Notion header만 보이고 본문이 흰색이다. 원래 모집 공지의 본문 표시·스크롤·앱 복귀를 복구한다.

## Investigation

- develop `4d6d0606`, 4.30.0(137), Pixel7/API35/WebView153에서 원래 모집 공지의 accessibility 본문·화면 내 bounds는 있지만 픽셀은 흰색이었다.
- 같은 URL/provider/settings의 native parent 전후 비교에서 paint·스크롤 차이를 확인했다. JavaScript/DOM storage는 이미 켜져 있었으며 전역 GPU·UA 원인으로 확정하지 않았다.
- 원래 [모집 공지](https://massive-iguana-121.notion.site/2bb7ad52990e80c5a4b1dae8a667e263?source=copy_link)의 이벤트는2025-12-21 종료됐지만, 렌더 결함은 최신 실행에서도 재현됐다. 이벤트 종료와 기술적 해결을 구분한다.

## Plan

WebView를 native FrameLayout 안에 두어 호스팅 경계를 한정한다. 해당 화면에서 실제 관측한 native 닫기 버튼의 status bar 겹침도 보정한다.

## Implementation

`WebActivity.kt`에서 FrameLayout의 MATCH_PARENT child로 WebView를 유지하고 실제 child의 URL을 추적한다. toolbar에 status bar inset을 반영한다. 외부 브라우저 정책·Manifest·전역 acceleration·UA·의존성은 변경하지 않는다.

## Verification

기존 수정 APK **4.30.0(137), Pixel7/API35/WebView153.0.8010.36**에서 원래 공지의 이미지·본문 표시, 실제 스크롤, native 닫기로 앱 복귀를 확인했다. 별도 사장님 소개 URL과 system Back도 확인했다. 이전 7건 작업 APK의 증거와 최종4건 **4.31.0(138), release base `79c2afb2`**의 결과를 구분한다.

| Before | After |
|---|---|
| [원래 공지 header 아래 공백](https://github.com/3dollar-in-my-pocket/3dollar-in-my-pocket-android/releases/download/verification-assets/residual-four-20261015-TH-901-before.png) | [같은 공지 이미지·본문 표시](https://github.com/3dollar-in-my-pocket/3dollar-in-my-pocket-android/releases/download/verification-assets/residual-four-20261015-TH-901-after.png) |

최종4건만 적용한 **4.31.0(138)**에서 `testDebugUnitTest` **334 tests / failures 0 / errors 0 / skipped 0**, `:app:assembleDebug`, 모듈 의존성 검사(18간선, 신규 위반0), `git diff --check`를 통과했다. JDK17·Pixel7/API35 에뮬레이터를 사용했으며 Before는 develop baseline, 위 After 링크는 이번4건 전용 APK의 신규 캡처다.

원래 공개 모집 공지의 이미지·제목·본문이 렌더링되고 실제 스크롤 및 native 닫기로 홈 복귀를 다시 확인했다. WebView provider 교체나 실제 리뷰 데이터 쓰기는 수행하지 않았다.

실기기·다른 API/WebView provider·Release/R8 추가 검증은 남아 있다. renderer/GPU/CSS 내부 원인과 과거 ANR 전체가 해결됐다고 선언하지 않는다.
