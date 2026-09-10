# 배포 서버 SDUI ↔ Android 계약 감사 — 2026-09-09

현재 Android는 운영 서버 SDUI를 완전히 지원하지 않는다. 운영 V2 상세의 섹션 종류 16개 중 `MARGIN`, `CALLOUT`은 실제 응답에서 탈락하고, `EDIT`는 지도 없이 버튼만 남는다. 액션 이름·파라미터, 홈 지도 범위, 일부 스타일·로그에도 누락이 있다. 섹션 분기 개수만으로 전체 구현률을 계산해서는 안 된다.

이 문서는 조사 결과다. 앱/서버 기능 수정, 원격 배포·게시·리뷰·쿠폰 등 상태 변경은 수행하지 않았다. 기존 `10-qa-investigation-2026-09-09.md`는 수정하지 않았다.

## 1. 비교 기준과 실제 배포 근거

| 대상 | 확인한 기준 |
| --- | --- |
| Android | `feature/TH-1226-store-detail-bottomsheet`, HEAD `24c102eb3573f3abb48155f1a051a099d403ac32`, 4.22.1 (128) |
| 실제 서버 저장소 | [pocket-three/pocket-backend](https://github.com/pocket-three/pocket-backend), 비공개. 예전 `3dollar-in-my-pocket/3dollars-in-my-pocket-backend` 주소가 이 저장소로 연결됨 |
| 운영 | [v4.76.1 릴리스][release], SHA `3d2691298340fe1a53923cbc1c88b0060dd44c3a` |
| 운영 배포 | [전체 배포 run 34147339864][prod-run] 성공. **유저 API ECS 배포 job 완료: 2026-09-08 02:55:18 KST**. 전체 workflow 완료 03:53:35 KST |
| 개발 | [run 34296079228][dev-run], SHA `a95e560c19fc8f0b95e36b719ad780c8ccf06d39`, 2026-09-09 배포 성공 |
| 공개 OpenAPI | [개발 OpenAPI][openapi]의 `info.version=34296079228`, 개발 배포 run ID와 일치. 운영 `/api/v3/api-docs`는 HTTP 404 |

운영 전체 배포 이후 더 최근의 유저 API 단독/단계별 배포가 있는지도 각 workflow run 목록에서 확인했다. 단독 배포 최근 실행은 6월, 단계별 배포 최근 실행은 8월이므로 조사한 GitHub 배포 기록의 최신 운영 기준은 v4.76.1이다. 현재 ECS의 task digest를 클라우드에서 직접 조회한 것은 아니다. 운영 HTTP 응답으로 주요 변경이 실제 서비스 중임을 별도 확인했다.

v4.76.1 릴리스에 EDIT·MAP 통합, MARGIN 추가, Preview 리뷰 미리보기 제거, 영업일 전체 요일 표시, 앱 이미지 경로 수정이 명시돼 있다. 운영/개발 커밋 사이 6개 커밋의 파일 차이도 확인했다. 이 문서의 핵심 CALLOUT/EDIT/MARGIN/쿠폰 계약은 운영 소스에서 검증했다. 개발에만 있는 변경을 운영 배포로 취급하지 않았다.

## 2. 실응답과 mapper 실행 결과

인증 없이 조회 가능한 GET만 사용했다. `User-Agent: 4.22.1 (com.zion830.threedollars); 36`, 위치가 필요한 요청에는 점검용 서울 좌표 헤더를 명시했다. 테스트 계정 생성이나 로그인 토큰 발급은 하지 않았다.

운영 홈/일반 목록/BOSS 목록/이벤트 목록/인증 목록/기여자, 아래 상세 4개를 합쳐 **운영 응답 10개 HTTP 200**을 확보했다. 개발 상세 120024도 HTTP 200이었다. 목록 첫 시도는 위치 헤더 누락으로 400이었고, 필수 헤더를 넣어 성공했다.

현재 Android mapper 클래스로 실제 JSON을 재생한 결과:

| 운영 가게 | 구분 | 서버 섹션 수 → 앱 모델 수 | 확인된 탈락 |
| --- | --- | --- | --- |
| 120024 | USER | 14 → 9 | MARGIN 5개 |
| 106775 | BOSS | 15 → 9 | MARGIN 6개 |
| 121173 | 공식인증 USER | 15 → 9 | CALLOUT 1개 + MARGIN 5개 |
| 525611 | 공식인증 BOSS | 16 → 9 | CALLOUT 1개 + MARGIN 6개 |

위 숫자는 배열 원소 수이며 섹션 종류 지원률이 아니다. 살아남은 EDIT 모델에도 서버의 `map`이 없다.

로그·가게 본문 전체 대신 구조와 SHA-256을 담은 [증거 요약][evidence] 및 [mapper 재생 결과][replay]를 로컬 build 하네스에 저장했다. 원본 응답, 서버 소스 archive, 개발 OpenAPI는 `/private/tmp/gasam-sdui-audit-20260909/`에 있다.

## 3. 수정이 필요한 확정 차이

| 우선순위 | 항목 | 서버와 Android의 차이 | 검증 수준 |
| --- | --- | --- | --- |
| P1 | EDIT 지도 | 서버는 `EDIT.map`과 최상위 `EDIT.actionBars`. 앱 DTO/model/mapper/renderer는 actionBars만 처리. 지도 전체 유실 | 운영 4개 상세 실응답 + mapper 실행 |
| P1 | 지도 액션 | 서버 `STORE_EDIT_SECTION_COPY_ADDRESS`, `STORE_EDIT_SECTION_MAP_ENLARGE`; 앱은 예전 `STORE_MAP_SECTION_*`만 분기 | 운영 EDIT 액션 + 양쪽 코드 |
| P1 | 전체 화면 상세 좌표 | 방문 인증·공유 등의 좌표를 독립 `Map` 섹션에서 검색. 통합 EDIT만 내려오면 방문 인증은 위치 오류 후 종료, 공유는 좌표 없이 return하는 코드 경로 | 실제 EDIT 응답 + Activity 코드; 기기 클릭 미실행 |
| P2 | CALLOUT | 서버 `content.image/text/style`; 앱은 CTA와 같은 `title/subTitle/footerLeftButton` 구조로 파싱. title 부재로 CALLOUT 전체 탈락 | 인증 USER/BOSS 실응답 + mapper 실행 |
| P1 | 쿠폰 사용 키 | 서버 `COUPON_ISSUED_KEY`; 앱은 `ISSUED_KEY`만 읽음. 값이 null이어서 use API 호출하지 않음 | 운영 배포 소스 + ViewModel 코드; 실제 쿠폰 사용 미실행 |
| P1 | 홈 이벤트 지도 범위 | `focusLabels=LIVE_ALONE_YURI` 선택 결과에 제주 `focusBounds`를 내려주지만 앱 DTO/model에 필드 없음 | 운영 AOS 4.22.1 응답으로 실제 발행 확인 |
| P2 | MARGIN | 서버 배열에 `{type:MARGIN,height:8}`를 삽입. 앱은 unknown section으로 제거 | 운영 실응답 + mapper 실행 |
| P2 | 계좌 복사 | BOSS INFO_V2의 copyButton은 clickLog만 있고 link/customAction 없음. 앱은 일반 onAction으로 보내므로 복사 처리 없이 return | 운영 106775 버튼 + 양쪽 코드; 클립보드 실행 미검증 |
| P2 | BOSS 소식 탭 | 서버 `/stores/{id}#post`. 앱 section anchor는 home/info/images/reviews만 지원해 post로 스크롤하지 못함 | 운영 BOSS TAB + 코드. 표본은 POST 본문도 없어 서버의 빈 탭 노출과 구분 필요 |
| P2 | 초기 지도 줌 | 운영 `configuration.initialMapZoomLevel=13.3`; 앱은 configuration 수신/전달 경로 없음 | 운영 홈 응답 + DTO/mapper/camera 코드 |
| P2 | 서버 페이지 로그 | 홈의 `preset`, 기여자의 `store_id` 등 추가 파라미터 유실. 기존 하드코딩 pageview는 별도로 존재 | 운영 응답 + 로컬 probe/호출부 |
| P2 | 광고/빈 카드 로그 | 홈 AdMob clickLog는 모델에 있지만 클릭 callback에 연결 없음. EMPTY impressionLog는 model/mapper에서 제거 | 운영 AdMob 응답, EMPTY 운영 builder + 앱 코드 |
| P2/P3 | 일부 스타일 | 홈 inline HTML weight/색/크기, chip 간격, body/card style·separator 등은 고정 UI로 처리하는 부분 존재 | 실제 발행값 + HTML probe/렌더 코드; 픽셀 검증 미실행 |

### EDIT는 지도만 표시해서 끝나는 변경이 아니다

실제 EDIT JSON의 `map`과 `actionBars`는 **형제 필드**다. 기존 명세 예제의 중첩 모호함은 운영 payload로 해소됐다. `map`에는 `location`, `footerLeft`, `footerRight`가 있다. 운영 map 확대 action의 extraParams는 빈 객체이므로 지도 위치를 EDIT 모델에서 제공해야 한다.

- Android [DTO][detail-dto] 42행, [mapper][detail-mapper] 151행, [model][detail-model] 33행에서 map을 보존하지 않는다.
- [ViewModel][detail-vm] 119–120행과 상수 323–324행은 예전 MAP 액션 이름을 사용한다.
- [V2 Activity][detail-activity] 268행 방문 인증, 325행 지도 확대, 348–351행 공유는 독립 Map 섹션을 검색한다. 길안내는 서버가 직접 LATITUDE/LONGITUDE를 주는 경우 동작할 수 있어 모든 좌표 기능이 무조건 실패한다고 일반화하지 않는다.
- 홈 host와 전체 화면 Activity의 좌표 fallback은 서로 다르다. 전체 화면 문제를 홈에서도 동일하게 재현했다고 보고하지 않는다.

### CALLOUT과 Swagger 스키마 이름 충돌

서버 CALLOUT의 내부 클래스와 CTA 내부 클래스가 모두 `Content`다. 공개 OpenAPI에서는 이 이름이 충돌해 CALLOUT `$ref`가 CTA의 title 기반 구조로 보일 수 있다. `Summary`도 VISIT/REVIEW에서 충돌한다. 따라서 Swagger의 타입 목록만으로 계약을 확정하면 안 된다.

운영 [CALLOUT 클래스][server-callout]·[composer][server-callout-composer] 및 공식인증 실응답에서 image/text/style을 확인했다. Android [mapper][detail-mapper] 135–150행이 CALLOUT과 CTA를 같은 DTO로 처리하고, 300행 이후 title을 필수로 요구하여 실패한다. VISIT summary의 chips/rating 형태는 앱이 이미 양쪽을 수용하므로 그 충돌 자체를 앱 누락으로 보고하지 않는다.

### 액션은 type뿐 아니라 params와 실행까지 확인해야 한다

- 운영 [쿠폰 composer][server-coupon] 133–135행의 `COUPON_ISSUED_KEY`와 [앱 ViewModel][detail-vm] 122행/326행의 `ISSUED_KEY`가 불일치한다.
- [INFO_V2 renderer][info-renderer] 146–148행은 계좌 복사를 `syntheticActionBar(...,"ACCOUNT_COPY")`로 전달한다. ViewModel은 actionBar.type으로 복사를 처리하지 않고 button.link/customAction만 읽는다. 현재 운영 106775 버튼에는 두 값이 없어 로그 처리 뒤 종료된다. 클라이언트의 명시적 계좌 복사 책임으로 처리할지 서버 customAction을 추가할지는 구현 시 계약을 정해야 한다.
- [탭 라우팅][detail-content] 182–187행에는 `post`가 없다. 서버가 POST 없는 가게에도 소식 탭을 보내는 표본이 있었으므로, 앱 anchor 지원과 서버 빈 탭 정책을 분리해 판단해야 한다.

## 4. 타입과 API 수준의 커버리지

현재 서버 V2 종류는 16개다. Android에는 이 중 15개의 이름 분기가 있지만 CALLOUT의 본문을 파싱하지 못해 **실제로 모델을 만드는 종류는 14개이고 EDIT도 부분 지원**이다. 다른 분기에도 위 액션/스타일 한계가 있다. 남아 있는 Android standalone MAP을 포함해 16개라고 세면 잘못된 결론이 된다.

| 종류 | 현재 판단 |
| --- | --- |
| CALLOUT, MARGIN | 미지원. 실응답에서 탈락 재현 |
| EDIT | 부분. 수정/제보 actionBars만 보존, 통합 map 유실 |
| PREVIEW, AD_MOB, TAB, COUPON, VISIT, INFO_V1, INFO_V2, POST, IMAGE, APPEARANCE_DAY, RELATED_STORES, REVIEW, CTA | 대응 DTO/model/mapper/render 있음. 존재 자체가 하위 필드·동작 전체 검증을 의미하지 않으며 위 누락 및 아래 제한이 적용됨 |

| API | 현재 Android 상태 |
| --- | --- |
| `/v2/screen/store/{storeId}` | 사용 중. 위 계약 불일치 존재 |
| `/v1/screen/home` | CATEGORY/RADIO/ACTION 필터 연결. configuration/viewLog·일부 chip 속성 누락 |
| `/v1/screen/home/section/list` | BASIC/EMPTY/ADMOB 및 cursor 연결. focusBounds/일부 로그·style 누락 |
| `/v1/screen/store/{storeId}/contributors` | SCREEN_HEADER/HISTORIES_HEADER/HISTORIES/ACTION 및 현재 카드 내용 표시 연결. 서버 viewLog/일부 선택 필드 누락 |
| `.../contributors/section/histories` | cursor paging 호출·append 구현. 실제 기기 스크롤 paging은 이번에 검증하지 않음 |
| `/v1/screen/home-cards` | 서버에서 deprecated, 현행 홈 목록 API로 대체. 이 API 미사용을 새 구현 필요 항목으로 세지 않음 |
| `/v1/screen/store/{storeId}/preview` | 현행 홈은 목록 카드로 Preview를 구성하고 펼치면 V2를 사용. 별도 endpoint 미사용 자체를 누락으로 세지 않음 |

### 홈 및 공통 요소의 추가 확인

- 홈 radio `paramKey/paramValue` 전달은 일반화돼 있어 신규 정렬·focusLabels 요청 자체는 가능하다. 반환된 bounds를 읽지 않는 것이 이벤트 지도 문제다. `MAP_FOCUS_BOUNDS_SUPPORT`의 AOS 4.23.0 정의만 보고 4.22.1에는 서버가 안 보낸다고 판단할 수 없다. 4.22.1 UA의 실응답에 bounds가 있었다.
- `SDText`의 top-level fontWeight 누락이 핵심은 아니다. 서버는 HTML span CSS로 weight를 보낸다. 공통 [SDTextRenderer][common-renderer]는 이를 처리하지만 홈 `displayText()` 경로는 제거한다. 운영 영업 종료 chip의 CSS 600이 앱 Normal fallback 400으로 흘러가는 것을 probe와 렌더 코드로 확인했다.
- 홈 필터 chip의 운영 contentSpacing=4.0은 DTO에서 없어지고 이미지 뒤 6dp 고정 간격을 사용한다. 홈 카드 metadata separator는 서버 이미지 대신 로컬 점을 그린다. 현재 값이 같은 스타일과 현재 값부터 다른 스타일을 구분해야 한다.
- 홈은 기존 BaseFragment pageview, 기여자는 기존 Activity pageview가 있다. 모든 페이지 로그가 없다는 뜻이 아니라 서버 추가값이 유실된다는 뜻이다. 기여자 편집 click 역시 로컬 로그가 현재 서버 click 내용을 보완하므로 완전 미전송으로 분류하지 않았다.

상세에서도 다음 필드는 mapper가 보존해도 renderer가 사용하지 않는다. 표의 배포 생성값과 렌더 코드 차이를 확인했으며 기기 픽셀 차이를 촬영한 것은 아니다.

| 필드 | 현재 렌더 구현과 배포 생성값 |
| --- | --- |
| APPEARANCE_DAY.items.style, VISIT.history.style | 서버 배경색 #F8F8F8 / #FAFAFA를 섹션 렌더러에서 소비하지 않음 |
| INFO_V2.detailCard.style / menuListCard.style | 모델에 남지만 화면에서 적용하지 않음 |
| INFO_V2.imageGallery.images.style | 서버 288×180, 앱 고정 120×120 |
| INFO_V2.menuListCard.items.image.style | 서버 44×44, 앱 고정 72×72 |
| REVIEW.cards.style / images.style | 본인 리뷰 강조 배경 등을 미적용. 이미지 서버 96×96, 앱 고정 100×100 |
| REVIEW.reply.header.subTitle | 서버가 답글 작성일을 보내지만 화면은 작성자 title과 body만 표시 |

각 필드의 정확한 파일·행 및 16종 전체 중첩 필드 대조는 [상세 감사 부록][detail-annex], 홈·기여자의 현재값/잠재값 구분은 [홈·공통 감사 부록][home-annex]에 있다.

## 5. 잠재 위험과 별도 정책 확인

- **marker nullable:** 서버는 위치 없는 BASIC 카드의 marker를 null로 생성할 수 있다. 앱은 marker가 없으면 카드 전체를 버린다. 원본 Kotlin probe에서 카드 1 → 0 재현. 현재 수집한 일반/BOSS/이벤트 목록에는 null marker가 없었으므로 현재 표본 피해로 보고하지 않는다.
- **기여자 CALLOUT_CARD description nullable:** 합법적인 description=null 카드가 Unknown으로 사라지는 probe 재현. 현재 서버 composer는 description을 항상 제공한다. 상세 CALLOUT 미지원과 별개의 잠재 문제다.
- **chip END:** 공통 model까지 imageAlignment를 보존해도 [SDChipRenderer][common-renderer] 286행 이후는 항상 이미지를 먼저 그린다. 홈 필터/기여자는 일부 속성을 DTO부터 받지 않는다. 이번 운영 표본에서 END chip의 화면 피해를 직접 재현한 것은 아니다.
- **기타 선택 필드:** 기여자 Header.subTitle/trailingAction, Button의 customAction/imageAlignment, card/button 로그 등을 모두 일반적으로 지원하지 않는다. 현재 composer의 null/기본값 덕분에 드러나지 않는 계약 확장 위험을 실제 동작 장애와 구분한다.
- **AD_MOB 여러 카드:** V2 mapper는 목록을 읽지만 UI는 첫 카드만 사용. 현 서버 composer가 1개만 만들므로 현재 운영 장애로 단정하지 않는다. 광고 no-fill도 SDUI 미구현과 별개다.
- **초기 sortType:** 홈 필터 호출보다 위치 조회가 먼저 완료되거나 필터가 실패하면 CATEGORY만 있는 fallback으로 필수 sortType 없는 목록 요청이 가능한 코드 경로가 있다. 네트워크 지연·기기 재현은 하지 않아 별도 재현 대상으로 둔다.
- **impression 정의:** 홈 BASIC/ADMOB는 실제 화면 노출/광고 로드와 무관하게 응답 수신 시 로그를 보낸다. 노출 집계 정책 확인이 필요하며 JSON만으로 합의된 의미를 확정할 수 없다.
- **관련없는 QA와 범위:** 메뉴 접힘, 사진 제보의 바로 앨범 진입, 정보 수정 크래시, 딥링크 지도 복귀 등 기존 UX QA는 서버 계약 대응과 구분한다. 이번에 수정하거나 기기에서 다시 검증하지 않았다.

## 6. 검증 및 다음 작업 기준

실행한 검증:

1. GitHub 릴리스, 운영/개발 배포 workflow와 유저 API job, 배포 커밋 archive, prod→dev diff 조회.
2. 최신 개발 OpenAPI 및 운영 10개·개발 1개 실제 조회 응답 확인.
3. `./gradlew :data:compileDebugKotlin --offline` 성공, 115 tasks up-to-date로 현재 mapper 클래스 검증.
4. 현재 컴파일된 Android mapper에 실제 상세 5개(운영 4, 개발 1)와 소스 기반 fixture 1개를 JVM 재생. 위 섹션 탈락 재현.
5. 원본 Kotlin 소스와 Gson을 임시 경로에 컴파일해 5개 probe 실행 성공(exit 0): nullable marker/description 손실, 필터 chip 속성과 로그 추가값 손실, 기여자 viewLog 손실, HTML weight 처리 차이.
6. 이 보고서의 로컬 참조, 요약 JSON 및 Markdown 공백 검증.

기존 mapper 테스트에는 CALLOUT.content.title와 standalone MAP을 넣은 옛 fixture가 남아 있다. 기존 테스트 통과를 최신 서버 계약 준수의 증거로 재사용하지 않는다. 이번 실행은 **결함 재현 성공**이며 앱 호환성 통과가 아니다.

기기 UI/전체 E2E, 로그인별 쿠폰/소식/좋아요 상태, Firebase 이벤트 수집, 광고 노출, 모든 가게/정책 조합은 실행 검증하지 않았다. 쿠폰 사용 등 외부 상태를 바꾸는 요청은 호출하지 않았다.

구현 시 우선순위는 (1) EDIT+좌표+새 액션 및 CALLOUT/MARGIN, (2) 쿠폰 키·계좌 복사·소식 탭, (3) 홈 bounds/configuration, (4) 실제 서버 계약 fixture 및 스타일/로그 대응이다. USER/BOSS/공식인증과 Home/full-screen host를 분리한 회귀 검증을 완료 기준에 포함해야 한다. 이번 요청은 확인이므로 구현은 별도 작업 범위다.

[release]: https://github.com/pocket-three/pocket-backend/releases/tag/v4.76.1
[prod-run]: https://github.com/pocket-three/pocket-backend/actions/runs/34147339864
[dev-run]: https://github.com/pocket-three/pocket-backend/actions/runs/34296079228
[openapi]: https://dev.threedollars.co.kr/api/v3/api-docs
[evidence]: ../../../build/harness/sdui-contract-audit-20260909/evidence-summary.json
[replay]: ../../../build/harness/sdui-contract-audit-20260909/detail-replay-result.jsonl
[detail-annex]: ../../../build/harness/sdui-contract-audit-20260909/detail-audit.md
[home-annex]: ../../../build/harness/sdui-contract-audit-20260909/home-primitives-audit.md
[detail-dto]: ../../../core/network/src/main/java/com/threedollar/network/data/screen/StoreDetailSectionResponses.kt
[detail-mapper]: ../../../data/src/main/java/com/threedollar/data/screen/StoreDetailScreenMapper.kt
[detail-model]: ../../../core/common/src/main/java/com/threedollar/common/serverdriven/model/StoreDetailV2Models.kt
[detail-vm]: ../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2ViewModel.kt
[detail-activity]: ../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2Activity.kt
[detail-content]: ../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2Content.kt
[info-renderer]: ../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2InfoSections.kt
[common-renderer]: ../../../core/ui/src/main/java/com/zion830/threedollars/core/ui/serverdriven/ServerDrivenRenderer.kt
[server-callout]: https://github.com/pocket-three/pocket-backend/blob/3d2691298340fe1a53923cbc1c88b0060dd44c3a/three-app/api-user/src/main/kotlin/com/three/api/user/application/screen/store/store/section/callout/StoreCalloutSection.kt
[server-callout-composer]: https://github.com/pocket-three/pocket-backend/blob/3d2691298340fe1a53923cbc1c88b0060dd44c3a/three-app/api-user/src/main/kotlin/com/three/api/user/application/screen/store/store/section/callout/StoreCalloutSectionComposer.kt
[server-coupon]: https://github.com/pocket-three/pocket-backend/blob/3d2691298340fe1a53923cbc1c88b0060dd44c3a/three-app/api-user/src/main/kotlin/com/three/api/user/application/screen/store/store/section/coupon/StoreCouponSectionComposer.kt
