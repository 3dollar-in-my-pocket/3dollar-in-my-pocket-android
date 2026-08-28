# TH-1226 Store Detail V2 Bottom Sheet Design

- 상태: 사용자 검토 요청
- 작성일: 2026-08-26
- 작업 브랜치: `feature/TH-1226-store-detail-bottomsheet`
- 기준 커밋: `6729399293462178d71e58048787067c7186ef97`
- Jira: https://3dollarinmypocket.atlassian.net/browse/TH-1226

## 1. 결론

TH-1226은 기존 Home과 상세 기능을 대체하는 새 아키텍처를 만드는 작업이 아니다. 현재 Home Preview, sheet 계산, legacy 상세 action, Repository, Dialog, 하위 Activity를 유지하면서 다음 두 container만 V2로 연결한다.

- Home map marker와 Home bottom sheet list card: 현재 Preview를 열고 같은 bottom sheet를 V2 전체 상세로 확장한다.
- 그 외 모든 상세 진입: 같은 V2 상세 content를 full-screen으로 연다.

UI는 Figma 개선 화면을, 데이터 계약은 현재 Swagger를 source of truth로 사용한다. 기존 `StoreDetailActivity`와 `BossStoreDetailActivity`는 삭제하지 않지만 더 이상 상세 진입 목적지나 실패 fallback으로 사용하지 않는다.

## 2. 확정된 Product 결정

1. 단계별 또는 1차 범위가 없다. 아래 성공 기준을 한 번에 완료한다.
2. rollout gate와 Remote Config 분기는 추가하지 않는다.
3. V2 실패 시 legacy Activity로 이동하지 않는다.
4. Home marker는 `Preview -> 같은 sheet의 Expanded` 흐름이다.
5. Home bottom sheet list card는 marker와 같은 Preview/Expanded sheet 흐름으로 연결하고, 별도 Home list 화면, deep link, push, share와 기존 legacy 상세을 열던 다른 caller는 V2 full-screen으로 연결한다.
6. V2 section의 모든 action을 구현한다. action을 후속 작업으로 미루지 않는다.
7. 기존 action의 사용자 동작, Dialog, 하위 Activity와 mutation 의미는 바꾸지 않는다.
8. 오류 UX는 현재 USER/BOSS legacy 상세의 동작을 따른다.
9. 기존 구현에서 재사용 가능한 것은 유지하고, 기능에 필요한 구조만 좁게 변경한다.
10. 의존성 추가·업그레이드, 전체 Home/상세 리팩터링, 범용 SDUI framework 신설은 하지 않는다.

## 3. Source of truth

충돌 시 다음 우선순위를 사용한다.

1. API field, nullability, enum, endpoint: Swagger/OpenAPI
2. 화면 구성, 순서, sticky UI, Preview/Expanded 시각 상태: Figma 개선 화면
3. action 결과, Dialog, 하위 Activity, 오류 메시지: 현재 Android legacy 상세
4. 모듈과 코드 배치: 현재 Android architecture와 주변 패턴
5. iOS: 사용자 동작을 이해하는 참고 자료이며 API model source가 아니다.

### Figma

- 개선된 화면: https://www.figma.com/design/Gw367Wy4qqnEWcvSlNUqzB/%EA%B0%80%EC%8A%B4%EC%86%8D-3%EC%B2%9C%EC%9B%90-ver4.0.0?node-id=10582-31339&m=dev
- 개선 화면 root: `10582:31339`
- 제보된 가게 TO-BE: `10582:31340`
- 사장님 직영 가게 TO-BE: `10582:31668`
- Preview 상태 참고: `10582:31201`
- Expanded 진입 상태 참고: `10582:31249`

`10582:31339`의 AS-IS frame은 구현 대상이 아니다. TO-BE 두 frame과 scroll case만 화면 요구사항으로 사용한다.

Figma에서 확인한 핵심 변경은 다음과 같다.

- Preview와 상세 상단 정보를 같은 UI로 유지한다.
- 상세 상단에 저장 action과 navigation chrome을 제공한다.
- 기여자 정보를 누를 수 있는 row로 표시한다.
- main tab과 section anchor를 제공한다.
- 지도 아래에 정보 수정/없는 장소 제보를 배치한다.
- 원본 action row가 사라지는 scroll 구간에는 하단 action을 표시한다.
- 제보된 가게와 사장님 직영 가게는 서버가 내려주는 서로 다른 section 조합을 같은 renderer로 표시한다.

