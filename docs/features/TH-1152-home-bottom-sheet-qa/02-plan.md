# TH-1152 Home Bottom Sheet QA Fix Implementation Plan

> **For agentic workers:** REQUIRED: Use `superpowers:subagent-driven-development` if available, or `superpowers:executing-plans` to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** `TH-1152`의 QA 하위 작업 12개를 홈 탐색 바텀시트 전환 구조 안에서 순차적으로 수정하고, 각 수정이 독립적으로 검증 가능한 상태로 만든다.

**Architecture:** 홈 화면은 기존 XML/ViewBinding 기반 `HomeFragment` 위에 `ComposeView`로 바텀시트 목록/프리뷰를 렌더링하는 현재 구조를 유지한다. 수정은 `HomeViewModel`의 서버 드리븐 상태, `HomeBottomSheetContent`의 순수 UI 상태, `NaverMapFragment`의 지도 마커 렌더링, `HomeBottomSheetScreenMapper`의 SDUI 모델 매핑으로 나눠 처리한다. 방문/리뷰/공유/marker 정책은 사용자 답변, iOS GitHub 코드, 서버 응답 계약 확인 결과를 기준으로 아래 확정값을 따른다.

**Tech Stack:** Kotlin, Android XML/ViewBinding, Jetpack Compose interop, Naver Map SDK, Google Mobile Ads SDK, Kakao Share SDK, MVVM, Hilt, StateFlow, JUnit4.

---

## 구현 결과

2026-05-31 기준 본 문서의 확정 정책을 Android 코드에 반영했다.

- 홈 바텀시트 리스트 카드 탭은 프리뷰가 아니라 상세 화면으로 이동한다.
- 지도 마커 탭은 기존처럼 프리뷰 바텀시트를 열고, OS back/닫기 후 기존 리스트 sheet 상태를 복원한다.
- 방문 인증은 상세 화면을 건너뛰고 `StoreCertificationActivity`를 통해 인증 fragment로 직접 진입한다.
- 리뷰 작성은 `StoreDetailActivity`로 이동한 뒤 `AddReviewDialog`를 연다.
- Kakao 공유는 iOS와 동일하게 Kakao Share SDK를 사용하고 OS 공유 sheet fallback을 제거했다.
- `AD_MOB` 카드 alias, SDText `fontWeight`, 서버 marker chip text를 서버 드리븐 모델에 반영했다.
- 리스트가 `BasicCard` 없이 empty/ad card만 받으면 이전 지도 마커를 비운다.

검증:

- PASS: `./gradlew :app:testDebugUnitTest --tests com.zion830.threedollars.ui.home.ui.compose.HomeSheetStateCalculatorTest --tests com.zion830.threedollars.ui.home.ui.HomeStorePreviewRouteTest`
- PASS: `./gradlew :data:testDebugUnitTest --tests com.threedollar.data.screen.HomeBottomSheetScreenMapperTest`
- PASS: `./gradlew :app:assembleDebug`

---

## 2026-06-03 자동 QA 체크리스트

에뮬레이터: `emulator-5554` (`Pixel_7_API_35`, Android 15)

- [x] 홈 관련 단위 테스트 통과
  - `./gradlew :app:testDebugUnitTest --tests com.zion830.threedollars.ui.home.ui.compose.HomeSheetStateCalculatorTest --tests com.zion830.threedollars.ui.home.ui.HomeStorePreviewRouteTest`
- [x] 서버 드리븐 매퍼 단위 테스트 통과
  - `./gradlew :data:testDebugUnitTest --tests com.threedollar.data.screen.HomeBottomSheetScreenMapperTest`
- [x] Debug APK 설치 성공
  - `./gradlew :app:installDebug`
- [x] 앱 실행 smoke 통과
  - `com.zion830.threedollars.dev/com.zion830.threedollars.ui.splash.ui.SplashActivity` 실행
  - 앱 프로세스 `com.zion830.threedollars.dev` 생존 확인
  - `logcat -b crash` 비어 있음
- [x] 홈 화면/지도/바텀시트 기본 노출 확인
  - UI tree에서 `homeBottomSheetComposeView`, `layout_map`, 필터, 홈 바텀시트 리스트 확인
  - 스크린샷: `/private/tmp/th1152-home-smoke.png`
- [x] 홈 바텀시트 리스트 카드 탭 시 프리뷰가 아니라 가게 상세로 이동 확인
  - 리스트 첫 카드 탭 후 UI tree에서 `storeNameTextView`, `reviewButton`, `addCertificationButton` 확인
  - `logcat -b crash` 비어 있음
- [x] 상세 화면에서 OS back 후 홈 복귀 확인
  - 홈 `homeBottomSheetComposeView` 재노출
  - 앱 프로세스 생존 확인
- [x] 지도 핀 탭 시 프리뷰 바텀시트 노출 확인
  - UI tree에서 `방문 인증`, `리뷰 작성`, `공유`, `길안내` 액션 확인
  - `logcat -b crash` 비어 있음
- [x] 프리뷰 상태에서 OS back 시 앱 종료 대신 프리뷰 닫힘 확인
  - 홈 리스트 바텀시트 재노출
  - 앱 프로세스 생존 확인
- [x] 프리뷰 `방문 인증` 액션이 상세를 건너뛰고 인증 화면으로 직접 진입 확인
  - UI tree에서 `가게 근처에서\n 방문을 인증할 수 있어요`, `방문 성공`, `방문 실패` 확인
  - `logcat -b crash` 비어 있음
- [x] 프리뷰 `리뷰 작성` 액션이 상세 진입 후 리뷰 bottom sheet를 노출하는지 확인
  - UI tree에서 `이 가게를\n추천하시나요?`, 별점 영역, `리뷰를 남겨주세요!(100자 이내)`, `리뷰 쓰기` 확인
  - `logcat -b crash` 비어 있음
