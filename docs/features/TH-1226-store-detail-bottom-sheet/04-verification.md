# TH-1226 Store Detail V2 검증

## 자동 검증

아래 명령은 2026-08-28 실응답 교정 후 실제 실행해 모두 통과했다.

```bash
./gradlew :core:common:testDebugUnitTest --tests '*ServerDrivenText*'
./gradlew :core:network:testDebugUnitTest --tests '*StoreDetail*' --tests com.threedollar.network.api.ServerApiTest
./gradlew :data:testDebugUnitTest --tests '*StoreDetail*' --tests com.threedollar.data.screen.HomeBottomSheetScreenMapperTest
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest --tests '*StoreDetail*' --tests 'com.zion830.threedollars.ui.home.*'
./gradlew :app:assembleDebug
git diff --check
```

- app 관련 범위: 59 tests 통과
- data 관련 범위: 16 tests 통과
- network 관련 범위: 2 tests 통과
- common server-driven text 범위: 5 tests 통과
- `StoreDetailActivity.getIntent`/`BossStoreDetailActivity.getIntent` direct caller: 0건
- V2 package의 legacy Activity fallback: 0건
- OpenAPI custom action enum 18개와 ViewModel 처리 목록 일치
- `StoreDetailV2Content` direct `when`에 section 16개 존재
- staged 파일 없음, 보호 파일 stage 없음
- final review 보강 회귀 test: repository exception/content 보존, Home loading 종료, favorite override, false child result, display 시점 view log, APP_SCHEME routing, exact sheet anchor, sticky action sentinel, review report validation
- 실응답 교정 회귀 test: CSS span style/plain text, `VISIT.summary.chips`, contributor route, hash tab anchor, generic INFO anchor, `IMAGE_ID`/`IMAGE_URL`, 누락 contributor 이름 fallback

기존 프로젝트의 Kotlin plugin 중복 로드, deprecated API와 annotation target warning은 baseline과 동일하게 출력됐다.

추가로 final gate에서 `./gradlew test :app:assembleDebug`를 실행했으나, TH-1226 변경 범위 밖의 기존 `:core:abtest:kaptDebugUnitTestKotlin`과 `:core:abtest:kaptReleaseUnitTestKotlin`이 `ExampleUnitTest`의 `@error.NonExistentClass` stub 오류로 실패했다. 해당 test 파일은 기준 커밋 `4ab76e8f65aed8608f3afd1df8237b6181f7c222`과 동일하고, `core/abtest` 및 Gradle 설정에는 이번 변경이 없다. 위에 기록한 TH-1226 관련 test/compile과 독립 `:app:assembleDebug`는 모두 성공했다.

## 2026-09-03 renderer fidelity 자동 검증

```bash
./gradlew :core:common:testDebugUnitTest --tests '*ServerDrivenText*' --tests com.threedollar.common.compose.utils.ComposeColorUtilsTest
./gradlew :data:testDebugUnitTest --tests '*StoreDetail*' --tests com.threedollar.data.screen.HomeBottomSheetScreenMapperTest
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest --tests '*StoreDetail*' --tests 'com.zion830.threedollars.ui.home.*'
./gradlew :app:assembleDebug
git diff --check
```

- app 관련 범위: 61 tests, failures 0, errors 0
- data 관련 범위: 16 tests, failures 0, errors 0
- common text/color 범위: 7 tests, failures 0, errors 0
- TDD RED 확인: `normalizeServerDrivenColor` 미구현, 빈 menu item 2개 유지, presentation policy 미구현으로 각각 실패한 뒤 GREEN을 확인했다.

## emulator 실서버 확인

Debug APK를 `emulator-5554`의 `com.zion830.threedollars.dev`에 설치하고 로그인된 dev 환경에서 확인했다.

- [x] Home bottom sheet 첫 list card tap 후 foreground가 `MainActivity`를 유지하며 기존 Preview를 표시한다.
- [x] Preview title tap 후 Activity 전환 없이 같은 sheet에서 V2 Expanded가 열린다.
- [x] `GET /api/v2/screen/store/120024`의 `PREVIEW, AD_MOB, TAB, MAP, EDIT, VISIT, INFO_V1, IMAGE, REVIEW, CTA` 응답으로 화면이 구성된다.
- [x] Expanded 화면에 raw `<span>`과 literal `null`이 노출되지 않고 title/metadata/contributor 문구가 일반 UI로 표시된다.
- [x] 상단 방문·리뷰·공유·길찾기 action row가 기존 Preview UI와 같은 icon/한 줄 배치로 표시된다.
- [x] `가게 정보 & 메뉴` tab이 `INFO_V1` section으로 이동한다.
- [x] contributor row의 `/store-contributors?storeId=...`가 `StoreContributorActivity`로 이동한다.
- [x] TalkBack을 끈 상태에서 앱 화면에 초록색 접근성 focus 테두리가 남지 않는다.