### API

- Swagger UI: https://dev.threedollars.co.kr/api/swagger-ui/swagger-ui/index.html#/%5BUI%5D%20%EA%B0%80%EA%B2%8C%20%EC%83%81%EC%84%B8%20%ED%8E%98%EC%9D%B4%EC%A7%80/getStoreScreenV2
- OpenAPI JSON: https://dev.threedollars.co.kr/api/v3/api-docs
- Notion guide: https://app.notion.com/p/API-3a67ad52990e805b84c5d55120a9b45a

2026-08-26 공개 Swagger에서 다시 확인한 계약:

```http
GET /v2/screen/store/{storeId}
storeId: int64
Experiment-Context: optional header
X-Device-Latitude: optional header
X-Device-Longitude: optional header
```

Android Retrofit gateway path는 다음을 사용한다.

```http
GET /api/v2/screen/store/{storeId}
```

인증 없는 status 확인에서 `/api/v2/...`는 `403`, `/v2/...`는 `404`였다. 위치가 없으면 `0.0`을 만들지 않고 header를 생략한다.

Top-level `StoreScreen`은 non-null `sections`와 `viewLog`를 가진다. 서버 배열 순서를 그대로 렌더링하고, 서버가 제외한 section을 client가 임의로 삽입하지 않는다.

지원 section type:

| Type | Figma/기능 역할 | 구현 원칙 |
| --- | --- | --- |
| `CALLOUT` | 공식 인증 상단 안내 | Swagger의 현재 content shape만 지원 |
| `PREVIEW` | 가게명, metadata, action, 이미지, 기여자 | 현재 Home Preview UI를 확장해 재사용 |
| `AD_MOB` | 상세 광고 | 기존 AdView 패턴 재사용 |
| `TAB` | section anchor | 현재 응답에서 첫 matching section으로 scroll |
| `MAP` | 지도, 주소, 확대 | 기존 Naver map 동작 재사용 |
| `EDIT` | 정보 수정, 없는 장소 제보 | 현재 수정/신고 흐름 재사용 |
| `COUPON` | 발급, 사용, 쿠폰함 | mutation 후 V2 refresh |
| `VISIT` | 방문 요약과 이력 | Figma TO-BE 그대로 표시 |
| `POST` | 사장님 소식과 좋아요 | 기존 상세 동작 유지, 성공 후 refresh |
| `IMAGE` | 가게 사진, 제보, 확대 | 현재 사진 Activity/Dialog 재사용 |
| `APPEARANCE_DAY` | 영업일/출점 위치 | 서버 순서 유지 |
| `RELATED_STORES` | 주변 가게 | 선택 시 V2 full-screen 상세 |
| `CTA` | 서버 link CTA | 기존 link 처리 재사용 |
| `REVIEW` | 요약, 목록, 작성/신고/삭제/좋아요 | 현재 리뷰 흐름 재사용 |
| `INFO_V1` | 제보 가게 정보와 메뉴 | Swagger 다형 row를 따른다. |
| `INFO_V2` | 사장님 가게 정보와 메뉴 | link, 계좌, 메뉴 동작을 유지한다. |

Unknown section은 해당 item만 건너뛰고 debug log를 남긴다. 알려진 section의 필수 field가 깨진 경우에도 가능한 한 해당 section만 제외하고 다른 section은 표시한다. Top-level screen decode가 불가능할 때만 전체 load 실패로 처리한다.

## 4. 현재 Android baseline과 재사용 범위

### 그대로 유지할 것

- `HomeFragment`의 map, marker 선택, current location과 lifecycle
- `HomeBottomSheetContent`의 list/Preview UI, sheet height/offset 계산과 animation
- `HomeViewModel`의 Home list, 선택 card, fallback Preview와 favorite override
- `HomeStorePreviewRoute`의 현재 link/store ID 해석 중 재사용 가능한 부분
- `HomeRepository`, `ScreenRepository`, `ServerApi`의 현재 호출 흐름
- legacy 상세에서 사용하는 Dialog, 하위 Activity, share/direction/map/photo/review flow
- 기존 analytics logger와 SDUI foundation model
- 기존 USER/BOSS 상세 Activity 파일 자체

### 좁게 변경할 것