- [x] 프리뷰 `공유` 액션에서 Android OS 공유 chooser 미노출 및 crash 없음 확인
  - 공유 탭 후 프리뷰 UI 유지
  - `logcat -b crash` 비어 있음

제약:

- KakaoTalk 앱 설치/로그인 상태가 아니어서 Kakao 앱 내부 공유 완료까지는 확인하지 않았다. 이번 자동 QA에서는 iOS와 맞춘 “OS 공유 sheet fallback 미노출”과 crash-free 동작을 확인했다.
- 실서버 응답 기반 QA라 화면에 노출된 실제 가게/마커 데이터는 실행 시점 데이터에 의존한다.

---

## Jira 범위

Parent: `TH-1152` `[유저앱/AOS] 홈 탐색 개선: 바텀 시트 전환`

하위 작업:

- `TH-1175` 지도 > 영업중인 가게 영업중 표기 누락
- `TH-1177` 리스트 > 지도상 가게가 있지만 리스트에 없다고 나오는 현상 존재
- `TH-1178` 리스트/바텀시트 > New 아이콘 위치 디자인 상이
- `TH-1179` 리스트/바텀시트 > 영업종료 라인 폰트 bold 처리를 서버가 아닌 앱에서 처리 하는걸로 보입니다
- `TH-1180` 리스트 > 가게 클릭시 바텀시트가 아닌 가게 상세로 이동해야합니다
- `TH-1181` 바텀시트 > 방문 인증 클릭시 가게 상세로 이동 후 방문 페이지로 이동
- `TH-1182` 바텀시트 > 리뷰 클릭시 가게 상세로 이동 후 리뷰 모달 노출
- `TH-1183` 리스트 > 애드몹 광고 노출 안됨
- `TH-1184` 리스트 > 결과가 있으나 조건에 만족하는 가게가 없다고 노출됨
- `TH-1185` 바텀시트 > 공유 클릭시 ios와 동작 상이
- `TH-1186` 리스트 > 리스트 중간에서 바텀시트 이동 후 뒤돌아갔을때 리스트 ui가 어색한것 같습니다
- `TH-1187` 리스트뷰 > 바텀시트 이동 후 os 뒤로가기 누르면 앱이 꺼집니다

## 확정 정책 및 확인 결과

- `TH-1180`: "리스트"는 홈 바텀시트 full list를 의미한다. 홈 바텀시트 리스트 카드 탭은 가게 상세로 이동하고, 지도 marker 탭은 기존처럼 프리뷰 바텀시트를 연다.
- `TH-1181`: 방문 인증 버튼은 상세 화면을 건너뛰고 방문 인증 화면으로 직접 진입한다. 현재 `StoreDetailActivity(startCertification=true)` 경유는 제거한다.
- `TH-1182`: 리뷰 버튼은 홈에서 바로 다이얼로그를 띄우지 않고, 가게 상세로 이동한 뒤 리뷰 다이얼로그를 띄운다. 현재 Android의 `StoreReviewDetailActivity(openReviewWrite=true)`는 "가게 상세" 경유가 아니므로 `StoreDetailActivity`에서 `AddReviewDialog`를 여는 경로로 맞춘다.
- `TH-1185`: GitHub의 iOS repo `3dollar-in-my-pocket/3dollars-in-my-pocket-ios` `develop` branch 기준, iOS는 `AppModuleInterfaceImpl.shareKakao()`에서 `KakaoSDKShare`/`KakaoSDKTemplate`의 `ShareApi.shared.shareDefault`를 사용하고, `storeId`/`storeType`을 `androidExecutionParams`와 `iosExecutionParams`에 함께 넣은 뒤 Kakao가 반환한 URL을 연다. `StoreType.kakaoParameterValue`는 boss store `foodTruck`, user store `streetFood`이며 Android string 값과 같다. OS 공유 시트 fallback은 없다.
- `TH-1175`: 서버 응답 계약은 `marker.focused`/`marker.unfocused`를 내려주는 구조다. `docs/features/home-bottom-sheet-transition/01-analysis.md`와 `HomeBottomSheetScreenMapperTest`에도 `marker.focused.text = "영업중"`, `marker.unfocused.text = ""` 케이스가 이미 있다. 앱은 metadata에서 임의 fallback을 만들지 않고 서버 marker chip을 그대로 렌더링한다. 실데이터에서 marker가 비어 있으면 서버 계약 이슈로 분리한다.

## 작업 순서

1. 상태 동기화/뒤로가기 문제를 먼저 고친다. 이 단계는 사용자 흐름 전체 안정성에 영향을 준다.
2. 리스트/프리뷰 렌더링 문제를 고친다. 서버 드리븐 모델 매핑과 Compose UI만 다룬다.
3. 방문/리뷰/공유 액션을 확정 정책에 맞춰 고친다.
4. 지도 핀 영업중 표기를 마지막에 고친다. 서버 marker chip을 앱 marker bitmap으로 렌더링하는 경로만 보정한다.

## 파일 구조

Create:

- `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationActivity.kt`
  - 홈에서 상세 화면을 건너뛰고 방문 인증 Fragment로 직접 진입하기 위한 전용 container activity를 추가한다. 기존 fragment를 재사용하되 args를 상세 화면 의존성에서 분리한다.
- `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationArgs.kt`
  - 직접 방문 인증 진입에 필요한 `storeId`, `storeName`, `latitude`, `longitude`, category 표시 정보를 담는다.
- `app/src/main/res/layout/activity_store_certification.xml`
  - 직접 방문 인증 Activity의 fragment container layout을 추가한다.

Modify:

- `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt`
  - 홈 바텀시트 back 처리, 리스트 카드 클릭 정책, store preview action route, 지도 marker clear/sync를 조정한다.
