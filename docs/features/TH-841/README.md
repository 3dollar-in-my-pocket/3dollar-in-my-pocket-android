# TH-841 설정 '사장님앱 다운받기' 링크를 사장님앱 소개 페이지로

## Brief

[TH-841](https://3dollarinmypocket.atlassian.net/browse/TH-841): 마이 > 설정의 '가슴속 3천원 사장님앱 다운받기'가 Play 스토어(`market://details?id=app.threedollars.manager`)로 간다. 제보 화면의 '사장님 앱 설치하기'처럼 사장님앱 소개 노션 페이지로 보낸다.

## Investigation

- `MyPageSettingFragment.constraintBoss`: `market://` → 실패 시 `play.google.com` 스토어 링크.
- `NewAddressFragment` 사장님 앱 설치 다이얼로그: 노션 URL 에 `setPackage("com.android.vending")` 를 붙여 먼저 실행 → 스토어가 https 노션 URL 을 처리하지 못해 `START ... pkg=com.android.vending result code=-91`(START_INTENT_NOT_RESOLVED) 후 브라우저로 폴백. 같은 URL 이 코드에 두 번 하드코딩.

## Implementation

- `core/common` `strings.xml` URL 묶음에 `boss_app_intro_url` 추가 (제보 화면과 같은 값).
- 설정·제보 두 곳 모두 `ACTION_VIEW(boss_app_intro_url)` 하나로 연다. 처리할 앱이 없으면 아무 것도 하지 않는다.

## Verification

4.35.0(142) debug, emulator-5554(API 35). 설정 탭 → Chrome 에서 노션 소개 페이지, 제보 다이얼로그 → `-91` 없이 바로 노션 페이지. `:app:assembleDebug :app:testDebugUnitTest` 296 tests / 0 failures.