- `HomeBottomSheetContent`: Preview와 Expanded 상태, 상세 nested scroll 추가
- `HomeViewModel`: 선택 store ID/type 보존과 V2 ViewModel 연결에 필요한 최소 state 정리
- `HomeFragment`: Preview 확장, platform action, child result refresh 연결
- `ScreenRepository`: V2 screen 요청과 mapper surface 추가
- legacy 상세을 직접 여는 caller: V2 full-screen intent로 변경
- 기존 공통 button/log model: V2에 실제로 필요한 nullable field만 추가

### 새로 추가할 것

- V2 screen response와 section response
- V2 display model과 mapper
- 공통 `StoreDetailV2ViewModel`
- 공통 `StoreDetailV2Content`
- Figma section composable
- V2 full-screen Activity
- V2 action을 기존 기능으로 연결하는 feature-local handler

## 5. 목표 사용자 흐름

### Home marker

```text
marker 선택
  -> 현재 card 기반 Preview를 즉시 표시
  -> 같은 store ID로 V2 API를 background load
  -> Preview tap 또는 upward drag
  -> 같은 Home sheet를 Expanded anchor로 이동
  -> V2 content가 준비됐으면 전체 상세 표시
  -> 준비 중이면 기존 Preview를 유지하고 loading만 표시
```

- store를 바꾸면 이전 V2 load job을 취소하고 새 store를 요청한다.
- 늦게 도착한 이전 store 응답은 현재 선택 화면에 반영하지 않는다. 별도 generation framework 대신 coroutine job 취소와 현재 store ID 비교를 사용한다.
- Expanded에서 상세 list가 top일 때 downward drag하면 Preview로 돌아간다.
- back은 `Expanded -> Preview -> 닫기/기존 list 상태 복원` 순서다.
- Preview 내부 action tap은 sheet drag나 Preview 전체 tap보다 우선한다.

Home bottom sheet list card 선택도 동일한 store의 marker 선택과 같은 Preview를 열며, 첫 tap에서 full-screen Activity를 시작하지 않는다.

### Full-screen

다음 진입은 모두 V2 full-screen을 사용한다.

- deep link와 Kakao link
- push
- share link 재진입
- My page, favorite, review/visit history 등 현재 legacy 상세을 직접 여는 모든 caller
- related store

Full-screen은 `StoreDetailV2Content`를 그대로 사용하고 top navigation chrome만 별도로 제공한다. 기존 `startCertification`과 `openReviewWrite` 같은 진입 의도는 간단한 Intent extra로 유지하며 content load 후 기존 action을 실행한다.

복잡한 route model, origin enum, route resolver, intent factory 계층은 만들지 않는다. `StoreDetailV2Activity.getIntent()`와 현재 deep-link parser에서 필요한 ID/type/initial action만 전달한다.

## 6. 목표 architecture

```text
ServerApi
  GET /api/v2/screen/store/{storeId}
        |
        v
StoreDetailScreenResponse
  sections: List<JsonObject>
        |
        v
StoreDetailScreenMapper
  type별 독립 decode, unknown skip
        |
        v
StoreDetailScreenModel
        |
        v
StoreDetailV2ViewModel
  load / refresh / mutation / platform event
        |
        +----------------------------+
        |                            |
        v                            v
Home 기존 sheet Expanded     StoreDetailV2Activity
        |                            |
        +-------------+--------------+
                      v
            StoreDetailV2Content
            direct section `when`
```

### Data layer

- V2 endpoint는 standard Gson을 사용하는 기존 `ServerApi`에 추가한다.
- V1 `StoreApi`와 `SDUIGson`은 수정하지 않는다.
- `ScreenRemoteDataSource`와 `ScreenRepository`에 V2 method를 추가한다.
- top-level response는 section을 `List<JsonObject>`로 받아 mapper가 type별로 decode한다.
- 기존 server-driven text/image/button/log model을 재사용하고, V2에만 필요한 section model만 추가한다.
- DTO/domain/UI model을 기계적으로 3벌 만들지 않는다. network response와 UI에서 사용하는 display model 사이 mapper 한 경계만 둔다.
- `Long`, `Double`, `Boolean`, `String` extra parameter 값은 타입을 보존한다.

### Presentation layer

- `HomeViewModel`은 Home/marker/Preview 선택만 소유한다.
- `StoreDetailV2ViewModel`은 V2 상세 상태와 mutation을 소유한다.
- Home과 full-screen은 같은 ViewModel class의 각 host instance를 사용한다.
- ViewModel 상태는 `Loading`, `Content`, `Error` 정도로 유지한다. 별도 reducer/policy/sequence class를 만들지 않는다.
- mutation 성공 시 현재 content를 유지한 채 V2 screen을 refresh한다.
- mutation 실패 시 현재 content와 scroll position을 유지한다.
- 외부 Activity/Dialog/Intent가 필요한 action만 작은 platform event로 host에 전달한다.