- `app/src/main/AndroidManifest.xml`
  - 새 `StoreCertificationActivity`를 등록한다.
- `app/src/main/java/com/zion830/threedollars/ui/home/viewModel/HomeViewModel.kt`
  - `selectedStoreScreen`, `selectedHomeListCardId`, `homeListSection`, `homeListNextCursor` 갱신 순서를 정리한다.
- `app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeBottomSheetContent.kt`
  - full list 상태 복원, title+badge layout, SD text fontWeight 적용, AdMob card rendering, back close hook을 처리한다.
- `app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeSheetState.kt`
  - 필요 시 프리뷰 종료 후 복원할 sheet state 계산 함수를 추가한다.
- `app/src/main/java/com/zion830/threedollars/ui/map/ui/NaverMapFragment.kt`
  - 소스 변경은 필요 없었다. 기존 구현이 이미 서버 marker chip text/image를 사용하므로, `HomeFragment`에서 empty 상태 marker clear만 보정한다.
- `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreDetailActivity.kt`
  - 상세 진입 직후 리뷰 다이얼로그를 여는 optional intent extra를 추가한다.
- `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationFragment.kt`
  - 기존 `UserStoreModel` 단일 인자 의존성을 직접 방문 인증 진입에도 쓸 수 있는 args로 완화한다.
- `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationAvailableFragment.kt`
  - 기존 `UserStoreModel` 단일 인자 의존성을 직접 방문 인증 진입에도 쓸 수 있는 args로 완화한다.
- `data/src/main/java/com/threedollar/data/screen/HomeBottomSheetScreenMapper.kt`
  - `AD_MOB` alias, SDText fontWeight, 서버 marker chip 필드를 매핑한다.
- `core/network/src/main/java/com/threedollar/network/data/screen/HomeBottomSheetScreenResponse.kt`
  - SDText `fontWeight` 응답 필드를 추가한다.
- `core/common/src/main/java/com/threedollar/common/serverdriven/model/ServerDrivenModels.kt`
  - `SDTextModel.fontWeight`를 optional로 추가한다.
- `app/src/test/java/com/zion830/threedollars/ui/home/ui/compose/HomeSheetStateCalculatorTest.kt`
  - 바텀시트가 프리뷰 진입/닫기/뒤로가기 후 어떤 리스트 상태로 돌아가야 하는지 순수 상태 단위로 검증한다.
- `app/src/test/java/com/zion830/threedollars/ui/home/ui/HomeStorePreviewRouteTest.kt`
  - link가 없을 때 card id/type fallback으로 상세 route를 결정하는지 검증한다.
- `data/src/test/java/com/threedollar/data/screen/HomeBottomSheetScreenMapperTest.kt`
  - `AD_MOB` alias, fontWeight, marker text mapping 테스트를 추가한다.

Verify:

- `./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.HomeBottomSheetScreenMapperTest"`
- `./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.*"`
- `./gradlew :app:assembleDebug`
- 필요 시 emulator에서 `test-android-apps:android-emulator-qa`로 홈 지도/리스트/프리뷰 수동 QA

---

## Chunk 1: 리스트/지도 상태 동기화와 뒤로가기 안정화

### Task 1: 빈 리스트 응답 시 지도 마커를 반드시 비운다

**Jira:** `TH-1177`, `TH-1184`

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt:355`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/map/ui/NaverMapFragment.kt:194`
- Test: 수동 QA 중심. 필요 시 marker state helper를 분리해 unit test 작성.

- [ ] **Step 1: 현재 동작 재확인**

확인 지점:

```kotlin
viewModel.homeListSection.collect { section ->
    val cards = section.cards.filterIsInstance<HomeListCardModel.BasicCard>()
    if (cards.isEmpty()) return@collect
    naverMapFragment.addHomeListMarkers(...)
}
```

문제: 리스트가 `EMPTY_CARD`만 받거나 비어 있으면 기존 지도 마커가 남는다. 사용자는 지도에는 가게가 있는데 리스트는 없다고 보게 된다.

- [ ] **Step 2: marker clear 조건 추가**

`cards.isEmpty()`일 때:

```kotlin
if (cards.isEmpty()) {
    naverMapFragment.clearMarker()
    return@collect
}
```

- [ ] **Step 3: 선택 카드 상태도 같이 정리**

`HomeViewModel.fetchHomeListSection()`에서 첫 페이지 응답이 `BasicCard` 없음이면 `_selectedHomeListCardId`를 `null`로 둔다.

현재 코드의 `section.cards.firstOrNull()?.cardId`는 `EMPTY_CARD`/`ADMOB_CARD`도 선택 id가 될 수 있다. 아래처럼 `BasicCard` 기준으로 바꾼다.

```kotlin
if (!append) {
    _selectedHomeListCardId.value = section.cards
        .filterIsInstance<HomeListCardModel.BasicCard>()
        .firstOrNull()
        ?.cardId
}
```

- [ ] **Step 4: 검증**

Run:

```bash
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.*"
```

Expected: existing home unit tests pass.

Manual QA:

- 필터를 바꿔 결과 없음 상태를 만든다.
- 리스트에는 empty card만 보여야 한다.
- 지도에 이전 검색의 가게 핀이 남지 않아야 한다.

### Task 2: 프리뷰 닫기/뒤로가기 후 full list 상태를 복원한다

**Jira:** `TH-1186`

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeBottomSheetContent.kt:154`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeSheetState.kt`
- Test: `app/src/test/java/com/zion830/threedollars/ui/home/ui/compose/HomeSheetStateCalculatorTest.kt`

- [ ] **Step 1: 실패 테스트 추가**

`HomeSheetStateCalculatorTest`에 프리뷰 진입 전 sheet state를 복원하는 순수 함수 테스트를 추가한다.

