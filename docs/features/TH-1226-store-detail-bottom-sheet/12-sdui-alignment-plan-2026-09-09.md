# 운영 서버 SDUI Android 정비 Implementation Plan

> 승인: 2026-09-09 사용자가 이 계획의 구현과 검증을 명시 요청했다. 동일 설계·실행 승인 재질문 없이 진행한다.

**상태:** 2026-09-10 승인 범위 구현·필수 검증 완료. 결과는 13/14 문서에 기록한다.

**Goal:** 운영 v4.76.1의 홈·상세·기여자 SDUI 데이터와 동작을 Android에 반영하고 Home 정보 수정 크래시를 해결한다.
**Architecture:** 기존 MVVM, core network DTO → data mapper → common model → Compose/host 흐름을 유지한다.
**Tech Stack:** 현 Kotlin/Compose/Hilt/Retrofit/Gson, JDK17. 의존성·앱 버전 변경 없음.
**Spec:** 사용자가 승인한 대화 내 계획 및 `10-qa-investigation-2026-09-09.md`, `11-server-deployment-contract-audit-2026-09-09.md`.

## Global Constraints

- 서버 기준 `pocket-three/pocket-backend`, 운영 v4.76.1 `3d2691298340fe1a53923cbc1c88b0060dd44c3a`. 구현 시작 재조회에서도 최신 운영 배포 동일.
- Android 기준 `feature/TH-1226-store-detail-bottomsheet`, `24c102eb3573f3abb48155f1a051a099d403ac32`. 사용자 미추적 문서 10/11 보존.
- 앱/서버 원격 변경·업로드·실제 쿠폰/리뷰/공유 전송 금지. Fake repository 및 테스트 광고 사용.
- 테스트를 먼저 추가하고 의도한 assertion 실패를 관측한 뒤 관련 구현. 기존 변경을 되돌려 TDD를 재연하지 않는다.
- 섹션 순서 유지. 미지원 미래 타입/깨진 하위항목은 해당 항목만 제외. 임의 섹션/chrome 추가 금지.
- 기존 standalone MAP, 확인된 아이콘 URL 보정은 호환 경로로 유지.
- 계좌 복사는 은행명+계좌번호 표시 문구의 HTML 제거본. 예금주 제외, 추가 API 없음.
- 로그는 누락 값/연결만 보완하며 현재 응답 수신 기준 impression 시점을 유지.
- 후속 QA: 사진 빈 상태/바로 picker, 메뉴 접기, Preview 본문 드래그, 딥링크 지도 복귀, USER 사진리뷰.
- 구현자는 소유 파일만 수정하며 다른 작업자 변경을 되돌리지 않는다. git commit/push는 통합 담당자 지시 없이 하지 않는다.
- Gradle은 JDK17, --offline 우선. 같은 빌드 트리에서 concurrent Gradle 호출을 하지 않도록 주 에이전트와 조율한다.

## Task 1: 계약 DTO/model/mapper와 fixture

**소유:** `core/common/.../serverdriven/model/*`, `core/network/.../data/screen/*`, `data/.../screen/*`, data screen tests/resources. app/core:ui 변경 금지.

