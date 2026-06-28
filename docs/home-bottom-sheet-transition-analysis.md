# 홈 탐색 개선 바텀시트 전환 분석

## 분석 기준

- Figma: `NEiQWZyejXjMKjoQhMIw0S`, node `244:2153`
  - 주요 섹션: `✅ 홈화면 개선`, `리스트 컴포넌트`, `상세 컴포넌트`
  - 세부 확인 노드: `244:6779` 바텀시트, `244:6900` 리스트 카드, `244:6941` 상세 프리뷰, `244:7040` 상세 이미지 variants
- API 문서: `/Users/jeongjin-yong/Downloads/[#TH-1150] [유저앱_서버] 홈 탐색 개선_ 바텀 시트 전환.pdf`
- 현재 코드 확인 범위:
  - `app/src/main/java/com/zion830/threedollars/ui/home`
  - `app/src/main/java/com/zion830/threedollars/ui/map`
  - `core/common/src/main/java/com/threedollar/common/serverdriven`
  - `core/ui/src/main/java/com/zion830/threedollars/core/ui/serverdriven`
  - `core/network/src/main/java/com/threedollar/network/api/ServerApi.kt`
  - `data/src/main/java/com/threedollar/data/home`, `data/src/main/java/com/threedollar/data/screen`
  - `domain/src/main/java/com/threedollar/domain/home`, `domain/src/main/java/com/threedollar/domain/screen`

## 결론

이번 작업은 단순 UI 교체가 아니라 홈 탐색 데이터 소스를 기존 `/api/v4/stores/around` 중심 모델에서 서버 드리븐 홈 리스트 섹션과 가게 프리뷰 바텀시트로 확장하는 작업이다.

기존 홈 화면은 `HomeFragment`에서 지도 위에 주소/필터/가로 카드 RecyclerView를 띄우고, `HomeListViewFragment`는 별도 전체 리스트 화면으로 동작한다. Figma와 PDF 기준 To-Be는 리스트 탐색과 상세 핀 클릭 결과를 지도 위 바텀시트로 전환한다.

이미 공통 SDUI 모델과 Compose 렌더러가 일부 존재하지만, 현재 모델은 `StoreContributor` 화면 중심이라 홈 리스트 카드, 지도 마커, 이미지 리스트, `SDButton.imageAlignment`, `SDButton.customAction`, `SDPageViewLog.extraParameters`, impression log를 표현하기 부족하다.

## Figma 요구사항

### 홈 지도

- 기존 지도, 주소 선택, 필터 칩은 유지한다.
- 기존 "리스트뷰" 진입 버튼과 별도 리스트 화면 흐름은 바텀시트 중심으로 재검토해야 한다.
- Figma상 바텀시트는 지도 하단에서 올라오며:
  - 상단 radius: `16dp`
  - shadow: `0 -4 10 rgba(0,0,0,0.12)` 계열
  - handle 높이: `12dp`, handle bar `40x5`, 색상 `#E2E2E2`
  - 기본 노출 높이 메모: `348px`
- 현재 위치 버튼과 피드 버튼은 바텀시트 위 지도 영역에 유지된다.

### 리스트 카드

Figma `List` 컴포넌트 기준:

- 전체 카드:
  - width `375`, padding horizontal `20`, vertical `16`
  - 배경 `#FFFFFF`, 하단 border `#F4F4F4`
  - 내부 gap `8`
- 제목:
  - `16/bold`, line height `24`, color `#0F0F0F`
  - 신규 가게는 `NEW` badge `14x14` 노출
- 1차 metadata:
  - 카테고리 텍스트
  - 별점과 리뷰 수: 예 `4.6 (23)`
  - text color `#787878`, `14/regular`
- 2차 metadata:
  - 영업 중, 거리, 최근 방문 수
  - 영업 중은 `#232323`, `14/semi-bold`
  - 거리/최근 방문은 `#787878`, `14/regular`
  - 구분 dot은 `2x2`, `#B7B7B7`