```kotlin
@Test
fun `closing preview restores previous list sheet value`() {
    assertEquals(
        HomeSheetValue.FullList,
        HomeSheetStateCalculator.restoreAfterPreview(HomeSheetValue.FullList),
    )
    assertEquals(
        HomeSheetValue.Collapsed,
        HomeSheetStateCalculator.restoreAfterPreview(HomeSheetValue.Collapsed),
    )
}
```

- [ ] **Step 2: 테스트 실패 확인**

Run:

```bash
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.ui.compose.HomeSheetStateCalculatorTest"
```

Expected: `restoreAfterPreview` unresolved.

- [ ] **Step 3: 최소 구현**

`HomeSheetState.kt`:

```kotlin
fun restoreAfterPreview(previousListValue: HomeSheetValue): HomeSheetValue = previousListValue
```

- [ ] **Step 4: Compose 상태 적용**

`HomeBottomSheetContent`에서 `storeScreen == null` 상태의 마지막 settled list state를 별도 보관한다.

방향:

- `var lastListSettledValue by remember { mutableStateOf(HomeSheetValue.Collapsed) }`
- 리스트 모드에서 `settledValue`가 바뀔 때 `lastListSettledValue` 갱신
- `storeScreen`이 `null`로 돌아올 때 `HomeSheetStateCalculator.restoreAfterPreview(lastListSettledValue)`로 animate

주의:

- 프리뷰가 열려 있을 때는 drag를 막는 기존 정책을 유지한다.
- full list 배경 `homeFullListTopBackgroundView`는 복원 후 다시 보이도록 둔다.

- [ ] **Step 5: 검증**

Run:

```bash
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.ui.compose.HomeSheetStateCalculatorTest"
```

Manual QA:

- 리스트를 full 상태로 올린다.
- 리스트 중간 항목을 눌러 프리뷰를 연다.
- 닫기 버튼 또는 OS back으로 돌아온다.
- 리스트가 full 상태와 기존 스크롤 위치를 유지해야 한다.

### Task 3: OS back이 프리뷰를 먼저 닫게 한다

**Jira:** `TH-1187`

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt`

- [ ] **Step 1: MainActivity back 흐름 확인**

현재 `MainActivity.onBackPressed()`는 home destination에서 `super.onBackPressed()`로 빠진다. `HomeFragment`가 back callback을 등록하지 않으면 앱이 종료된다.

- [ ] **Step 2: `OnBackPressedCallback` 추가**

`HomeFragment`에 callback을 등록한다.

```kotlin
private val homeBackPressedCallback = object : OnBackPressedCallback(false) {
    override fun handleOnBackPressed() {
        viewModel.closeStorePreview()
    }
}
```

`initView()` 또는 별도 init 함수에서:

```kotlin
requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, homeBackPressedCallback)
```

`selectedStoreScreen` collect에서:

```kotlin
homeBackPressedCallback.isEnabled = viewModel.selectedStoreScreen.value != null
```

주의:

- 프리뷰가 없을 때는 기존 back 동작을 막지 않는다.
- callback lifecycle owner는 반드시 `viewLifecycleOwner`를 사용한다.

- [ ] **Step 3: 검증**

Run:

```bash
./gradlew :app:assembleDebug
```

Manual QA:

- 홈 리스트에서 가게를 눌러 프리뷰를 연다.
- Android system back을 누른다.
- 앱이 종료되지 않고 프리뷰만 닫혀야 한다.
- 프리뷰가 없는 홈에서 back은 기존 동작을 유지해야 한다.

---

## Chunk 2: 리스트/바텀시트 렌더링 보정

### Task 4: 리스트 카드 클릭 정책을 상세 이동으로 바꾼다

**Jira:** `TH-1180`

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt:274`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/viewModel/HomeViewModel.kt:234`
- Test: `app/src/test/java/com/zion830/threedollars/ui/home/ui/HomeStorePreviewRouteTest.kt`

- [ ] **Step 1: 적용 대상 고정**

`TH-1180`의 "리스트"는 홈 바텀시트의 full list로 확정한다.

현재 코드 기준:

- 기존 `HomeListViewFragment`는 이미 상세 화면으로 이동한다.
- 새 홈 바텀시트 list는 `selectHomeListCard()`를 호출해 프리뷰를 연다.

- [ ] **Step 2: route 테스트 보강**

`HomeStorePreviewRouteTest`에 `cardId` fallback 테스트를 추가한다.

```kotlin
@Test
fun `fallback card id resolves route when link is missing`() {
    val route = HomeStorePreviewRoute.fromLink(
        link = null,
        fallbackStoreId = 100186L,
        fallbackStoreType = "USER_STORE",
    )

    assertEquals(HomeStorePreviewRoute(100186L, "USER_STORE"), route)
}
```

- [ ] **Step 3: 리스트 card click과 marker click을 분리**

방향:

- `onCardClick`: 상세 화면으로 이동
- `onMarkerClick`: 프리뷰 바텀시트 열기

`HomeBottomSheetContent` 호출부:

```kotlin
onCardClick = ::moveHomeListCardDetail
```

새 함수:

```kotlin
private fun moveHomeListCardDetail(card: HomeListCardModel.BasicCard) {
    viewModel.sendClickHomeListCard(card)
    val route = HomeStorePreviewRoute.fromLink(
        link = card.link?.link,
        fallbackStoreId = card.storeIdFromCardId(),
    ) ?: return
    val intent = if (route.storeType == BOSS_STORE) {
        BossStoreDetailActivity.getIntent(requireContext(), route.storeId.toString())
    } else {
        StoreDetailActivity.getIntent(requireContext(), storeId = route.storeId.toInt())
    }
    startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
}
```

`HomeViewModel`는 `selectHomeListCard()`와 click log 전송을 분리한다.

```kotlin
fun sendClickHomeListCard(card: HomeListCardModel.BasicCard) {
    card.clickLog?.let { SDClickLogger.send(it) }
}
```

- [ ] **Step 4: 검증**

Manual QA:

- 홈 바텀시트 리스트 카드 탭: 상세 화면으로 이동해야 한다.
- 지도 핀 탭: 기존처럼 프리뷰 바텀시트가 열려야 한다.
- 상세에서 back 후 홈 리스트 상태가 유지되어야 한다.

### Task 5: New badge 위치를 제목 바로 뒤로 붙인다

**Jira:** `TH-1178`

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeBottomSheetContent.kt:588`

