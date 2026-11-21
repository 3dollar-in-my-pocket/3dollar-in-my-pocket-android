# DESIGN.md — 가슴속 3천원 Android 디자인 시스템

AI 에이전트와 개발자가 UI 코드를 작성할 때 읽는 문서입니다. 시각 원본은 피그마이고, 이 문서는 그 내용을 Android 코드 이름과 함께 옮긴 사본입니다. iOS 레포의 `DESIGN.md`와 같은 형식입니다.

- 피그마: [Design System](https://www.figma.com/design/DU3txoMhj2wCjW9xkkyyMP) — `Foundation`(ㄴ Color · ㄴ Typography · ㄴ Icons · ㄴ Illustrations), `Component`(ㄴ 컴포넌트별 16개), `Module`(ㄴ 기능별 9개) 페이지
- 코드: `core/designsystem` (`res/values/colors.xml`, `res/font`, `res/drawable`, `java/base/compose/Color.kt` · `Font.kt`)
- 피그마와 이 문서가 다르면 피그마가 맞습니다. 이 문서를 고치고, 코드도 피그마에 맞춥니다.

## 규칙

1. 색은 XML `@color/<토큰>`, Compose `base.compose.<토큰>`만 씁니다. 새 hex를 화면 코드에 쓰지 않습니다. 필요한 색이 없으면 토큰을 먼저 추가하자고 제안합니다.
2. 이름이 같아도 값이 다를 수 있으니 시안의 hex와 토큰 값을 대조합니다. 아래 표가 피그마와 맞춘 값입니다.
3. 텍스트는 아래 [타이포그래피](#타이포그래피) 표에 있는 조합만 씁니다. 표에 없는 크기·굵기가 시안에 있으면 디자이너에게 확인합니다.
4. 서버가 내려주는 화면 섹션의 색·문구는 서버 값을 그대로 렌더링합니다. 서버 값이 없을 때의 기본값(fallback)만 토큰으로 둡니다.

## 색

피그마 Paint Style 27개 기준입니다. XML 이름은 `colors.xml`, Compose 이름은 `Color.kt`입니다.

| 피그마 | XML | Compose | 값 |
|---|---|---|---|
| main/pink | `pink` | `Pink` | #FF8181 |
| main/red | `red` | `Red` | #FF5C43 |
| main/green | `green` | `Green` | #00C667 |
| system/white | `color_white` | `ColorWhite` | #FFFFFF |
| system/black | — | — | #000000 |
| gray/0 | `gray0` | `Gray0` | #FAFAFA |
| gray/10 | `gray10` | `Gray10` | #F4F4F4 |
| gray/20 | `gray20` | `Gray20` | #E2E2E2 |
| gray/30 | `gray30` | `Gray30` | #D0D0D0 |
| gray/40 | `gray40` | `Gray40` | #B7B7B7 |
| gray/50 | `gray50` | `Gray50` | #969696 |
| gray/60 | `gray60` | `Gray60` | #787878 |
| gray/70 | `gray70` | `Gray70` | #5A5A5A |
| gray/80 | `gray80` | `Gray80` | #2E2E2E |
| gray/90 | `gray90` | `Gray90` | #232323 |
| gray/95 | `gray95` | `Gray95` | #181818 |
| gray/100 | `gray100` | `Gray100` | #0F0F0F |
| pink/100 ~ 500 | `pink_100` ~ `pink_500` | `Pink100` ~ `Pink500` | #FFEFEF · #FFD9D5 · #FFB1AD · #F57575 · #E96060 |
| green/100 ~ 500 | `green_100` ~ `green_500` | `Green100` ~ `Green500` | #F1FFF8 · #99E8C2 · #4DD794 · #00A455 · #00753D |

- `gray40`은 2026-11 TH-1620 전까지 #878787이어서 피그마 gray/40과 달랐습니다. 피그마 값으로 맞추면서 임시 토큰 `color_B7B7B7` · `ColorB7B7B7`은 지웠습니다.
- gray/40 글자는 흰 배경에서 옅습니다. 날짜·태그·placeholder·비선택 칩·비선택 탭·미입력 라벨처럼 보조 글자에만 씁니다. 본문과 신고·삭제 사유 같은 목록형 선택지 글자는 gray/60 이상을 씁니다. 새 화면에서 gray/40 글자를 쓰기 전에 iOS 같은 화면의 색을 확인합니다.
- 피그마에 없는 옛 색(`color_main_red`·`ColorMainRed` #EE624C, `color_black` #1C1C1C, `color_828282` 등)은 정리 대상입니다. 이름에 main_red가 들어가도 피그마 main/red(#FF5C43)와 다르니 새 코드에서는 `red`를 씁니다.

## 타이포그래피

피그마 텍스트 스타일은 Pretendard, 자간 -1%입니다.

| 피그마 스타일 | XML 스타일 | Compose |
|---|---|---|
| 30/bold | `pretendard_bold.size_30dp` | `FontWeight.Bold`, 30 |
| 24/bold | `pretendard_bold.size_24dp` | `FontWeight.Bold`, 24 |
| 20/semi-bold | `pretendard_semibold.size_20dp` | `FontWeight.SemiBold`, 20 |
| 16/bold | `pretendard_bold.size_16dp` | `FontWeight.Bold`, 16 |
| 16/medium | `pretendard_medium.size_16dp` | `FontWeight.Medium`, 16 |
| 14/semi-bold | `pretendard_semibold.size_14dp` | `FontWeight.SemiBold`, 14 |
| 14/regular | `pretendard_regular.size_14dp` | `FontWeight.Normal`, 14 |
| 12/bold | `pretendard_bold.size_12dp` | `FontWeight.Bold`, 12 |
| 12/medium | `pretendard_medium.size_12dp` | `FontWeight.Medium`, 12 |
| 10/medium | `pretendard_medium.size_10dp` | `FontWeight.Medium`, 10 |

Pretendard Regular · Medium · SemiBold · Bold 네 굵기가 `res/font`와 `PretendardFontFamily`에 모두 등록돼 있습니다. 20 크기는 피그마에 semi-bold만 있으니 XML에서 `pretendard_bold.size_20dp` 대신 `pretendard_semibold.size_20dp`를 씁니다. TH-1620 전에는 Regular·SemiBold가 없어서 Regular(400)는 Medium으로, SemiBold(600)는 Bold로 그려졌습니다. 그래서 굵기를 고를 때는 코드에 적힌 값이 아니라 피그마·iOS 값을 따릅니다.

## 아이콘

- 원본은 피그마 `ㄴ Icons` 페이지의 `Icon` 프레임이고, 이름은 소문자 `snake_case`, 형태 접미어는 `_line` · `_solid`입니다. 방향은 `chevron_*`(꺾쇠) · `arrow_*`(화살표), 닫기는 `close`입니다.
- Android drawable은 아직 옛 이름(`ic_arrow_left`, `ic_close_white`, 크기·색이 붙은 `ic_arrow_down_16_black` 등)을 씁니다. 피그마 이름으로 맞추는 작업은 후속입니다.
- 하단 탭 아이콘은 tint가 아니라 벡터 `fillColor`에 색이 들어 있습니다(`ic_*` 선택, `ic_*_off` 비선택). 그래서 `itemIconTintList = null`이 필요합니다.

## 컴포넌트

피그마 `Component` 하위 페이지 16개와 현재 Android 구현 위치입니다. 공용 컴포넌트가 없는 것은 화면마다 직접 그리고 있습니다.

| 피그마 | 피그마 규격 | Android 현재 위치 |
|---|---|---|
| Toast | 배경 gray/95, radius 10, 글자 14 흰색 | `custom_toast_black.xml` (`showCustomBlackToast`) |
| BottomCTA (Full) | 높이 64, 배경 main/pink(비활성 gray/30), 글자 bold 16 흰색 | `AddStoreFlowScreen` · `EditStoreComponents` · `CompletionScreen` (화면마다 직접) |
| Bottom (하단 탭) | 높이 60, 아이콘 24 + 라벨 12 medium, 선택 main/red · 미선택 gray/40, My 탭만 배경 gray/100 · 구분선 gray/80 | `activity_home.xml` `nav_view` + `MainActivity` |
| SearchField | 높이 50, radius 12 | `fragment_search_by_address.xml` |
| Tabs | Width 속성: Fill = 같은 폭, Hug = 글자 길이에 맞춤(넘치면 가로 스크롤). 좌우 여백 16, 탭 안쪽 여백 12, 선택 밑줄 2 | Fill: `MyCouponsScreen` `CouponTabBar` · `fragment_my_review.xml` · `activity_poll_list.xml` · `fragment_community.xml` / Hug: 가게 상세 탭 · 가게 제보 메뉴 카테고리 탭 |
| Button | Medium 48 · Small 34, radius 12 | 공용 컴포넌트 없음 |
| Chip · Badge · Rating · SelectRow · TextField · HandleBar · ProgressBar · Header · Top | 피그마 각 페이지 참고 | 공용 컴포넌트 없음 |

- 홈 인디케이터(Home bar)와 상태바는 시스템 요소라 디자인 시스템에 넣지 않습니다.
- iOS는 하단 탭에 시스템 탭바를 쓰고 색·아이콘만 맞춥니다. Android는 피그마 Bottom을 그대로 그립니다.

## 모듈

서버가 섹션 단위로 내려주는 화면 조각입니다. 피그마 `Module` 하위 페이지에 기능별로 나뉘어 있고, 이름은 대부분 서버 타입 키입니다.

- 가게 상세·미리보기 섹션: `core/ui/.../sdui/`
- 홈 필터·지도 컨트롤: `core/common/.../serverdriven` 모델 → `data/.../screen` 매퍼 → `app/.../ui/home`
- 우리 동네 소식: `app/.../ui/feed` (`FeedCard`)
- `FEED_CARD`는 피그마에서 붙인 이름입니다(서버는 본문 형태 `CONTENT_ONLY` 등으로 구분). 가게 기여자 카드 `ICON_TEXT_CARD` · `CALLOUT_CARD`는 서버 타입 키이고, Android는 타입 키 대신 필드 유무로 판단해 `SDCardModel.HistoryCard`로 그립니다(`ScreenMapper`).

| 피그마 페이지 | 모듈 |
|---|---|
| 가게 정보 | PREVIEW · INFO_V1 · INFO_V2 · APPEARANCE_DAY · EDIT · IMAGE |
| 리뷰·방문 | REVIEW · VISIT |
| 쿠폰 | COUPON |
| 소식 | POST · FEED_CARD |
| 목록·카드 | BASIC_CARD · RELATED_STORES · EMPTY_CARD · ICON_TEXT_CARD · CALLOUT_CARD |
| 지도·필터 | HOME_FILTER · HOME_MAP_CONTROL |
| 안내·유도 | CTA · CALLOUT |
| 광고 | AD_MOB |
| 화면 구성 | TAB |

- THREADS_POST · AD · AD_CARD · IMAGE_PREVIEW_CARD · HOME_BOTTOM_SHEET_TAB · CAROUSEL은 앱과 서버에는 있지만 피그마에 아직 없습니다.