- 이미지:
  - 리스트 카드에서 3개 이상은 `120x120`, radius `8`, gap `6`
  - 1개/2개 이미지는 Figma 예외 케이스 기준 height fill 처리
- 대표 리뷰:
  - 배경 `#F4F4F4`, radius `12`, height `58`, padding `12`
  - text color `#5A5A5A`, `12/medium`, 2줄 ellipsis 성격
- 예외 케이스:
  - 이미지 있음/없음
  - 리뷰 있음/없음
  - 이미지 개수 1/2/3+
  - 카테고리 항목이 많을 때
  - 리뷰 또는 최근 방문이 없을 때 표시 정책은 Figma 메모상 결정 필요

### 상세 프리뷰 바텀시트

Figma `item/사진=on` 기준:

- 전체:
  - width `375`, padding horizontal `20`, vertical `16`
  - 배경 `#FFFFFF`
  - section gap `12`, header/action 영역 gap `16`
- 헤더:
  - title `20/semi-bold`, line height `28`, color `#0F0F0F`
  - 신규 badge `16x16`
  - 우측 원형 아이콘 버튼: 저장, 닫기
    - size `32`
    - background `#F4F4F4`
- metadata:
  - primary: 카테고리, 별점, 리뷰 수
  - secondary: 영업 중, 거리, 최근 방문 수
  - 상세에서 영업 중 텍스트는 `#2E2E2E`, `14/semi-bold`
- 액션 버튼:
  - `방문 인증`: pink `#FF858F`, white text, trailing chevron
  - `리뷰 작성`: light pink `#FFECEE`, text `#F9737E`
  - `공유`: border `#E2E2E2`, icon + text
  - `길안내`: border `#E2E2E2`, icon + text
- 이미지 variants:
  - single: height `158`, full width, radius `10`
  - two: height `158`, 2 columns, gap `6`, radius `10`
  - three: `158x158` square items, horizontal overflow 가능
- 리뷰:
  - `300x58` card들이 가로로 이어지는 형태
  - background `#F4F4F4`, radius `12`, padding `12`

### 지도 핀

- To-Be는 영업 중 지도 핀과 상세 핀 클릭 케이스를 포함한다.
- Figma에는 focused/unfocused marker, focused count badge `+4`, group label `1/4 강남역 0번...`가 있다.
- PDF 기준 `marker.focused.text`는 영업 중이면 `영업중`, 영업 종료면 빈 문자열이고, 빈 문자열이면 문구 영역을 미노출해야 한다.
- 현재 앱의 지도 마커는 서버가 내려주는 selected/unselected image URL을 Glide로 bitmap화해 NaverMap marker icon에 적용한다. 신규 API의 `marker.focused`, `marker.unfocused`가 `SDChip` 형태라면 현재 `MarkerModel`과 직접 호환되지 않는다.

## API 분석

### 구현 중 확인된 실제 dev API 차이

- `sortType=DISTANCE`는 dev 서버에서 `StoreAroundSortBy` 변환에 실패한다.
  - 실제 동작 값은 `DISTANCE_ASC`다.
  - 필터 API가 내려준 `paramValue`를 그대로 보존해 `/screen/home/section/list`에 전달해야 한다.
- `SDText`는 위치에 따라 두 형태가 섞여 내려온다.
  - 기본 형태: `{ "text": "...", "isHtml": true, "fontColor": "#..." }`
  - wrapper 형태: `{ "text": { "text": "...", "isHtml": true, "fontColor": "#..." }, "style": {...} }`
  - `cards[].bodies[]`에서 wrapper 형태가 확인되었고, 기존 문자열 전용 DTO는 `Expected a string but was BEGIN_OBJECT`로 200 응답 전체 파싱을 실패시켰다.