- [ ] **Step 1: 공통 title+badge composable 추가**

`HomeHeader`와 `StorePreviewTitle`이 같은 문제를 갖고 있으므로 내부 helper를 만든다.

```kotlin
@Composable
private fun TitleWithBadge(
    title: SDTextModel?,
    badge: SDImageModel?,
    titleSize: Int,
    titleWeight: FontWeight,
    titleColor: Color,
    maxLines: Int,
    badgeDefaultSize: Dp,
    modifier: Modifier = Modifier,
)
```

렌더링 원칙:

- 제목이 짧으면 badge가 제목 바로 오른쪽에 붙는다.
- 제목이 길면 제목이 ellipsis되고 badge는 오른쪽 끝에 남는다.
- `Row` 안 `Text`는 `weight(1f, fill = false)`를 사용한다.

- [ ] **Step 2: 기존 함수 교체**

`HomeHeader`와 `StorePreviewTitle`에서 `TitleWithBadge`를 사용한다.

- [ ] **Step 3: 검증**

Manual QA:

- New badge가 리스트 카드 제목 바로 뒤에 붙어야 한다.
- 상세 프리뷰 제목에서도 같은 위치 규칙을 따라야 한다.
- 긴 제목에서 badge가 화면 밖으로 밀리지 않아야 한다.

### Task 6: SDText fontWeight를 서버 값으로 적용한다

**Jira:** `TH-1179`

**Files:**

- Modify: `core/network/src/main/java/com/threedollar/network/data/screen/HomeBottomSheetScreenResponse.kt:112`
- Modify: `core/common/src/main/java/com/threedollar/common/serverdriven/model/ServerDrivenModels.kt:72`
- Modify: `data/src/main/java/com/threedollar/data/screen/HomeBottomSheetScreenMapper.kt:178`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeBottomSheetContent.kt:819`
- Test: `data/src/test/java/com/threedollar/data/screen/HomeBottomSheetScreenMapperTest.kt`

- [ ] **Step 1: mapper 실패 테스트 추가**

```kotlin
@Test
fun homeListSectionMapper_mapsTextFontWeight() {
    val response = HomeListSectionResponse(
        cards = listOf(
            HomeListCardResponse(
                type = "BASIC_CARD",
                cardId = "S:100186",
                header = HomeListCardHeaderResponse(title = SDTextResponse.fromText("가게")),
                metadata = HomeListCardMetadataResponse(
                    secondary = listOf(
                        chip(text = "영업 종료", fontWeight = "REGULAR"),
                        chip(text = "1km", fontWeight = "BOLD"),
                    ),
                ),
                marker = HomeListMarkerResponse(
                    focused = chip("영업중"),
                    unfocused = chip(""),
                    location = SDLocationResponse(latitude = 37.1, longitude = 127.2),
                ),
            ),
        ),
    )

    val card = response.asModel().cards.single() as HomeListCardModel.BasicCard

    assertEquals("REGULAR", card.metadata.secondary[0].text.fontWeight)
    assertEquals("BOLD", card.metadata.secondary[1].text.fontWeight)
}
```

필요한 test helper:

```kotlin
private fun chip(text: String, fontWeight: String? = null): SDChipResponse = SDChipResponse(
    text = SDTextResponse.fromText(text = text, fontWeight = fontWeight),
)
```

- [ ] **Step 2: response/model 확장**

`SDTextModel`:

```kotlin
data class SDTextModel(
    val text: String,
    val isHtml: Boolean,
    val fontColor: String? = null,
    val fontWeight: String? = null,
)
```

`SDTextResponse`:

```kotlin
@SerializedName("fontWeight")
private val rawFontWeight: String? = null

val fontWeight: String?
    get() = rawText.stringValue("fontWeight") ?: rawFontWeight
```

`fromText()`에도 `fontWeight` optional parameter를 추가한다.

- [ ] **Step 3: UI 적용**

`MetadataChip`, `BodiesRow`, button text 등 SDText를 쓰는 곳에 helper를 사용한다.

```kotlin
private fun String?.toComposeFontWeight(fallback: FontWeight): FontWeight {
    return when (this?.uppercase()) {
        "BOLD", "700" -> FontWeight.Bold
        "SEMIBOLD", "SEMI_BOLD", "600" -> FontWeight.SemiBold
        "MEDIUM", "500" -> FontWeight.Medium
        "REGULAR", "NORMAL", "400" -> FontWeight.Normal
        else -> fallback
    }
}
```

`MetadataRows`의 `firstChipWeight = FontWeight.SemiBold` 강제 로직은 제거하거나 fallback 전용으로 낮춘다. 최종 원칙은 서버 `fontWeight`가 있으면 서버 값을 우선한다.

- [ ] **Step 4: 검증**

Run:

```bash
./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.HomeBottomSheetScreenMapperTest"
./gradlew :app:assembleDebug
```

Manual QA:

- 서버가 내려준 영업상태/거리/최근 방문 chip font weight가 그대로 보인다.
- 첫 번째 secondary chip이라는 이유만으로 bold가 되면 안 된다.

### Task 7: AdMob card를 실제 광고 뷰로 렌더링한다

**Jira:** `TH-1183`

**Files:**

- Modify: `data/src/main/java/com/threedollar/data/screen/HomeBottomSheetScreenMapper.kt:90`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeBottomSheetContent.kt:399`
- Test: `data/src/test/java/com/threedollar/data/screen/HomeBottomSheetScreenMapperTest.kt`

