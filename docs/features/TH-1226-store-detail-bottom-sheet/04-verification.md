# TH-1226 Store Detail V2 검증

## 자동 검증

아래 명령은 2026-08-27 실제 실행해 모두 통과했다.

```bash
./gradlew :core:network:testDebugUnitTest --tests '*StoreDetail*' --tests com.threedollar.network.api.ServerApiTest
./gradlew :data:testDebugUnitTest --tests '*StoreDetail*' --tests com.threedollar.data.screen.HomeBottomSheetScreenMapperTest
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest --tests '*StoreDetail*' --tests 'com.zion830.threedollars.ui.home.*'
./gradlew :app:assembleDebug
git diff --check
```

- app 관련 범위: 55 tests 통과
- data 관련 범위: 15 tests 통과
- network 관련 범위: 2 tests 통과
- `StoreDetailActivity.getIntent`/`BossStoreDetailActivity.getIntent` direct caller: 0건
- V2 package의 legacy Activity fallback: 0건
- OpenAPI custom action enum 18개와 ViewModel 처리 목록 일치
- `StoreDetailV2Content` direct `when`에 section 16개 존재
- staged 파일 없음, 보호 파일 stage 없음
- final review 보강 회귀 test: repository exception/content 보존, Home loading 종료, favorite override, false child result, display 시점 view log, APP_SCHEME routing, exact sheet anchor, sticky action sentinel, review report validation

기존 프로젝트의 Kotlin plugin 중복 로드, deprecated API와 annotation target warning은 baseline과 동일하게 출력됐다.

추가로 final gate에서 `./gradlew test :app:assembleDebug`를 실행했으나, TH-1226 변경 범위 밖의 기존 `:core:abtest:kaptDebugUnitTestKotlin`과 `:core:abtest:kaptReleaseUnitTestKotlin`이 `ExampleUnitTest`의 `@error.NonExistentClass` stub 오류로 실패했다. 해당 test 파일은 기준 커밋 `4ab76e8f65aed8608f3afd1df8237b6181f7c222`과 동일하고, `core/abtest` 및 Gradle 설정에는 이번 변경이 없다. 위에 기록한 TH-1226 관련 test/compile과 독립 `:app:assembleDebug`는 모두 성공했다.

## 사용자 수동 확인 checklist

에이전트는 지시에 따라 아래 항목을 실행하지 않았다.

- [ ] Home marker 선택 시 기존 Preview가 즉시 표시되고 V2는 background load된다.
- [ ] Preview tap/upward drag로 같은 sheet가 300ms Expanded 되고, back/downward drag가 Preview로 돌아간다.
- [ ] 기존 Home list `Collapsed <-> FullList`와 Preview 내부 action tap이 회귀하지 않는다.
- [ ] USER/BOSS 실제 응답의 16개 section 조합이 server order와 TO-BE 구성대로 표시된다.
- [ ] 상단 action이 사라질 때만 하단 sticky action이 표시된다.
- [ ] list/deep link/push/share/favorite/my page/related store가 V2 full-screen을 연다.
- [ ] 저장, 방문, 리뷰 작성·신고·삭제·좋아요, 공유, 길안내, 지도/주소, 수정/없는 장소, 사진, 쿠폰, post, CTA/link action의 성공·실패가 legacy UX와 일치한다.
- [ ] child Activity/Dialog 성공 후 V2 content와 caller 목록이 refresh된다.
- [ ] first load/action/refresh 실패는 Toast/Snackbar를 표시하고 가능한 기존 content를 유지한다.
- [ ] `not_exists_store`와 initial 방문의 필수 위치 누락은 현재 container를 닫는다.
- [ ] Map/Ad가 pause/resume/화면 이탈 후 정상 동작하며 다른 section을 깨지 않는다.