### store 120120 renderer 재검증

- [x] Home FullList에서 store `120120` 선택 후 Preview → 같은 `MainActivity` bottom sheet Expanded로 진입한다.
- [x] `PREVIEW, AD_MOB, TAB, MAP, EDIT, VISIT, INFO_V1, IMAGE, REVIEW, CTA`가 server order로 표시된다.
- [x] PREVIEW metadata 사이에 server separator가 표시된다.
- [x] AdMob error code 3 발생 후 빈 72dp slot이 제거된다.
- [x] MAP action은 검은 반투명 배경, 주소 가변 폭, 48dp 확대 버튼과 local fallback icon으로 표시된다.
- [x] EDIT action의 local edit/report fallback icon이 표시된다.
- [x] INFO information/menu card가 `#FAFAFA` surface로 표시되고 빈 menu row가 제거된다.
- [x] REVIEW summary가 `#FFF3F4` surface와 server의 24x24 star 크기로 표시된다.
- [x] `가게 정보 & 메뉴` tab이 INFO_V1 section 시작 위치로 이동한다.

## 남은 수동 확인 checklist

- [ ] Home marker 선택 시 기존 Preview가 즉시 표시되고 V2는 background load된다.
- [ ] upward/downward drag와 back으로 `Preview <-> Expanded`가 왕복한다.
- [ ] 기존 Home list `Collapsed <-> FullList`와 Preview 내부 action tap이 회귀하지 않는다.
- [ ] USER/BOSS 실제 응답의 16개 section 조합이 server order와 TO-BE 구성대로 표시된다.
- [x] 상단 action이 사라질 때만 하단 sticky action이 표시된다.
- [ ] list/deep link/push/share/favorite/my page/related store가 V2 full-screen을 연다.
- [ ] 저장, 방문, 리뷰 작성·신고·삭제·좋아요, 공유, 길안내, 지도/주소, 수정/없는 장소, 사진, 쿠폰, post, CTA/link action의 성공·실패가 legacy UX와 일치한다.
- [ ] child Activity/Dialog 성공 후 V2 content와 caller 목록이 refresh된다.
- [ ] first load/action/refresh 실패는 Toast/Snackbar를 표시하고 가능한 기존 content를 유지한다.
- [ ] `not_exists_store`와 initial 방문의 필수 위치 누락은 현재 container를 닫는다.
- [ ] Map/Ad가 pause/resume/화면 이탈 후 정상 동작하며 다른 section을 깨지 않는다.

## 2026-09-03 strict server-driven 검증

TDD RED에서 아래 기존 동작을 확인했다.

- scroll sticky action이 표시됨
- MAP/EDIT action이 local icon을 선택함
- contributor `null` 문구를 Android가 보정함
- 빈 INFO_V1 menu item을 mapper가 제거함
- 상세 renderer source에 local 저장 action, section/review divider, top app bar가 존재함

GREEN 이후 아래 검증을 실행해 통과했다.

```bash
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.storeDetail.v2.*" --tests "com.zion830.threedollars.ui.home.*" :data:testDebugUnitTest --tests "com.threedollar.data.screen.*" :app:assembleDebug
git diff --check
```

- app 상세/홈 관련 57 tests: failures 0, errors 0
- data screen mapper 관련 21 tests: failures 0, errors 0
- `:app:assembleDebug`: 성공
- `git diff --check`: 오류 없음

Emulator `emulator-5554` 확인 항목:

- Home Preview → Expanded 후 `저장하기`, sheet handle, Home bottom navigation이 없음
- PREVIEW action은 서버 action row 한 세트만 표시되고 scroll 후 sticky action이 없음
- contributor `null`과 빈 menu item을 포함한 서버값을 client 보정 없이 유지함
- full-screen 상세에 local top app bar가 없음
- REVIEW card 사이의 client 1dp divider가 없음
- 캡처: `/tmp/store-detail-strict-full.png`, `/tmp/home-expanded-strict.png`, `/tmp/home-expanded-scrolled-strict.png`, `/tmp/store-120024-final-review.png`, `/tmp/final-home-expanded.png`