- 일부 응답/문서 변형에서 텍스트 키가 `text` 대신 `content`, 링크 키가 `link` 대신 `url`, 위치 키가 `latitude/longitude` 대신 `lat/lng`, card type이 `BASIC_CARD` 대신 `BASIC`으로 올 수 있어 alias 대응이 필요하다.
- 홈 리스트 카드 title이 `#FFFFFF`로 내려오는 케이스가 있다. 현재 홈 카드 배경은 흰색이므로 홈 목록 카드에 한해 흰색 title은 기본 본문색으로 보정해야 실제 제목이 보인다.

### 홈 리스트 섹션

엔드포인트:

```http
GET /v1/screen/home/section/list
```

헤더:

- `X-Device-Latitude`: 필수
- `X-Device-Longitude`: 필수
- `Authorization`: 선택. 전달 시 인증 기반 로직 적용

쿼리 파라미터:

- `mapLatitude`: 필수, 지도 중심 위도
- `mapLongitude`: 필수, 지도 중심 경도
- `distanceM`: 필수, 검색 반경 meter
- `sortType`: 필수. 문서상 `LATEST`, `DISTANCE`
- `targetStores`: 선택. `USER_STORE`, `BOSS_STORE`
- `filterCertifiedStores`: 선택, 기본 `false`
- `filterOpenStatuses`: 선택. `OPEN`, `CLOSED`
- `categoryIds`: 선택
- `filterConditions`: 선택. `RECENT_ACTIVITY`, `NO_RECENT_ACTIVITY`
- `filterMinReviewRating`: 선택
- `cursor`: 선택, 첫 요청 미전달

응답 `HomeListSection`:

- `cards: Array<HomeListCard>`
- `cursor.nextCursor: String?`
- `cursor.hasMore: Boolean`

지원 card type:

- `BASIC_CARD`: 가게 카드
- `EMPTY_CARD`: 검색 결과 없음 카드. 디자인 대기
- `ADMOB_CARD`: 광고 카드. 기존 모델에 `clickLog`, `impressionLog` 추가
- 향후 타입 추가 가능. 미지원 타입은 무시해야 한다.

`BASIC_CARD` 주요 필드:

- `type`: 항상 `BASIC_CARD`
- `cardId`: `S:{storeId}` 형태
- `header.title: SDText?`
- `header.badge: SDImage?`
- `metadata.primary: Array<SDChip>`
- `metadata.secondary: Array<SDChip>`
- `metadata.separator: SDImage`
- `images: Array<SDImage>`
- `bodies: Array<Body>`
- `marker.focused: SDChip`
- `marker.unfocused: SDChip`
- `marker.location.latitude/longitude`
- `marker.link: SDLink?`
- `marker.clickLog: SDClickLog`
- `link: SDLink?`
- `style: SDSurfaceStyle`
- `clickLog: SDClickLog`
- `impressionLog: SDImpressionLog`

### 가게 상세 바텀시트

엔드포인트:

```http
GET /v2/screen/store/{storeId}
```

헤더:

- `X-Device-Latitude`: 선택. 거리 계산에 사용
- `X-Device-Longitude`: 선택. 거리 계산에 사용
- `Authorization`: 선택. 북마크 저장 상태 등 인증 기반 로직에 사용

경로:

- `storeId: Long`

응답 `StoreScreen`:

- `sections: Array<StoreSection>`
- `viewLog: SDPageViewLog`

문서 예시는 `PREVIEW` section 중심이다.

`PREVIEW` section 구성:

- `header`
- `metadata.primary`
- `metadata.secondary`
- `metadata.separator`
- `actionBars`
- `images`
- `bodies`
- `style`

### 공통 SDButton 변경

`SDButton` 신규/변경 필드:

- `text: SDText`
- `image: SDImage?`
- `imageAlignment: String?`
  - `START`
  - `END`
  - `null`이면 앱에서 제어
- `link: SDLink?`
- `customAction: SDCustomAction?`
- `style: SDSurfaceStyle`

처리 우선순위:

- `link`와 `customAction`이 모두 있으면 `link` 우선

`SDCustomAction`:

- `actionType: String`
- `extraParams: Object`, 없으면 빈 객체

지원 action type:

