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

## 2026-08-28 실응답 교정

사용자 emulator QA와 로그인 세션의 `GET /api/v2/screen/store/120024` 응답을 기준으로 다음 계약 차이를 수정했다.

- Home bottom sheet list card 첫 tap이 full-screen `StoreDetailV2Activity`를 열던 연결을 제거하고, marker와 동일하게 기존 Preview를 선택한 뒤 같은 `MainActivity` sheet에서 Expanded로 전환하도록 연결했다. 별도 Home list 화면과 deep link 등 기존 full-screen caller는 그대로 유지한다.
- 실제 `isHtml=true`의 CSS `<span style="font-size:...; font-weight:...; color:...">`를 공통 `SDTextRenderer`가 `AnnotatedString`으로 해석하도록 보강했다. V2 section의 raw `.text` 출력은 공통 text/chip/button/surface renderer로 교체했다.
- V2 상단 4개 action은 기존 Home Preview의 `StorePreviewActionBarRow`를 그대로 재사용해 icon, 문구, 간격과 한 줄 배치를 동일하게 맞췄다.
- 실제 `VISIT.summary.chips`를 별도 visit summary model로 수용하면서 Swagger의 기존 rating summary 형태도 호환하도록 mapper를 분리했다.
- 실제 `/store-contributors`, `/stores/{id}#home|info|images|reviews`, `IMAGE_ID`/`IMAGE_URL` action을 route와 image index 해석에 반영했다.
- 서버의 누락된 제보자 이름이 `null님이 ...`로 노출되지 않도록 해당 contributor 문구만 안전한 fallback으로 정리했다.

## 범위 확인

- dependency, Gradle, toolchain 변경 없음
- rollout gate와 legacy fallback 없음
- 보호 대상 `AGENTS.md`, `docs/features/TH-1128-store-detail-action-nudge/03-implementation.md`에 대한 에이전트 수정·stage 없음
- 실기기는 실행하지 않았고, authenticated dev API와 `emulator-5554`의 Debug APK로 Home list card → Preview → 같은 sheet Expanded, HTML/style, action row, tab, contributor route를 확인했다.
- emulator의 초록색 focus 테두리는 앱 UI가 아니라 TalkBack 접근성 focus였으며 QA 중 접근성 service를 비활성화한 상태에서 다시 확인했다.

## 2026-09-03 renderer fidelity 교정

store `120120`의 현재 dev 실응답과 emulator 캡처를 대조해 다음 표현 경계를 보강했다.

- server의 CSS식 8자리 색상 `#RRGGBBAA`를 Android가 해석하는 `#AARRGGBB`로 정규화했다. `#18181899` MAP action 배경은 60% alpha의 `#181818`로 표시된다.
- 공통 `SDActionButton`에 image override slot을 추가하고, 403인 MAP/EDIT server asset에는 기존 design system copy/zoom/edit/report icon을 action type 기반 fallback으로 표시한다.
- MAP 주소 action은 남은 폭을 사용하고 icon-only 확대 action은 48dp로 제한했다.
- AdMob load 실패 시 고정 72dp slot을 제거해 section 사이 빈 공간이 남지 않게 했다.
- `INFO_V1.informationCard`, `INFO_V1.menuCard`, `REVIEW.summary`의 server surface style을 적용했다.
- 표시 텍스트가 모두 빈 menu item은 mapper에서 제외해 빈 row가 section 높이를 늘리지 않게 했다.
- PREVIEW metadata separator와 rating image의 server width/height를 렌더링했다.

서버 원인은 별도로 남아 있다. root 경로의 `Edit_fill.png`, `deletion.png`, `copy.png`, `zoom_3x.png`는 2026-09-03 GET 기준 HTTP 403이며, `리뷰`/`0개`와 `방문 성공`/`0명` span 사이에는 공백이 없다. Android는 알려진 action icon만 local fallback하고 문구는 서버 원문을 유지한다.

## 2026-09-03 strict server-driven 표시

사용자 요청으로 상세 화면을 현재 서버 응답만 비교할 수 있는 strict 표시로 전환했다. 이 결정은 앞선 Figma fidelity 보정보다 우선한다.

- `StoreDetailV2Content`는 `screen.sections`만 server order로 렌더링하며 section divider와 scroll sticky action을 추가하지 않는다.
- PREVIEW의 local 저장 action과 contributor `null` 문구 보정을 제거했다. action row는 서버 `actionBars`를 한 번만 표시한다.
- MAP/EDIT action의 local icon fallback을 제거하고 서버 image만 사용한다.
- INFO_V1의 빈 menu item도 서버가 보낸 항목이면 보존한다.
- REVIEW card 사이에 Android가 추가한 1dp divider를 제거했다.
- Home Expanded 상태에서는 bottom sheet handle과 Home bottom navigation/divider를 숨긴다.
- V2 full-screen Activity의 local top app bar를 제거하고 system back만 유지한다.
- MAP의 `location`을 Naver Map으로, AD_MOB card를 AdView로 해석하는 section renderer와 action 처리는 유지한다.

이에 따라 HTTP 403인 서버 image는 local icon으로 대체되지 않아 빈 icon 영역으로 보일 수 있고, 서버가 `null` 또는 빈 text를 보내면 그대로 화면에 나타난다. 이는 strict 비교 모드의 의도된 결과다.

## 2026-09-03 AdMob scroll 생명주기 수정

- 상세 `AD_MOB`의 `AdView`와 load state를 `LazyColumn` section item 밖의 `StoreDetailV2Content` scope에서 store ID와 card ID로 유지한다.
- Lazy item 이탈·재진입은 `AndroidView.onReset`, `update`, `onRelease`에서 resume/pause만 수행한다.
- screen/store 교체 또는 상세 종료로 parent effect가 dispose될 때만 보유한 `AdView`를 destroy한다.
- 최초 load 실패는 기존처럼 slot을 제거하지만, 이미 `Loaded`인 상태의 refresh 실패는 기존 광고를 유지한다.
- 광고 click/impression callback은 재사용되는 state에 최신 server card/log callback을 갱신한다.