- [x] 신규 assertion 테스트를 작성하고 현 코드에서 실패 확인. Gson으로 모델을 재직렬화하여 새 필드 유실을 검사하면 새 타입 부재로 인한 compile-error 대신 실제 실패를 관측할 수 있다.
- [x] `/private/tmp/gasam-sdui-audit-20260909/prod-store-{120024,106775,121173,525611}.json`을 익명화한 고정 fixture 보관. ID/가게명/작성자/본문/계좌/사용자사진 등 식별값을 테스트값으로 바꾸되 구조·타입·nullable·서버아이콘·크기/스타일은 유지. 출처 메타데이터는 commit/store종류/section count로 별도 기록. 계좌번호 예금주 실값 저장 금지.
- [x] 배포소스 `/private/tmp/gasam-sdui-audit-20260909/backend-prod` 기반 coupon/POST 및 16종 fixture를 추가한다. OpenAPI Content/Summary 이름충돌은 실제 서버 source/payload로 해소한다.
- [x] EDIT.map을 nullable 지도 모델(location, footerLeft?, footerRight)에 연결. actionBars는 EDIT 최상위. CALLOUT content(image,text,style) 모델을 CTA와 분리. MARGIN Int height 지원, 음수0. 기존 MAP 지원 유지.
- [x] HomeFilterScreen에 configuration(initialMapZoomLevel), HomeListSection에 focusBounds(southWest,northEast), BasicCard refs(type,storeId,storeType), body(text,style) 보존. marker nullable 허용하여 카드 유지.
- [x] HomeFilter/contributor에서 SDChip imageAlignment/contentSpacing, SDHeader subtitle/trailing, button/log 속성 및 screen viewLog 전체 extraParameters를 보존. EmptyCard impressionLog 보존.
- [x] contributor CALLOUT_CARD 명시 discriminator, description nullable 허용. 현재 ICON_TEXT/History 및 paging 호환 유지.
- [x] 기존 tests 중 잘못된 CALLOUT.title 및 구MAP만 있는 'all types' fixture를 최신 계약으로 보완, 구MAP 호환은 별도 테스트로 남긴다.
- [x] `:data:testDebugUnitTest --tests 'com.threedollar.data.screen.*'` 및 common serverdriven tests 검증. 모든 운영 fixture count14/15/15/16 보존과 EDIT map내용 assertion.
- [x] 변경한 public model 이름/생성자/새필드/defaults를 담당자에게 즉시 전달. 앱 compile은 다음 단계에서 연결한다.

## Task 2: 공통 렌더러와 기여자

**소유:** core:ui serverdriven renderer, common serverdriven ext/관련 테스트(모델 제외), app contributor 전체 및 tests. Task1 모델은 수정 요청만 전달.

- [x] START/END·imageOnly/textOnly·spacing·nullable description·HTML CSS를 테스트 먼저 고정한다. 존재하지 않는 노드 사이에 spacer를 만들지 않는다.
- [x] SDChip renderer가 정렬·간격·배경·테두리·additionalText를 반영하도록 수정. 공통 SDImage renderer가 width/height를 읽고 부모폭보다크면 같은비율축소. 기존 기본값은 값없을때만 적용.
- [x] HTML 스타일은 existing styledSegments 재사용. nested span/두색 CALLOUT/빈 문자열 처리 테스트. 표시용과 plain copy용을 분리.
- [x] 공통 header subtitle/trailing/action을 연결하고 기여자 CALLOUT_CARD nullable description을 표시한다.
- [x] 기여자 screen server viewLog/extraParameters를 전달하고 기존 동일 resume pageview와 이중 전송되지 않게 한다. 서버값 없을때 legacy fallback. button/actionBar 서버 clickLog 우선, 로컬 동일로그는 fallback만.
- [x] 기존 link navigation 및 edit flow 유지. 이미 지원하는 서버 버튼 동작과 log payload를 손실없이 host로 전달한다.
- [x] common tests, 새 contributor tests 및 렌더 검증. 필요한 helper API를 Task3/4에 전달.

## Task 3: 상세 렌더러·액션·좌표

**소유:** `app/.../storeDetail/v2/*` 및 v2 tests, 공통화가 필요한 v2 helper 신규파일. HomeFragment는 직접 수정하지 않고 host 연결 API를 Task4에 전달.

- [x] 현행 action tests에 새 EDIT action/쿠폰 COUPON_ISSUED_KEY/좌표pair/계좌copy/#post 케이스를 먼저 추가해 실패 관측.
- [x] EDIT map을 기존 Naver map content와 같은 생명주기로 표시하고 그 아래 actionBars 표시. CALLOUT 전용 content, MARGIN 순서대로 표시, 고정 구분선/chrome 삽입금지.
- [x] 공통 순수 좌표resolver: 유효한 명시lat/lng pair→EDIT.map→legacyMAP→같은가게home marker. 좌표범위 검사, 다른sourcepair혼합/0대체금지. Activity/host가 재사용하도록 API 제공.
- [x] 새 EDIT_COPY_ADDRESS/EDIT_MAP_ENLARGE를 지원, 구MAP action alias는 유지. 쿠폰 COUPON_ISSUED_KEY우선/ISSUED_KEYfallback. 18개 action names 및 실제 builder key 대조.
- [x] 방문인증/공유/확대/길안내에서 resolver 사용. 좌표없으면 안내하고 화면유지(강제finish금지). Home host wiring에 필요한 helper를 전달.
- [x] 계좌 copy를 typed local PlatformAction으로 전달, displayed account.text plainText만 복사, additionalText예금주 제외. clickLog한번/기존복사toast.
- [x] TAB #post 스크롤, target없으면 비활성/외부deeplink전달금지. 기존 home/info/images/reviews 유지.
- [x] PREVIEW/APPEARANCE_DAY/VISIT/INFO_V2/REVIEW의 서버style및image치수반영. reply.header.subtitle 및 header.trailing처리. Task2 helpers재사용.
- [x] V2 AdMob 첫카드제한제거, cardId별상태/load/click/impression/lifecycle유지. firstloadfail숨김정책 유지, 테스트callback/테스트광고만.
- [x] V2 unit tests (fake mutation) 및 Debug compile검증. Home host 변경 명세를 다음작업에 전달.