- `STORE_PREVIEW_SECTION_FAVORITE`
- `STORE_PREVIEW_SECTION_UNFAVORITE`
- `STORE_PREVIEW_SECTION_SHARE`
- `STORE_PREVIEW_SECTION_NAVIGATION`

`extraParams` 키:

- `STORE_ID: Long`
- `STORE_TYPE: String`
- `LATITUDE: Double`
- `LONGITUDE: Double`
- `STORE_NAME: String`

action type별 필수 키:

- favorite/unfavorite: `STORE_ID`
- share: `STORE_ID`, `STORE_TYPE`
- navigation: `LATITUDE`, `LONGITUDE`, `STORE_NAME`

### 로그

- 홈 리스트 카드에는 `clickLog`, `impressionLog`가 있다.
- 마커에는 `marker.clickLog`가 있다.
- 가게 상세 바텀시트에는 `viewLog`가 있고, `SDPageViewLog.extraParameters`가 추가되었다.
- 현재 공통 `SDClickLogger`는 click log만 처리한다. impression/page view log 모델과 sender가 추가로 필요하다.

## 현재 코드 영향 범위

### 홈 화면

현재 구조:

- `HomeFragment`
  - Naver map fragment를 child fragment로 붙임
  - 주소 박스, 필터 ComposeView, 재검색 버튼, 리스트뷰 버튼, 하단 가로 RecyclerView
  - `HomeViewModel.fetchAroundStores()`로 `/api/v4/stores/around` 호출
  - `AroundStoreMapViewRecyclerAdapter`로 가로 카드 표시
  - marker click과 recycler snap을 서로 동기화
- `HomeListViewFragment`
  - 별도 화면으로 전체 리스트 표시
  - `AroundStoreListViewRecyclerAdapter` 사용
  - 현재 리스트는 기존 `ContentModel` 기반
- `HomeFilterChips`
  - 이미 서버 드리븐 홈 필터 API(`/api/v1/screen/home`)를 사용한다.

변경 방향:

- 홈 map/list 데이터를 기존 `AroundStoreModel`에서 신규 `HomeListSection` 기반으로 전환하거나 병행해야 한다.
- 별도 `HomeListViewFragment`는 유지할지, 바텀시트로 대체할지 결정이 필요하다.
- 현재 horizontal RecyclerView UI는 Figma To-Be와 맞지 않으므로 제거 또는 fallback 용도로 분리해야 한다.
- 바텀시트는 XML + `BottomSheetBehavior` + ComposeView 또는 `HomeFragment` 내 Compose overlay 방식이 현실적이다.

### 지도 마커

현재 구조:

- `ContentModel.markerModel: MarkerModel?`
- `MarkerModel`은 `selected/unSelected: StoreMarkerImageModel`
- `StoreMarkerImageModel`은 `imageUrl`, `width`, `height`
- `NaverMapFragment`는 URL 이미지를 bitmap으로 로드해 `OverlayImage`로 설정

신규 영향:

- 신규 홈 리스트 API의 marker는 `focused/unfocused: SDChip` + location + link + clickLog 구조다.
- SDChip 형태의 marker를 그대로 NaverMap marker icon에 넣을 수 없다.
- 선택지는 두 가지다.
  - 서버 marker를 기존처럼 image URL로 내려주도록 API 계약을 조정한다.
  - 앱에서 SDChip/Compose/View를 bitmap으로 렌더링해 marker icon을 만든다.
- Figma상 focused marker는 텍스트와 count/label이 결합되어 있어 앱 렌더링이 필요할 가능성이 높다.

### API 계층

현재 `ServerApi`:

- `/api/v4/stores/around`
- `/api/v4/store/{storeId}`
- `/api/v4/boss-store/{bossStoreId}`
- `/api/v1/screen/home`
- `/api/v1/screen/store/{storeId}/contributors`

신규 필요:

- `GET /api/v1/screen/home/section/list`
- `GET /api/v2/screen/store/{storeId}`

주의:

