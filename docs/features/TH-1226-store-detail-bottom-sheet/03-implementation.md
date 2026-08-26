# TH-1226 Store Detail V2 구현 기록

## 구현 결과

- `GET /api/v2/screen/store/{storeId}`를 `Long` path와 nullable 위치 header로 `ServerApi`/`ScreenRepository`에 연결했다.
- raw `List<JsonObject>`를 section별로 독립 decode하는 mapper와 display model 한 경계를 추가했다.
- Swagger의 16개 section을 server order로 direct `when` 렌더링하고 unknown/깨진 section만 제외한다.
- `StoreDetailV2ViewModel`에 load 취소·현재 store 확인, content 유지 refresh, mutation, child result, page/impression/click log를 연결했다.
- OpenAPI의 18개 custom action enum을 모두 feature-local `when`에서 platform event 또는 기존/new repository mutation으로 연결했다.
- MapView와 AdView lifecycle은 각 Compose host 한곳에서 관리하고 dispose 시 정리한다.
- 기존 Home list/Preview 구현을 유지하면서 선택 store에만 `Preview <-> Expanded` anchor와 nested scroll을 추가했다.
- `StoreDetailV2Activity.getIntent()`와 공통 `StoreDetailV2Content`를 추가하고 deep link, push, list, favorite, my page 등 direct legacy caller를 V2로 전환했다.
- legacy `StoreDetailActivity`/`BossStoreDetailActivity`와 기존 Dialog/하위 Activity는 삭제하지 않았다.
- `DeleteStoreDialog`에는 legacy 동작을 유지하는 optional FragmentResult mode만 추가했다.

## 범위 확인

- dependency, Gradle, toolchain 변경 없음
- rollout gate와 legacy fallback 없음
- 보호 대상 `AGENTS.md`, `docs/features/TH-1128-store-detail-action-nudge/03-implementation.md`에 대한 에이전트 수정·stage 없음
- emulator/실기기/browser/screenshot/authenticated live preflight 실행 없음