### Renderer

- `LazyColumn`에서 server section 순서대로 직접 `when (section)` 렌더링한다.
- 별도 renderer registry와 render plan layer를 만들지 않는다.
- 16개 type마다 파일을 하나씩 만들지 않는다. Figma 기능 단위로 묶는다.
  - Preview/Callout/Tab
  - Map/Edit/Visit
  - Info V1/V2/Appearance
  - Coupon/Post
  - Image/Related/Review/CTA/Ad
- 기존 디자인 시스템 token과 icon을 먼저 재사용한다.
- Figma asset이 실제로 새로 필요한 경우에만 exact asset을 추가한다.

### Map과 Ad

- MAP은 하나의 Compose host 안에서 기존 Naver Map 설정과 action을 재사용한다.
- 별도의 lifecycle controller/policy hierarchy를 만들지 않고 `DisposableEffect`와 host lifecycle observer 한곳에서 SDK lifecycle을 연결한다.
- AD_MOB은 현재 Home/legacy AdView 패턴을 재사용하고 dispose에서 정리한다.
- Map/Ad 실패는 해당 section만 비우고 나머지 상세을 유지한다.

## 7. Sheet state와 interaction

현재 list sheet의 `Collapsed <-> FullList` 동작은 변경하지 않는다. store가 선택됐을 때만 기존 Preview 상태에 Expanded를 추가한다.

```text
List: Collapsed <-> FullList

Selected store: Preview <-> Expanded
```

- Preview offset 계산은 현재 `previewTargetOffset()`을 유지한다.
- Expanded offset은 system bar를 침범하지 않는 최상단 anchor다.
- Preview/Expanded 전환만 Figma의 `300ms` animation을 적용한다.
- list sheet의 현재 `220ms` animation을 전역 변경하지 않는다.
- Expanded content scroll이 top이 아니면 content가 drag를 소비한다.
- content가 top이고 아래로 drag하면 sheet가 Preview로 이동한다.
- horizontal photo/menu carousel, map gesture, button tap은 sheet gesture보다 우선한다.
- TAB은 고정 index가 아니라 현재 response에서 matching section의 실제 list index로 이동한다.
- 원본 상단 action row가 viewport에서 사라졌을 때만 Figma의 하단 sticky action을 표시한다.

## 8. Action 설계

목표는 action을 다시 설계하는 것이 아니라 V2 server action을 현재 Android 동작에 연결하는 것이다.

### 처리 원칙

1. link가 있으면 현재 공통 link 처리로 보낸다.
2. custom action은 feature-local `when` 한곳에서 처리한다.
3. mutation은 기존 Repository method를 우선 사용한다.
4. 기존 method가 없는 POST/coupon/review-delete endpoint만 현재 `ServerApi`/Repository 흐름에 최소 추가한다.
5. 기존 Dialog와 하위 Activity를 그대로 연다.
6. 하위 화면에서 성공 결과가 돌아오면 V2 screen을 refresh한다.
7. 한 사용자 action에서 click log는 한 번만 전송한다.

### Action 연결

| Action | 기존 동작 재사용 |
| --- | --- |
| 저장/저장 취소 | `HomeRepository.putFavorite/deleteFavorite` |
| 방문 인증 | 현재 USER/BOSS 방문 흐름과 child Activity |
| 리뷰 작성/수정 | 현재 review write/edit UI |
| 리뷰 신고/삭제 | 현재 report reason/Dialog와 endpoint |
| 리뷰 좋아요 | 현재 sticker mutation 의미 |
| 공유 | 현재 `shareWithKakao` |
| 길안내 | 현재 `DirectionBottomDialog`/지도 앱 연결 |
| 주소 복사/지도 확대 | 현재 clipboard/full-screen map 동작 |
| 정보 수정/없는 장소 제보 | 현재 edit/delete-report flow |
| 사진 제보/확대/더보기 | 현재 image picker, Dialog, `MoreImageActivity` |
| 쿠폰 발급/사용 | Swagger endpoint 호출 후 refresh |
| 게시글 좋아요 | Swagger sticker endpoint 호출 후 refresh |
| 관련 가게 | V2 full-screen |
| 기여자, CTA, SNS, 전화, 쿠폰함 | 현재 link/Intent 처리 |

