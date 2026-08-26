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
- final code review에서 확인한 예외/상태 경계를 보강했다.
  - repository exception은 first load를 `Error`로 전환하고 refresh/mutation에서는 기존 content를 유지한 채 공통 오류를 표시한다.
  - Home first load 실패 시 기존 Preview는 유지하되 loading indicator를 종료하며, 필수 위치 누락은 오류를 표시하고 현재 sheet를 닫는다.
  - 즐겨찾기 성공 상태는 refresh 실패와 무관하게 화면/caller result에 보존한다.
  - APP_SCHEME은 기존 방문·제보자·리뷰·DynamicLink 목적지를 명시적으로 구분하고, 알 수 없는 route가 Home으로 오진입하지 않게 차단한다.
  - Preview action row visibility sentinel과 exact anchor 동기화를 추가해 sticky action/외부 expanded state를 맞춘다.
  - visit/review stars, image subtitle, related context label을 렌더링하고 AdMob impression을 ViewModel dedupe 경계로 통합했다.
  - Home background preload에서는 page view를 보내지 않고, 공유 `StoreDetailV2Content`가 실제 표시될 때 host instance/store당 한 번 전송한다.
  - 리뷰 신고는 사유 선택과 필수 상세 입력을 검증하며, child result·initial action·Fragment container 복원 상태를 실제 성공 결과에만 연동한다.

## 범위 확인

- dependency, Gradle, toolchain 변경 없음
- rollout gate와 legacy fallback 없음
- 보호 대상 `AGENTS.md`, `docs/features/TH-1128-store-detail-action-nudge/03-implementation.md`에 대한 에이전트 수정·stage 없음
- emulator/실기기/browser/screenshot/authenticated live preflight 실행 없음