- [ ] **Step 1: `AD_MOB` alias 테스트 추가**

```kotlin
@Test
fun homeListSectionMapper_mapsAdMobTypeAlias() {
    val response = HomeListSectionResponse(
        cards = listOf(HomeListCardResponse(type = "AD_MOB", cardId = "ad-1")),
    )

    assertTrue(response.asModel().cards.single() is HomeListCardModel.AdMobCard)
}
```

- [ ] **Step 2: mapper alias 보강**

```kotlin
normalizedType == "ADMOB_CARD" || normalizedType == "AD_MOB" -> HomeListCardModel.AdMobCard(...)
```

- [ ] **Step 3: Compose AdMob view 추가**

`HomeListContent`에서 `Spacer(0.dp)` 대신 `HomeListAdMobCard()`를 렌더링한다.

구현 방향:

```kotlin
@Composable
private fun HomeListAdMobCard() {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.MEDIUM_RECTANGLE)
                adUnitId = context.getString(CommonR.string.admob_list_banner)
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
```

주의:

- 기존 `item_list_view_admob.xml`은 `MEDIUM_RECTANGLE`을 사용한다.
- `AdView` lifecycle pause/resume/destroy가 필요하면 `DisposableEffect`로 정리한다.
- 광고 로드 실패 시 카드 높이가 과도하게 남지 않는지 확인한다.

- [ ] **Step 4: 검증**

Run:

```bash
./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.HomeBottomSheetScreenMapperTest"
./gradlew :app:assembleDebug
```

Manual QA:

- 서버가 `AD_MOB` card를 내려주는 리스트에서 광고 영역이 보인다.
- 광고 카드가 marker list 계산에 포함되지 않아야 한다.
- 광고가 실패해도 리스트 스크롤이 깨지지 않아야 한다.

---

## Chunk 3: 방문/리뷰/공유 액션 정리

### Task 8: 방문 인증을 상세 화면 없이 직접 연다

**Jira:** `TH-1181`

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt:432`
- Modify: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationActivity.kt`
- Create: `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationArgs.kt`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationFragment.kt`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreCertificationAvailableFragment.kt`

- [ ] **Step 1: 현재 상세 경유 제거**

현재 홈 프리뷰의 `/visit` link는 상세 화면을 열고 `startCertification=true`로 인증 fragment를 붙인다.

```kotlin
StoreDetailActivity.getIntent(requireContext(), storeId = storeId, startCertification = true)
```

이 경로를 홈에서는 사용하지 않는다.

- [ ] **Step 2: 방문 인증 전용 Activity 추가**

`StoreCertificationActivity`를 추가해 아래 역할만 갖게 한다.

- intent extra에서 `storeId`, `storeName`, `latitude`, `longitude`, category 표시 정보 또는 최소 args를 받는다.
- `FusedLocationProviderClient` 또는 기존 `StoreCertificationNaverMapFragment.currentPosition`으로 현재 위치를 얻는다.
- `StoreCertificationAvailableFragment.MIN_DISTANCE` 기준으로 `StoreCertificationFragment` 또는 `StoreCertificationAvailableFragment`를 첫 화면으로 붙인다.
- 인증 성공/실패 후 `RESULT_OK`를 반환해 홈 프리뷰/리스트 갱신이 가능하게 한다.

`StoreCertificationFragment`와 `StoreCertificationAvailableFragment`는 지금처럼 `UserStoreModel` 전체만 받지 말고, 직접 진입에 필요한 최소 args도 받을 수 있게 변경한다. 가장 작은 변경은 `UserStoreModel`을 홈 preview 값으로 구성하는 helper를 두는 것이다.

- [ ] **Step 3: 홈 `/visit` link 처리 변경**

`HomeFragment.handleStorePreviewLink()`에서 `/visit`을 만나면 상세를 열지 않고 전용 Activity를 실행한다.

```kotlin
val intent = StoreCertificationActivity.getIntent(
    context = requireContext(),
    args = StoreCertificationArgs(
        storeId = storeId,
        storeName = currentStorePreviewTitle(),
        latitude = selectedStoreLatitude(),
        longitude = selectedStoreLongitude(),
    ),
)
startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
```

주의:

- 서버가 `/visit` link에 `storeId`만 내려주는 경우가 있으므로, `latitude`/`longitude`는 현재 선택된 preview screen 또는 marker/card route에서 보강한다.
- 좌표를 끝까지 얻지 못하면 인증 화면을 열지 말고 toast를 띄운다. `0.0,0.0`으로 인증 계산을 진행하면 안 된다.

- [ ] **Step 4: 검증**

Manual QA:

- 방문 인증 버튼 클릭 후 `StoreDetailActivity`가 보이지 않아야 한다.
- 위치 권한/거리 초과/거리 이내 케이스가 기존 상세 화면과 동일하게 동작한다.
- 인증 성공/실패 후 홈으로 돌아왔을 때 프리뷰/리스트 refresh 정책이 깨지지 않는다.

### Task 9: 리뷰는 상세 화면 진입 후 다이얼로그를 연다

**Jira:** `TH-1182`

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt:466`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreDetailActivity.kt`

- [ ] **Step 1: 현재 리뷰 상세 경유 제거**

현재 홈 프리뷰의 user store 리뷰 작성은 `StoreReviewDetailActivity(openReviewWrite=true)`로 이동한다.

```kotlin
StoreReviewDetailActivity.getInstance(
    context = requireContext(),
    storeId = route.storeId.toInt(),
    openReviewWrite = true,
)
```

