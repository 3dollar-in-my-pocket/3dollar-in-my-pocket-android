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
- drawable 이름은 `ic_` + 피그마 이름입니다. 예: 피그마 `chevron_left` = `ic_chevron_left`, 일러스트 `face_gloomy` = `ic_face_gloomy`. iOS는 접두어 없이 `chevron_left`입니다.
- 피그마 이름으로 맞춘 것: `ic_chevron_left`(24dp) · `ic_chevron_right`(30dp) · `ic_location_solid` · `ic_location_line` · `ic_location_current` · `ic_delete`(휴지통) · `ic_information` · `ic_fire_solid` · `ic_bookmark_line` · `ic_bookmark_solid`, 일러스트 `ic_face_gloomy` · `ic_bungeoppang_default` · `ic_face_colored_sad` · `ic_face_colored_smile`. 원래부터 같은 이름: `ic_heart_fill` · `ic_heart_line` · `ic_fire_line` · `ic_mappin_focused_on` · `ic_mappin_focused_off` · `ic_check` · `ic_change` · `ic_map` · `ic_list` · `ic_plus` · `ic_zoom`.
- 아직 옛 이름인 것 (tint·크기로 합치면서 정리):
  - 색·크기가 붙은 변형: `ic_arrow_down_16_black`, `ic_close_24_gray40`, `ic_copy_18`, `ic_refresh_20`, `ic_coupon_solid_16`, `ic_star_solid_pink_12` 등. `ic_arrow_right` · `ic_arrow_down_16*` · `ic_arrow_*_pink_20`은 꺾쇠 모양이라 `chevron_*`으로 갑니다. `ic_delete_16` · `ic_delete_20` · `ic_delete_small`은 휴지통이 아니라 원 안의 X(`delete_x`) 모양입니다. `ic_location_soild_12*`는 철자도 고칩니다.
  - 피그마 이름이 이미 다른 모양에 쓰이는 것: `ic_close`(원 안의 X = `delete_x` 모양, 피그마 `close` 모양은 `ic_store_preview_close`), `ic_star_line`(원 안의 별), `ic_badge`(색 있는 메달 = 일러스트 Medal), `ic_write`(빨간 +), `ic_share`(옛 선 스타일, 피그마 `share` 모양은 `ic_store_preview_share`). 옛 이름을 다른 그림에 다시 쓰면 그 이름을 참조하던 코드가 에러 없이 다른 그림을 띄우므로 따로 정리합니다. 같은 이유로 돋보기 `ic_reading_glasses` → `ic_search`도 후속입니다.
  - 일러스트 `bungeoppang_crying`(ㅠ 말풍선 붕어빵)은 `ic_empty` · `ic_list_view_empty` · `ic_no_store` 세 벌이 조금씩 다릅니다.
  - `:common`과 `:core:designsystem`에 같은 이름이 있는 것(`ic_kakao` · `ic_apple` · `ic_setting` · `ic_no_store` 등)은 `:common`이 먼저 선언돼 `:common` 쪽이 화면에 나옵니다. 중복을 지울 때 어느 그림을 남길지 정합니다.
- 하단 탭 아이콘은 tint가 아니라 벡터 `fillColor`에 색이 들어 있습니다(`ic_*` 선택, `ic_*_off` 비선택). 그래서 `itemIconTintList = null`이 필요합니다.

## 컴포넌트

피그마 `Component` 하위 페이지 16개와 현재 Android 구현 위치입니다. 공용 컴포넌트가 없는 것은 화면마다 직접 그리고 있습니다.