- 문서 엔드포인트는 `/v1/...`, `/v2/...`로 표기되어 있지만 현재 코드 Retrofit path는 대부분 `/api/v...` prefix를 포함한다. 실제 base URL 정책에 맞춰 `/api` prefix 포함 여부를 확인해야 한다.
- `sortType`은 PDF에 `DISTANCE`로 표기되어 있으나 현재 앱은 `HomeSortType.DISTANCE_ASC`를 사용한다. 서버가 `DISTANCE_ASC`를 받는지, 신규 API는 `DISTANCE`만 받는지 확인이 필요하다.

### SDUI 모델

현재 SDUI 모델은 두 벌이 있다.

- `core/common/.../serverdriven/model`
  - 앱/도메인에서 쓰는 공통 모델
  - `StoreContributor`와 홈 필터에서 사용 중
- `core/network/.../sdui/model`
  - network 모듈 내부 실험성/별도 SDUI 모델로 보임
  - 현재 공통 흐름과 직접 연결되어 있지 않다.

신규 작업은 `core/common`의 공통 모델을 확장하는 편이 기존 `ScreenRepository`, `ServerDrivenRenderer`, `HomeFilterChips`, `StoreContributorActivity` 흐름과 맞다.

추가가 필요한 모델 후보:

- `HomeListSectionModel`
- `HomeListCardModel`
  - `BasicCard`
  - `EmptyCard`
  - `AdMobCard`
  - `Unknown`
- `HomeListCardHeaderModel`
- `HomeListCardMetadataModel`
- `HomeListMarkerModel`
- `StorePreviewScreenModel` 또는 기존 `SDScreenModel` 확장
- `StorePreviewSectionModel`
- `SDButtonModel.imageAlignment`
- `SDButtonModel.customAction`
- `SDCustomActionModel`
- `SDImpressionLogModel`
- `SDPageViewLogModel.extraParameters`

### 상세 액션 재사용 가능 자산

기존 전체 상세 화면에 이미 유사 액션 구현이 있다.

- 즐겨찾기:
  - `HomeRepository.putFavorite`
  - `HomeRepository.deleteFavorite`
  - `StoreDetailActivity.clickFavoriteButton`
  - `BossStoreDetailActivity.clickFavoriteButton`
- 공유:
  - `shareWithKakao`
  - `ShareFormat`
- 길안내:
  - `DirectionBottomDialog`
  - `FullScreenMapActivity`
- 방문 인증:
  - 현재 홈 카드의 방문 버튼은 `StoreDetailActivity.getIntent(..., startCertification = true)`를 사용한다.

상세 바텀시트 action handler는 위 기능을 직접 호출하거나 공통 handler로 추출할 수 있다.

## 구현 순서 제안

1. API/모델 추가
   - 신규 response DTO 추가
   - 공통 SD model 확장
   - mapper 추가
   - unknown card/section/action type 무시 정책 반영

2. Repository 연결
   - 홈 리스트 섹션은 `HomeViewModel`의 필터/지도 상태와 강하게 결합되어 있다.
   - `HomeRepository`에 둘지, `ScreenRepository`에 둘지 경계를 먼저 정해야 한다.
   - 추천: 화면 SDUI API는 `ScreenRepository`에 모으되, request params builder는 홈 UI 계층에 둔다.

3. 홈 바텀시트 UI 도입
   - `HomeFragment`의 map fragment는 유지
   - 하단 RecyclerView 대신 바텀시트 컨테이너 추가
   - 기본 높이, expanded/collapsed 상태, pagination trigger 정의
   - 기존 `HomeListViewFragment` 진입 플로우 정리

4. 홈 리스트 카드 렌더링
   - `BASIC_CARD`, `ADMOB_CARD`, `EMPTY_CARD`, `Unknown` 처리
   - 이미지/리뷰 예외 케이스 반영
   - click/impression log 처리
   - card `link` 처리