현재 Swagger에서 확인한 mutation endpoint:

```text
PUT/DELETE /v2/store/{storeId}/favorite
PUT        /v1/store/{storeId}/review/{reviewId}/stickers
DELETE     /v2/store/review/{reviewId}
POST       /v1/store/{storeId}/review/{reviewId}/report
PUT        /v1/store/{storeId}/news-post/{postId}/stickers
POST       /v1/store/{storeId}/coupon/{couponId}/issue
PUT        /v1/issued-coupon/{issuedKey}/use
```

현재 Swagger action enum을 그대로 사용한다. 관찰되지 않은 legacy alias를 미리 추가하지 않는다.

## 9. 오류 처리

오류 UI를 새로 만들지 않고 현재 legacy 상세의 사용자 동작을 유지한다.

- Home 첫 V2 load 실패: 기존 Preview 유지, 서버 message 또는 기존 connection error Toast
- full-screen 첫 V2 load 실패: 현재 chrome을 유지하고 Toast 표시, back 가능
- action/refresh 실패: 현재 상세 content와 scroll 유지, Toast 또는 기존 Snackbar
- USER `not_exists_store`: Home sheet 닫기 또는 full-screen 종료
- 필수 위치 실패: 기존 위치 오류 Toast 후 container 닫기
- automatic retry, 전용 오류 화면, legacy fallback 없음
- 인증 없는 live preflight가 아니라 실제 로그인 QA에서 load가 실패하면 사용자 지시대로 그 이후 수동 검증을 중단한다.

## 10. Result와 caller compatibility

- V2 full-screen은 기존 caller가 사용하는 `EXTRA_IS_UPDATED`, `EXTRA_IS_FAVORITE` 의미를 유지한다.
- V2 response만으로 legacy `UserStoreModel` 전체를 임의 합성하지 않는다.
- caller가 전체 legacy model을 기대하는 경우 V2 updated 결과에서 자신의 목록/화면을 refresh하도록 최소 변경한다.
- review/visit/photo child result가 성공하면 V2 상세을 refresh하고 full-screen updated 결과를 기록한다.
- 모든 기존 direct legacy 상세 caller를 검색해 V2 intent로 교체한다.
- 완료 검증에서 legacy Activity 내부 factory 정의를 제외한 direct `StoreDetailActivity.getIntent()`/`BossStoreDetailActivity.getIntent()` callsite가 남아 있지 않아야 한다.

## 11. Logging과 lifecycle

- `viewLog`: screen content를 처음 표시할 때 host instance당 한 번 전송
- click log: action 실행 직전 한 번 전송
- section/card impression: 실제 `LazyListState.visibleItemsInfo`에 들어온 stable ID당 한 번 전송
- dedupe는 `remember(storeId)` 또는 ViewModel의 작은 `MutableSet`으로 충분하며 별도 framework를 만들지 않는다.
- refresh는 같은 화면 session의 page view를 다시 보내지 않는다.
- Home에서 다른 store를 선택하면 새 session으로 본다.
- Map/Ad는 host lifecycle에 연결하고 화면 이탈 시 정리한다.

## 12. 예상 파일 경계

정확한 파일명은 구현 전 주변 구조와 충돌 여부를 확인하되 다음 범위를 넘지 않는다.

### 수정 가능 범위

- `core/network`: `ServerApi`, V2 response
- `core/common`: 기존 SDUI foundation의 필요한 field, V2 display model
- `data`: screen datasource/repository와 mapper
- `domain`: `ScreenRepository` method
- `app/ui/home`: `HomeFragment`, `HomeViewModel`, `HomeBottomSheetContent`, sheet calculator/test
- `app/ui/storeDetail/v2`: V2 ViewModel/content/Activity/section/action
- 상세을 직접 여는 기존 caller와 deep-link/push route
- 관련 unit test와 feature 문서

### 금지 범위

- Gradle dependency 또는 toolchain 변경
- 전체 Home/legacy 상세 패키지 이동
- legacy Activity 삭제
- 범용 navigation framework
- 범용 SDUI registry/plugin framework
- 16개 section마다 DTO/domain/UI/test를 기계적으로 한 파일씩 생성
- unrelated formatting/refactor
- 과거 backup branch의 wholesale cherry-pick

새 abstraction은 현재 설계의 실제 두 consumer가 필요할 때만 추가한다. 예상 범위를 넘어 새 module이나 공통 framework가 필요해지면 구현을 멈추고 사용자 승인을 받는다.