확정 정책은 "가게 상세로 이동한 다음 리뷰 다이얼로그"이므로 user store는 `StoreDetailActivity` 경유로 바꾼다. Boss store는 별도 상세/리뷰 플로우가 다르므로 기존 `BossReviewWriteActivity` 유지 여부를 구현 중 확인하되, 이 Task의 필수 범위는 user store다.

- [ ] **Step 2: `StoreDetailActivity`에 리뷰 다이얼로그 extra 추가**

`StoreDetailActivity.getIntent()`에 optional `openReviewWrite` extra를 추가한다.

```kotlin
StoreDetailActivity.getIntent(
    context = requireContext(),
    storeId = route.storeId.toInt(),
    openReviewWrite = true,
)
```

`StoreDetailActivity`는 최초 생성 시 한 번만 아래를 실행한다.

```kotlin
AddReviewDialog.getInstance(storeId = storeId)
    .show(supportFragmentManager, AddReviewDialog::class.java.name)
```

주의:

- 회전/재생성에서 중복 표시되지 않게 consumed flag를 둔다.
- 다이얼로그 완료 후 `StoreDetailActivity`의 기존 `addReviewResult` observer가 상세 정보를 refresh해야 한다.
- 상세에서 back으로 홈에 돌아왔을 때 `HomeFragment.onActivityResult()`의 기존 refresh 경로가 동작해야 한다.

- [ ] **Step 3: 홈 action route 변경**

`moveStorePreviewReviewWrite()`의 user store 분기를 `StoreDetailActivity(openReviewWrite = true)`로 교체한다.

- [ ] **Step 4: 검증**

Manual QA:

- 홈 프리뷰 리뷰 버튼 클릭 시 가게 상세가 열린다.
- 상세 화면 위에 리뷰 다이얼로그가 자동으로 열린다.
- 다이얼로그 닫기/작성 완료/뒤로가기 모두 기존 상세 화면 UX와 충돌하지 않는다.

### Task 10: 공유 동작을 iOS Kakao Share 기준으로 맞춘다

**Jira:** `TH-1185`

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt:493`
- Modify: `app/src/main/java/com/zion830/threedollars/utils/SystemUtils.kt:173`
- Modify: `app/src/main/java/com/zion830/threedollars/utils/ShareFormat.kt:5`

- [ ] **Step 1: iOS 기준 고정**

iOS 확인 결과:

- `StoreDetailViewModel.didTapShare`는 `shareKakao()`를 호출한다.
- `AppModuleInterfaceImpl.shareKakao()`는 Kakao `ShareApi.shared.shareDefault`를 호출한다.
- `Link`에는 Kakao map web URL과 `storeId`/`storeType` execution params가 들어간다.
- `StoreType.kakaoParameterValue`는 boss store `foodTruck`, user store `streetFood`이고, Android의 `scheme_host_kakao_link_food_truck_type`/`scheme_host_kakao_link_road_food_type`과 일치한다.
- 성공 시 Kakao가 반환한 URL을 열고, 실패 시 alert를 보여준다.
- OS 공유 시트 fallback은 없다.

Android도 홈 preview 공유에서 이 정책으로 맞춘다. `UserApiClient.instance.isKakaoTalkLoginAvailable()`은 로그인 가능 여부라 공유 가능 여부 판단으로 쓰지 않는다.

- [ ] **Step 2: route 파라미터 안전화**

현재:

```kotlin
androidExecutionParams = mapOf("storeId" to storeId.toString(), "storeType" to type.toString())
```

수정 방향:

- `storeId`/`type` null이면 공유를 중단하고 실패 안내를 보여준다.
- `ShareFormat.shareUrl`이 `location == null`일 때 `null,null`을 붙이지 않도록 한다.
- `storeType` 값은 iOS의 `storeType.kakaoParameterValue`와 동일한 scheme parameter 값으로 맞춘다.

- [ ] **Step 3: Android Kakao Share fallback 정리**

`shareWithKakao()`의 실패/미설치 처리를 iOS와 맞춘다.

현재 문제:

```kotlin
if (error != null) {
    shareUrl(shareFormat.url)
} else if (linkResult != null) {
    this.startActivity(linkResult.intent)
}
...
shareUrl(shareFormat.shareUrl)
```

수정 방향:

- Kakao share가 가능하면 `ShareClient.instance.shareDefault()` 결과의 `intent`를 연다.
- Kakao share 실패 시 OS 공유 시트를 열지 않고 toast/dialog로 실패를 알린다. Product가 미설치 fallback을 요구하면 Kakao Web Sharer URL만 별도 검토한다.
- `shareFormat.url`처럼 base URL만 공유되는 경로를 제거한다.

- [ ] **Step 4: 검증**

Manual QA:

- 카카오톡 설치 기기: 공유 버튼 클릭 시 채팅방 선택 화면이 뜬다.
- 카카오톡 미설치/공유 실패 케이스: OS 공유 시트가 뜨지 않고 정의된 실패 안내가 보인다.
- 수신자가 공유 링크 클릭 시 앱의 해당 가게 상세로 이동한다. `storeId`/`storeType` deep link parameter가 iOS와 동일해야 한다.

---

## Chunk 4: 지도 핀 영업중 표기

### Task 11: 서버 marker chip의 영업중 표기를 렌더링한다

**Jira:** `TH-1175`

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/map/ui/NaverMapFragment.kt:332`
- Modify: `data/src/main/java/com/threedollar/data/screen/HomeBottomSheetScreenMapper.kt:142`
- Test: `data/src/test/java/com/threedollar/data/screen/HomeBottomSheetScreenMapperTest.kt`

- [ ] **Step 1: 서버 계약 기준 고정**