5. 지도 마커 연동
   - 신규 marker 데이터 모델 연결
   - focused/unfocused marker 렌더링 방식 결정
   - marker click 시 `marker.clickLog` 전송
   - `marker.link`로 상세 바텀시트 오픈
   - list scroll과 marker focus 동기화

6. 상세 프리뷰 바텀시트
   - `GET /v2/screen/store/{storeId}` 연동
   - `PREVIEW` section 렌더링
   - favorite/unfavorite/share/navigation customAction 처리
   - 방문 인증 link 처리
   - viewLog 전송
   - 닫기 버튼 동작

7. 신규 딥링크
   - PDF 코멘트에 "가게 바텀시트로 랜딩하는 신규 딥링크 필요"가 있음
   - 현재 `DynamicLinkActivity`는 `/store`, `/reviewList`, `/home` 등만 처리한다.
   - `/storePreview`, `/storeBottomSheet`, `/visit` 등 실제 계약 확정 후 추가 필요

8. 검증
   - mapper unit test
   - query param builder test
   - customAction handler test
   - card type unknown 무시 test
   - Compose screenshot 또는 emulator QA로 Figma spacing 확인

## 확인 필요 항목

- 신규 API path에 `/api` prefix가 붙는지 확인 필요
- 신규 홈 리스트 API의 `sortType` 값이 `DISTANCE`인지 `DISTANCE_ASC`인지 확인 필요
- `filterOpenStatuses`, `filterConditions`, `categoryIds`, `targetStores` 배열 쿼리 인코딩 방식 확인 필요
- `HomeListSection.EMPTY_CARD` 디자인 확정 필요
- 리뷰/최근 방문 값이 없을 때 metadata 미노출인지 placeholder인지 결정 필요
- 상세 바텀시트의 `리뷰 작성` 액션이 API `actionBars`에 포함되는지 확인 필요
- Figma는 상세 우측 상단 저장 아이콘을 별도 아이콘 버튼으로 보여주지만 PDF는 action bar의 `저장` 버튼 예시가 있다. 최종 UX 정합 필요
- `GET /v2/screen/store/{storeId}`가 `USER_STORE`와 `BOSS_STORE`를 같은 `storeId`로 구분할 수 있는지 확인 필요
- marker를 서버 이미지로 받을지, 앱에서 SDChip을 bitmap marker로 렌더링할지 결정 필요
- 바텀시트 snap point:
  - 기본 `348px`
  - expanded 높이
  - collapsed/hidden 가능 여부
  - 지도 제스처와 바텀시트 drag 충돌 처리
- 홈 리스트 pagination이 바텀시트 scroll 끝에서 `cursor.nextCursor`로 이어지는지 확인 필요
- 기존 `HomeListViewFragment`를 제거/미사용 처리할지 fallback으로 유지할지 결정 필요

## 리스크

- SDUI 모델 확장이 `StoreContributor` 기존 화면에 영향을 줄 수 있다. 공통 모델에 nullable 필드를 추가하는 방식으로 호환성을 유지해야 한다.
- 지도 마커 bitmap 생성은 성능 이슈가 날 수 있다. 캐싱 전략이 필요하다.
- 바텀시트와 NaverMap fragment를 같은 화면에서 섞으면 touch/gesture 충돌이 생길 수 있다.
- API가 click/impression/view log를 모두 내려주기 시작하면 기존 Firebase log와 중복 전송 가능성이 있다.
- 신규 API와 기존 `/api/v4/stores/around`를 병행하면 홈 상태 소스가 이중화된다. 최종 전환 기준을 명확히 해야 한다.

## 다음 액션

- API 계약 확인 후 DTO/도메인 모델 설계를 먼저 고정한다.
- 그 다음 `HomeFragment`에 바텀시트 shell을 넣고, mock 모델로 Figma 레이아웃을 맞춘 뒤 API 연결을 붙이는 순서가 안전하다.
- marker 렌더링 방식은 구현 초기에 확정해야 한다. 이 결정이 NaverMap 연동 범위와 성능 검증 범위를 크게 바꾼다.