## 13. 테스트 전략

자동 테스트는 사용자 동작 계약을 보호하는 최소 세트로 구성한다.

### API/mapper

- endpoint path, int64 ID, optional header
- 제보 가게와 사장님 가게의 작은 Swagger-shaped JSON fixture
- 16개 type discriminator와 배열 순서
- unknown section skip
- 필수 top-level decode 실패
- nullable/dynamic extra parameter 타입 보존

거대한 한 파일 fixture/test를 만들지 않고 section family별 작은 fixture를 공유한다.

### ViewModel/action

- load success/failure와 legacy Toast event
- store 변경 시 이전 request 무시
- mutation 성공 refresh, 실패 content 유지
- child result refresh
- 주요 action이 기존 destination/Repository call로 연결되는지 확인

### Home sheet

- Preview/Expanded anchor와 back 순서
- upward expand, content-top downward collapse
- nested scroll과 horizontal/map/button gesture 우선순위의 pure 부분
- 기존 list `Collapsed/FullList` 회귀

### Route/caller

- marker는 Home sheet
- list/deep link/push/share/related/기존 caller는 V2 full-screen
- initial visit/review action 전달
- result compatibility
- direct legacy detail caller 정적 guard

### 실행 범위

```bash
./gradlew :core:network:testDebugUnitTest --tests '*StoreDetail*'
./gradlew :data:testDebugUnitTest --tests '*StoreDetail*'
./gradlew :app:testDebugUnitTest --tests '*StoreDetail*' --tests 'com.zion830.threedollars.ui.home.*'
./gradlew :app:assembleDebug
```

사용자 지시에 따라 에이전트는 emulator/실기기/browser 수동 UI, full-screen screenshot, screenshot comparator/visual diff, Figma·iOS screenshot parity, authenticated live preflight를 실행하지 않는다. 사용자가 직접 확인할 checklist만 최종 문서에 남긴다.

## 14. 구현 순서

아래는 배포 단계가 아니라 하나의 완료 범위 안에서의 의존 순서다.

1. V2 endpoint, response, display model, mapper
2. 공통 V2 content와 Figma section UI
3. 모든 action과 refresh/result 처리
4. 기존 Home sheet에 Preview/Expanded 연결
5. full-screen host와 모든 caller 전환
6. logging/lifecycle 보강
7. 관련 자동 테스트와 assemble
8. 구현/검증 문서 갱신

커밋은 위 작업을 4~6개의 논리적 단위로 묶는다. 실패 테스트 하나마다 micro commit을 만들지 않는다.

## 15. 완료 기준

- Figma `10582:31339`의 제보 가게/사장님 직영 TO-BE가 V2 response에 따라 표시된다.
- Home marker에서 기존 Preview가 열리고 같은 sheet로 Expanded 된다.
- Home marker와 Home bottom sheet list card에서 기존 Preview가 열리고 같은 sheet로 Expanded 된다.
- deep link/push/share/related와 모든 기존 상세 caller가 V2 full-screen을 연다.
- 16개 section을 server order로 처리하고 unknown section은 다른 content를 깨지 않는다.
- 현재 상세에서 가능했던 모든 action이 동일한 사용자 결과를 낸다.
- action 성공 후 V2 content와 caller 결과가 갱신된다.
- 오류 동작이 legacy 정책과 일치하고 legacy fallback이 없다.
- 기존 Home list sheet 동작과 Preview action이 회귀하지 않는다.
- 관련 unit test와 `:app:assembleDebug`가 통과한다.
- dependency/toolchain 변경과 unrelated refactor가 없다.
- 보호 대상 사용자 변경이 commit에 포함되지 않는다.

## 16. 문서 운영

현재 단계에서는 이 설계 문서와 다음 세션 프롬프트만 유지한다. 사용자가 이 문서를 승인한 뒤 다음 세션에서 하나의 간결한 `02-plan.md`를 작성하고 구현한다. 실제 변경 중에는 `03-implementation.md`, 완료 시에는 `04-verification.md`만 추가한다.

과거 과구현은 로컬 `backup/TH-1226-before-replan-20260826`에 복구용으로만 남아 있다. 다음 구현은 해당 branch를 cherry-pick하거나 구조를 복사하지 않고, 필요한 기존 behavior를 확인하는 제한된 `git show` 용도로만 사용할 수 있다.