서버는 `HomeListMarkerResponse.focused`/`unfocused`로 marker chip을 내려준다.

확인된 근거:

- `core/network/.../HomeBottomSheetScreenResponse.kt`: `HomeListMarkerResponse(focused, unfocused, location, link, clickLog)`
- `docs/features/home-bottom-sheet-transition/01-analysis.md`: `marker.focused.text`는 영업 중이면 `영업중`, 영업 종료면 빈 문자열
- `HomeBottomSheetScreenMapperTest`: focused `영업중`, unfocused `""` fixture 존재

- [ ] **Step 2: mapper 테스트는 서버 marker 보존을 강화**

Fallback 테스트를 추가하지 않는다. 대신 아래를 명확히 검증한다.

```kotlin
assertEquals("영업중", card.marker.focused.text.text)
assertEquals("", card.marker.unfocused.text.text)
```

- [ ] **Step 3: marker rendering 확인**

`NaverMapFragment.applyHomeListMarkerIcon()`는 서버 chip의 `text + additionalText`를 bitmap text로 그린다. focused marker의 text가 비어 있지 않으면 "영업중"을 표시하고, 빈 문자열이면 문구 영역을 숨긴다.

확인할 것:

- focused marker만 "영업중"을 표시할지
- unfocused marker도 표시할지
- 서버 image marker가 내려오는 경우 text chip과 image marker의 우선순위를 어떻게 둘지

- [ ] **Step 4: 검증**

Manual QA:

- 영업중 가게 pin 선택 시 "영업중" 표기가 보여야 한다.
- 영업 종료 또는 text blank 케이스는 문구 영역이 없어야 한다.
- marker image URL이 있는 경우 기존 이미지 렌더링이 깨지지 않아야 한다.

---

## Chunk 5: 최종 회귀 검증

### Task 12: 전체 수정 검증

**Files:**

- Verify only

- [ ] **Step 1: mapper/unit tests**

Run:

```bash
./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.HomeBottomSheetScreenMapperTest"
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.*"
```

Expected: all selected tests pass.

- [ ] **Step 2: build**

Run:

```bash
./gradlew :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 3: emulator smoke test**

검증 플로우:

- 홈 진입 후 리스트 바텀시트가 보인다.
- 필터 변경 후 지도 마커와 리스트가 같은 결과를 보여준다.
- 결과 없음 필터에서 이전 marker가 남지 않는다.
- full list에서 리스트 중간 항목 클릭 후 상세로 이동한다.
- 지도 marker 클릭은 프리뷰 바텀시트를 연다.
- 프리뷰가 열린 상태에서 OS back은 프리뷰만 닫는다.
- New badge가 제목 바로 옆에 있다.
- 영업상태 font weight가 서버 값과 맞다.
- `AD_MOB` 카드가 광고 영역으로 보인다.
- 방문 버튼은 상세 화면 없이 방문 인증 화면으로 직접 이동한다.
- 리뷰 버튼은 가게 상세로 이동한 뒤 리뷰 다이얼로그를 연다.
- 공유 버튼은 iOS와 같은 Kakao Share 흐름으로 동작한다.
- 영업중 marker 문구는 서버 marker chip 기준으로 보인다.

- [ ] **Step 4: Jira 업데이트 준비**

각 하위 작업별 완료 결과와 특이사항을 정리한다.

특이사항이 있으면 아래 기준으로 분리한다.

- 실데이터에 `marker.focused/unfocused`가 비어 있으면 AOS fallback이 아니라 서버 계약 이슈로 기록한다.
- `/visit` link에 직접 방문 인증에 필요한 위치 값이 없으면 서버 action params 보강 필요 여부를 기록한다.
- Kakao Share 실패/미설치 fallback 정책이 iOS와 다르게 요구되면 Product 결정사항으로 기록한다.

## 완료 기준

- `TH-1177`, `TH-1184`: 결과 없음 상태에서 지도와 리스트가 불일치하지 않는다.
- `TH-1186`: 프리뷰 종료 후 full list 상태와 스크롤 맥락이 유지된다.
- `TH-1187`: 프리뷰 상태에서 OS back이 앱을 종료하지 않는다.
- `TH-1180`: 홈 바텀시트 리스트 카드 클릭은 상세 화면으로 이동하고, marker 클릭은 프리뷰를 연다.
- `TH-1178`: New badge가 제목 바로 뒤에 붙고 긴 제목에서도 깨지지 않는다.
- `TH-1179`: 서버 fontWeight가 UI에 반영되고 첫 번째 chip hardcoding에 의존하지 않는다.
- `TH-1183`: 서버 `AD_MOB`/`ADMOB_CARD` 광고 카드가 0dp가 아닌 실제 광고 영역으로 표시된다.
- `TH-1181`: 방문 인증은 상세 화면 없이 직접 인증 화면으로 이동한다.
- `TH-1182`: 리뷰는 가게 상세로 이동한 뒤 리뷰 다이얼로그가 열린다.
- `TH-1185`: 공유는 iOS와 같은 Kakao Share flow와 execution params를 사용한다.
- `TH-1175`: "영업중" marker 문구는 서버 `marker.focused/unfocused` chip을 기준으로 렌더링된다.

## 확정된 답변 반영

1. `TH-1180`: 홈 바텀시트 리스트로 확정.
2. `TH-1181`: 상세 화면을 건너뛰고 방문 인증 직접 진입으로 확정.
3. `TH-1182`: 홈 즉시 다이얼로그가 아니라 가게 상세 진입 후 리뷰 다이얼로그로 확정.
4. `TH-1185`: iOS GitHub 코드 확인 결과 Kakao Share SDK flow 기준으로 확정.
5. `TH-1175`: 서버가 `marker.focused/unfocused`를 내려주는 계약으로 확인. 앱 fallback은 구현하지 않는다.