## Task 4: 홈 지도·리스트·host·크래시

**소유:** app HomeViewModel/HomeUIState/home data/helpers/HomeFragment/HomeBottomSheetContent/HomeFilterChips, NaverMapFragment 및 관련 tests. v2파일은 직접변경금지(필요시담당자요청).

- [x] 사용자 승인 TDD 회복 절차로 기존 변경을 보존하고 필터지연/실패·stale response·pagination·nullablemarker·좌표/카메라정책의 실제 회귀 테스트를 보강했다. 초기 compile-error RED의 절차 차이와 후속 assertion RED→GREEN은 13 문서에 구분한다.
- [x] 첫목록요청은 필터초기화success/failure뒤 전송. 실패fallback sortTypePOPULAR; 뒤늦게필터복구하면 최신선택으로한번refresh. 반복중복요청방지.
- [x] 요청generation/cancel을 통해 늦은필터·옛목록이 최신state/bounds/cursor를 덮지못하게 한다. 기존선택보존refresh 유지.
- [x] initialMapZoomLevel 최초cameraplacement에만. 복원카메라/이미사용자조작있으면late config무시. mapready/filterresolved 상태조합 tests.
- [x] focusBounds 첫페이지에만 적용. topfilter/bottomsheet 가림영역반영fitBounds, pagination/ordinaryrefresh에서강제이동금지. camera자동이동이무한재조회로돌지않게한다.
- [x] Basic marker nullable전파, 리스트카드보존/지도marker만생략; card refs우선ID/type선택, 링크/cardId호환fallback. 좌표없는preview에가짜좌표금지. marker대상목록index/cardId연결회귀방지.
- [x] 홈 displayText렌더경로를Task2공통HTML렌더로전환. chipstyle/spacing/START-END/body.style/card.style/separator/image치수반영.
- [x] Home server pageView extras연결, 동일진입legacy중복방지. EMPTY impression 및 AdMob click전달, 기존응답수신impression주기유지.
- [x] Home 모든상세플랫폼액션이Task3좌표resolver/계좌copyaction을사용. EDIT fragment는 activity.supportFragmentManager 사용, tag중복진입guard와result/backstack소유일치. 현재가게/scroll유지.
- [x] home/v2/common/contributor tests 및 Debug assemble. host통합이완료되면root QA로인계.

## Task 5: 통합 검증과 문서

**소유:** root, review agents는 read-only. 소스충돌은담당구현자에게반환한다.

- [x] 각 task diff/spec+quality review 및 whole-change review. 요구사항미충족은검토횟수만으로완료처리하지않는다.
- [x] common serverdriven/data screen/app home+v2 및 신규contributor/coreui tests 실행, app assembleDebug.
- [x] USER/BOSS/VERIFIED × Home/fullscreen 실제 UI 지도/CALLOUT/여백/image/style검증. emulator기존데이터보존. 서버쓰기금지.
- [x] 정보수정진입·취소·뒤로가기·상태보존, fake수정결과갱신; 방문인증진입/지도확대/주소계좌복사/공유선택창/탭검증.
- [x] 이벤트제주bounds 및pagination카메라안정; 테스트광고load/failure/callback. 실제mutation은fake만.
- [x] 명령/결과/기기/스크린샷/미검증/남은문제를 13 implementation log,14 verification에기록. 모든필수검증pass전전체완료선언금지.
