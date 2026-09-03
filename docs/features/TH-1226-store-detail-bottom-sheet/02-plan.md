# TH-1226 Store Detail V2 구현 계획

기준 설계: `00-design.md`

## 파일 그룹

1. `core/network`, `core/common`: V2 endpoint/raw response, 필요한 공통 SDUI field, V2 display model
2. `data`, `domain`: response → display model mapper, screen load와 누락 mutation repository
3. `app/ui/storeDetail/v2`: `StoreDetailV2ViewModel`, direct `when` renderer, section UI, action host, full-screen Activity
4. `app/ui/home`: 기존 Preview를 유지한 `Preview <-> Expanded` sheet와 Home V2 host
5. 기존 caller/deep-link/push: legacy 상세 destination을 V2 full-screen으로 교체
6. 관련 unit test와 `03-implementation.md`, `04-verification.md`

## TDD 순서

- [ ] V2 Retrofit path/int64 ID/optional 위치 header 계약을 실패 테스트로 고정한다.
- [ ] 작은 section-family JSON fixture로 16개 type, server order, unknown skip, 필수 top-level 실패, dynamic extra type을 고정한 뒤 mapper를 구현한다.
- [ ] load/error, store 전환 stale response 방지, mutation refresh/content 유지, child result refresh를 실패 테스트부터 구현한다.
- [ ] action enum이 기존 Repository/Dialog/Activity/platform destination에 연결되는 계약을 테스트하고 feature-local `when`을 구현한다.
- [ ] Preview/Expanded anchor·back·nested scroll 계약을 pure test로 고정하고 기존 Home sheet에 연결한다.
- [ ] full-screen Intent initial action/result와 모든 caller route를 교체하고 정적 `rg` guard로 확인한다.

## 검증 checkpoint

- API/model: `:core:network:testDebugUnitTest`, `:data:testDebugUnitTest`
- state/action/sheet/route: 관련 `:app:testDebugUnitTest`
- compile 및 최종: 필요한 module compile task, `:app:assembleDebug`
- 정적 확인: legacy direct caller 0건, `git diff --check`, dependency/toolchain 무변경, 보호 파일 stage 제외
- 수동 UI/실서버 로그인 검증은 실행하지 않고 `04-verification.md` checklist로 남긴다.

## 보호 경계

- `AGENTS.md`, `docs/features/TH-1128-store-detail-action-nudge/03-implementation.md`는 수정·stage·commit하지 않는다.
- dependency/toolchain, legacy Activity 파일, 범용 renderer/action/state framework는 변경하지 않는다.

## 2026-08-28 실응답 교정 계획

사용자 emulator QA와 `GET /api/v2/screen/store/120024` 실응답으로 확인된 계약을 우선 재현한다.

1. Home list card가 Activity를 열지 않고 marker와 같은 Preview를 선택하는 계약을 실패 테스트로 추가한다.
2. `isHtml=true` text, image가 포함된 chip/button, surface style을 실제 응답 형태 fixture로 고정하고 기존 공통 SDUI element renderer를 재사용한다.
3. 런타임 응답과 Swagger가 충돌하는 `VISIT.summary.chips`는 실제 응답도 손실 없이 처리하되, 기존 review summary 모델과 분리한다.
4. 실제 `/store-contributors`, `/stores/{id}#home|info|images|reviews`, `IMAGE_ID`/`IMAGE_URL` action을 실패 테스트로 고정한다.
5. 관련 unit test, compile, `assembleDebug`, debug APK install 후 Home list/marker 양 경로를 emulator에서 재검증한다.

## 2026-09-03 renderer fidelity 교정 계획

`GET /api/v2/screen/store/120120` 실응답과 Android emulator 전체 스크롤 결과에서 확인된 client-side 표현 손실을 교정한다.

1. server의 8자리 `#RRGGBBAA` 색상을 Android `#AARRGGBB`로 정규화하는 실패 테스트를 추가한다.
2. MAP/EDIT action type에 기존 design system icon fallback을 연결하고, MAP 주소 action은 가변 폭, 확대 action은 48dp 고정 폭으로 분리한다.
3. 광고 load 실패 시 `AD_MOB` slot을 제거하고, INFO/REVIEW card의 server surface style을 적용한다.
4. 빈 menu item은 mapper 경계에서 제외하고, PREVIEW metadata separator와 rating image server size를 렌더링한다.
5. 관련 unit test, compile, `assembleDebug`, APK install 후 store `120120`을 emulator에서 다시 전체 스크롤한다.

## 2026-09-03 strict server-driven 표시 계획

사용자 확인에 따라 Figma 보정 UI보다 현재 V2 응답을 우선하는 비교용 strict 표시를 적용한다.

1. 상세 renderer가 서버 section 밖에서 추가한 저장 action, section/review divider, sticky action을 제거한다.
2. MAP/EDIT action의 local icon fallback과 contributor 문구 보정, 빈 menu item 필터를 제거해 서버값을 그대로 사용한다.
3. Home Expanded에서는 sheet handle과 Home bottom navigation을 숨기고, full-screen 상세의 local top app bar를 제거한다.
4. MAP/AD_MOB처럼 section type을 플랫폼 컴포넌트로 해석하는 필수 renderer와 action routing은 유지한다.
5. source contract와 mapper test를 RED→GREEN으로 고정하고, 관련 unit test, `assembleDebug`, emulator 스크롤로 검증한다.

## 2026-09-03 AdMob scroll 생명주기 수정 계획

1. 상세 `AD_MOB`의 상태와 `AdView` 소유권을 Lazy item 밖의 상세 content로 올린다.
2. `AndroidView`는 `onReset`으로 재사용하고 item release에서는 pause만 수행한다.
3. store/screen이 바뀌거나 상세 content가 종료될 때만 `AdView.destroy()`를 호출한다.
4. 한 번 `Loaded`가 된 광고는 이후 refresh 실패가 와도 기존 광고를 유지하고, 최초 load 실패만 slot을 접는다.
5. state transition과 source lifecycle contract를 RED→GREEN으로 검증한 뒤 관련 app test, assemble, emulator 로그를 확인한다.
