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