| 피그마 | 피그마 규격 | Android 현재 위치 |
|---|---|---|
| Toast | 배경 gray/95, radius 10, 글자 14 흰색 | `custom_toast_black.xml` (`showCustomBlackToast`) |
| BottomCTA (Full) | 높이 64, 배경 main/pink(비활성 gray/30), 글자 bold 16 흰색 | `AddStoreFlowScreen` · `EditStoreComponents` · `CompletionScreen` (화면마다 직접) |
| Bottom (하단 탭) | 높이 60, 아이콘 24 + 라벨 12 medium, 선택 main/red · 미선택 gray/40, My 탭만 배경 gray/100 · 구분선 gray/80 | `activity_home.xml` `nav_view` + `MainActivity` |
| SearchField | 높이 50, radius 12, 배경 흰색 + 테두리 gray/30 1, 좌우 여백 12 · 간격 10, 왼쪽 아이콘 24 gray/100, 글자 medium 16 gray/100 · 플레이스홀더 gray/40, 지우기 `close` 20(입력값 있을 때만) | `base.compose.AppSearchField` — 주소 검색(`SearchAddressFragment`, XML 안 `ComposeView`) |
| SelectRow | 높이 44, radius 12, 배경 흰색, 좌우 여백 16. Off = 테두리 gray/40 · 글자 regular 14 gray/60, On = 테두리 main/red · 글자 gray/100 · 오른쪽 체크 20 main/red | XML `@style/ThreeDollars.SelectRow`(`RadioButton`용) — 리뷰 신고(`dialog_report_review.xml`) · 가게 삭제 요청(`dialog_delete.xml`) / Compose `base.compose.AppSelectRow` |
| TextField | 제목 semi-bold 14 gray/100(선택) + 박스 높이 44 · radius 8 · gray/10 · 좌우 여백 12, 글자 regular 14 gray/90 · 플레이스홀더 gray/50 · 단위 gray/70, 도움말 medium 12. 포커스 main/pink 테두리, 에러 main/red 테두리 + 빨간 도움말. 제목·박스·도움말 간격 7 | `base.compose.AppTextField` — 가게 제보 가게 이름(`RequiredInfoScreen`) · 메뉴 이름·개수·가격(`MenuEditorComponents`) · 가게 정보 수정 가게 이름(`EditStoreInfoScreen`) |
| Tabs | Width 속성: Fill = 같은 폭, Hug = 글자 길이에 맞춤(넘치면 가로 스크롤). 좌우 여백 16, 탭 안쪽 여백 12, 선택 밑줄 2 | Fill: `MyCouponsScreen` `CouponTabBar` · `fragment_my_review.xml` · `activity_poll_list.xml` · `fragment_community.xml` / Hug: 가게 상세 탭 · 가게 제보 메뉴 카테고리 탭 |
| Button | Style(Primary · Secondary · Destructive · Outline · Dark) × Size(Medium 48 · Small 34) × State(Enabled · Disabled). Medium 모서리 12, Small 모서리 10 | `base.compose.AppButton` (`core/designsystem`, Compose). XML 화면은 아직 직접 그림 |
| Chip | Filled 40·32 / Outline 36 / Filter 34 / Save 36 | `base.compose.AppChip` (Save는 서버가 그림). 아래 [Chip](#chip) |
| Badge | 높이 20, radius 4, 글자 10 medium | `base.compose.AppBadge`. 아래 [Badge](#badge) |
| Rating | 별 12 × 5, 여백 4 → 68×20, radius 4 | `base.compose.AppRating`. 아래 [Rating](#rating) |
| Header | 여백 위 20 · 좌우 20 · 아래 16. 제목 bold 24(행간 32) gray/100 + 간격 4 + 서브타이틀(선택) bold 16(행간 24) gray/40, 세로 가운데. 설명(선택) regular 14 gray/80, 제목 아래 8 | `base.compose.AppHeader`. 일부 글자 색을 바꿀 때는 `annotatedSubtitle`. 쓰는 곳: 가게 제보 단계(가게 정보 · 카테고리 (n/10) · 메뉴 (선택) · 상세 정보 (선택)), 메뉴판 인식 결과, 메뉴 정보 수정 |
| Top | 높이 56. Light = 흰 배경·글자/아이콘 gray/100, Dark = gray/100 배경·흰 글자/아이콘. 뒤로 24(누르는 영역 56), 제목 16/medium 가운데(좌우 72 안쪽), 오른쪽 아이콘 24 간격 8 · 오른쪽 여백 16 | `base.compose.AppTop`(`AppTopTheme`), 오른쪽 아이콘은 `AppTopIcon`(누르는 영역 48). 쓰는 곳: Light = 가게 제보 · 메뉴판 인식 · 가게 정보 수정 · 가게 소식 목록 / Dark = 내 쿠폰함. 아직 직접 그리는 곳: 회원가입, 팀 소개, 가게 기여자, 웹뷰, 마이페이지 제목, XML 화면(투표 목록 등) |
| HandleBar | 영역 높이 20, 손잡이 40×5 · radius 2.5 · gray/20, 위 여백 7 | `base.compose.AppHandleBar`(드래그는 `modifier`로). 쓰는 곳: 홈 리스트·가게 미리보기 시트 |
| ProgressBar | 두께 2, 트랙 gray/30, 채움 main/pink, 단계 수만큼 같은 폭(기본 4) | `base.compose.AppProgressBar(step)`. 쓰는 곳: 가게 제보 상단 |

**`AppButton` 규격** (피그마 `ㄴ Button`)
- `size`: `AppButtonSize.Medium` 높이 48 · 모서리 12 · 좌우 여백 12 · semi-bold 14 · 아이콘 24 / `Small` 높이 34 · 모서리 10 · 좌우 여백 10 · bold 12 · 아이콘 16. 아이콘-글자 간격 4
- `style`: `Primary`(배경 `Pink` · 흰 글자) · `Secondary`(배경 `Gray10` · 글자 `Gray70`) · `Destructive`(배경 `Red` · 흰 글자) · `Outline`(배경 흰색 · 테두리 `Gray40` 1 · 글자 `Gray50` — 밝은 팝업의 닫기/취소, 보조 선택) · `Dark`(배경 `Gray50` · 흰 글자 — 팝업의 회색 취소). 빨간 강조 버튼은 삭제가 아니어도 `Destructive`를 씁니다
- `enabled = false`면 스타일과 관계없이 배경 `Gray30` · 흰 글자(테두리 없음)
- `leadingIcon` · `trailingIcon`(Painter)은 글자색으로 칠합니다. 너비는 Medium은 화면이 정하고(`fillMaxWidth` · `weight`), Small은 글자 길이에 맞춥니다
- 화면 아래 꽉 찬 버튼(높이 64)은 BottomCTA라 `AppButton`을 쓰지 않습니다

- 홈 인디케이터(Home bar)와 상태바는 시스템 요소라 디자인 시스템에 넣지 않습니다.
- iOS는 하단 탭에 시스템 탭바를 쓰고 색·아이콘만 맞춥니다. Android는 피그마 Bottom을 그대로 그립니다.

### Chip

누를 수 있는 알약입니다. 글자는 모두 semi-bold 14, 아이콘-글자 간격 4, 닫기(✕) 14는 글자색과 같은 색입니다. 상태는 `selected`, 닫기는 `onCloseClick`을 넘기면 보입니다.

| 피그마 Style · Size | Compose | 높이 · 좌우 여백 · 모서리 · 아이콘 | Off | On | 쓰는 곳 |
|---|---|---|---|---|---|
| Filled · Medium | `AppChip(style = Filled)` | 40 · 12 · 알약 · 24 | 배경 `Gray10`, 글자 `Gray100` | 배경 `Pink200`, 글자 `Pink` | 가게 제보 음식 종류 선택(`MenuDetailScreen` · `MenuCategoryScreen`의 `CategoryChip`) |
| Filled · Small | `AppChip(style = Filled, size = Small)` | 32 · 8 · 알약 · 24 | 〃 | 〃 | 가게 메뉴 수정 음식 종류(`EditMenuScreen`) |
| Outline · Medium | `AppChip(style = Outline)` | 36 · 12 · 알약 · 20 | 배경 흰색, 테두리 `Gray30`, 글자 `Gray90` | 배경 `Pink100`, 테두리·글자 `Pink500` | 요즘 뜨는 간식 카테고리 칩 — 서버 값으로 그림(아래) |
| Filter · Small | `AppChip(style = Filter)` | 34 · 10 · 10 · 20 | 배경 흰색, 테두리 `Gray30`, 글자 `Gray40` | 〃 | 홈 지도 상단 필터 — 서버 값으로 그림(아래) |
| Save · Small | 없음 | 36 · 12 · 18 · 16 | 테두리 `Gray20`, 글자 `Gray70` | 배경 #FFF8F9(토큰 없음), 테두리 `Pink200`, 글자 `Pink` | 가게 상세·미리보기 저장·공유 — 서버 값으로 그림(아래) |

- `size`는 Filled에만 의미가 있습니다(Outline·Filter는 크기가 하나).
- 서버가 색을 내려주는 칩은 `colors = AppChipColors(...)`에 서버의 배경·테두리(색·두께)·글자색을 그대로 넣습니다. 테두리가 없으면 `border = null`.
- 서버 값으로 그리는 칩 3곳은 `AppChip`으로 바꾸지 않고, 그리는 곳에서 앱이 정하는 값만 피그마 규격에 맞춥니다: 요즘 뜨는 간식 카테고리(`SDHomeCurationCategoryChip`, Outline) · 홈 지도 상단 필터(`HomeFilterChips`, Filter: 아이콘-글자 간격 4) · 가게 상세·미리보기 액션 바(`SDActionBarRow`, Save: 좌우 여백 12).
- 이 칩들의 글자 크기·굵기·색은 서버가 글자 서식(`<span style="font-size:…; font-weight:…">`)으로 정합니다. `SDText`가 서식을 그대로 따르고, 서식이 없는 글자일 때만 semi-bold 14 / 줄 높이 20으로 그립니다. 아이콘 크기도 서버 값입니다.
- 2026-11 기준 서버는 필터·카테고리 칩에 12px/500, 액션 바에 14px/600(방문 인증·리뷰 작성)·14px/400(공유·길안내)을 내려줍니다. 피그마(semi-bold 14)와 맞추려면 서버 값을 바꿔야 합니다.
- `:core:ui`의 `SDChip`(서버 이미지 + 글자 한 줄, 배경 없음)과 `SDRatingChip`(서버가 별 이미지를 개수만큼 내려줌)은 서버가 내용과 모양을 정하는 요소라 `AppChip`·`AppRating`과 별개로 둡니다.

### Badge

누를 수 없는 정보 표시입니다. 높이 20, 모서리 4, 좌우 여백 4, 글자 medium 10(줄 높이 14). 앞 아이콘(16, 간격 2)은 `leadingIcon`.

| 피그마 Style | Compose | 배경 · 글자 | 쓰는 곳 |
|---|---|---|---|
| Pink | `AppBadge(style = Pink)` | `Pink100` · `Pink` | 마이페이지 칭호(`backgroundColor = Gray80`) |
| Gray | `AppBadge(style = Gray)` | `Gray10` · `Gray80` | 투표 댓글의 "작성자"·고른 선택지 (XML 화면이라 아직 직접 그림) |

- 어두운 배경처럼 배경만 바꿀 때는 `backgroundColor`를 넘깁니다. 글자색은 Style을 따릅니다.
- 칭호를 그리는 투표 목록·상세·댓글, 가게 리뷰 목록, 찜 목록 보기는 XML 화면이라 아직 직접 그립니다(후속).

### Rating

별점 표시입니다. 별 12 × 5, 여백 4 → 68×20, 모서리 4. `value`에 채운 별 개수(0~5)를 넣으면 채운 별 `Pink`, 빈 별 `Gray70`.

| 피그마 Style | Compose | 배경 | 쓰는 곳 |
|---|---|---|---|
| Pink | `AppRating(style = Pink)` | `Pink100` | 가게 리뷰 목록 (XML `ScaleRatingBar`라 아직 직접 그림) |
| None | `AppRating(style = None)` | 없음 | 우리 동네 소식(`FeedCard`). 별 색·개수는 서버 값(`filledColor`·`emptyColor`·`maxValue`)을 넘김 |

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
