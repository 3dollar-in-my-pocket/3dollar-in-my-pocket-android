# TH-1226 실화면 재검증 — 2026-09-06

이 문서는 수정 전 관찰 기록이다. 후속 수정과 클릭 검증 결과는 [06-click-verification-2026-09-06.md](06-click-verification-2026-09-06.md)를 따른다.

## 결과

store `120024`의 현재 Home Expanded 화면을 상단부터 CTA까지 직접 스크롤해 확인했다. 10개 section의 순서와 주요 콘텐츠는 응답과 일치하지만, 사진 크기·카드 여백과 별점 배경은 Android renderer가 서버 값을 정확히 반영하지 않는다. 일부 아이콘은 서버 URL의 HTTP 403 때문에 표시되지 않는다. 따라서 완전 일치 판정은 보류한다.

## 환경과 범위

- 기기: `emulator-5554`, `sdk_gphone64_arm64`, 1080×2400, density 420.
- 앱: `com.zion830.threedollars.dev`, versionName `4.22.0`, versionCode `127`, 설치 갱신 `2026-09-03 23:44:10`.
- 컨테이너: `MainActivity`의 Expanded 상세. 검증 종료에도 같은 Activity를 유지했다.
- 비교 기준: 사용자가 이번 대화에 제공한 store `120024` 서버 응답. 중간에 여러 필드가 잘려 있어 전체 JSON parser 검증에는 사용할 수 없다. 온전한 필드와 화면을 대조했다.
- 현재 소스 HEAD: `691389fe`. 앱을 다시 빌드하거나 재설치하지 않았다.
- 캡처/UI tree: `build/harness/th-1226-visual-20260906/`.
- 앱 코드 수정, 상세 action 버튼 클릭, 좋아요·리뷰·신고 등 서버 데이터 변경은 수행하지 않았다. 종료 시 Preview 제목을 눌러 Expanded 상태를 복원했다.

## 확인된 불일치

### 1. IMAGE 크기와 불필요한 카드 여백 — Android

- 응답: 두 image 모두 `style.width=78`, `style.height=78`. title/subTitle 없음.
- 화면: 사진이 약 `132×132dp`로 표시되고 사진 아래부터 리뷰 앞까지 큰 공백이 남는다.
- 원인: `StoreDetailV2EngagementSections.kt:149`의 카드 `132×196dp` 고정과 `:165`의 이미지 높이 `132dp` 고정. 전달받은 image style 크기를 사용하지 않는다. title/subTitle이 없어도 카드의 나머지 64dp를 차지한다.
- 근거: [가게 정보·사진](../../../build/harness/th-1226-visual-20260906/02-info-image.png), [사진 아래 공백·리뷰](../../../build/harness/th-1226-visual-20260906/03-reviews.png).

### 2. 별점 뒤 회색 사각형과 stars.style 누락 — Android

- 응답: 별 이미지는 `24×24`, 개별 리뷰의 `stars.style.backgroundColor`는 `#FFF3F4`.
- 화면: 별 크기는 반영됐으나 각 별 뒤에 회색 사각형이 보이고 개별 리뷰의 별점 row는 흰 바탕이다.
- 원인: `StoreDetailV2PrimarySections.kt:376`에서 모든 `StoreDetailImage`에 `.background(Gray10)`를 추가한다. `StoreDetailRating`의 Row(`:277`)는 `rating.style`을 사용하지 않는다.
- 원본 `app/start_pink.png`는 HTTP 200이며 29×29 RGBA 이미지다. 투명 픽셀 320개, 좌상단 alpha 0을 확인했다. 회색 네모가 원본 파일의 배경인 것은 아니다.
- 근거: [리뷰 화면](../../../build/harness/th-1226-visual-20260906/03-reviews.png), [원본 별 이미지](../../../build/harness/th-1226-visual-20260906/server-star.png).

### 3. 복사·지도 확대·장소 제보·좋아요 아이콘 미표시 — 서버 asset

- 화면에서 주소 복사 아이콘, 지도 확대 아이콘, 없는 장소 제보 아이콘, selected 좋아요 하트가 보이지 않는다. 특히 확대 버튼은 회색 빈 버튼으로 보인다.
- 공개 URL을 직접 GET한 결과 `copy.png`, `zoom_3x.png`, `deletion.png`, `heart_fill.png`, `heart_line.png` 모두 HTTP 403이다.
- 기준 host: `https://storage.threedollars.co.kr/`.
- 이번 화면에 표시된 하트는 selected 상태다. unselected 하트는 URL 상태만 확인했다.
- 근거: [지도·수정·방문](../../../build/harness/th-1226-visual-20260906/01-map-visit.png), [리뷰](../../../build/harness/th-1226-visual-20260906/03-reviews.png), [HTTP 상태](../../../build/harness/th-1226-visual-20260906/asset-status.json).
- 앞선 strict server-driven 정책을 유지할 경우 로컬 아이콘을 임의로 복원하는 것이 아니라 서버 asset 접근 문제를 해결해야 한다.

## 응답과 일치한 항목

- `PREVIEW → AD_MOB → TAB → MAP → EDIT → VISIT → INFO_V1 → IMAGE → REVIEW → CTA` 순서.
- 제목 `ㅎㅎㅎ`, `계란빵, 와플`, `5.0 (2)`, 거리 `321m`, 최근 방문 `0명`.
- PREVIEW action 4개, 사진 2개, body 2개. raw HTML 대신 문구와 span 색상·강조가 표시된다.
- MAP 주소, 방문 성공/실패 각각 0명, 정보 업데이트 날짜, 요일/결제 방식의 비선택 상태.
- 정보·메뉴 카드의 연한 배경과 REVIEW summary의 분홍 배경.
- 리뷰 2개, 평균 5.0, selected 상태의 붉은 `좋아요 1`, 마지막 사장님 앱 CTA. CTA 문구는 화면 하단까지 잘리지 않고 읽힌다.
- 로컬 저장하기, 별도 sticky action, Home bottom navigation/handle이 Expanded에 추가되지 않는다.
- `null님이…`, 빈 메뉴 item, `방문 성공0명`, `리뷰2개`는 이번 payload와 일치한다. 화면 품질 이슈는 있지만 Android가 생성한 문구 오류로 분류하지 않는다.

## 스크롤·광고 확인

- 상단에서 하단 CTA까지 내려간 뒤 광고 위치까지 돌아오는 과정에서 상세가 닫히거나 다른 Activity로 전환되지 않았다. 마지막 상단 복원용 하향 drag는 Preview로 접혔으며, 제목 tap으로 Expanded에 재진입했다.
- 이번에는 실제 creative가 있는 테스트 광고가 로드됐다. 전체 탐색 전후에 광고가 표시되며, 짧은 추가 왕복에서는 같은 creative가 다시 표시됐다.
- [왕복 직전](../../../build/harness/th-1226-visual-20260906/06-ad-before.png) → [광고가 화면 밖인 위치](../../../build/harness/th-1226-visual-20260906/07-ad-offscreen.png) → [복귀](../../../build/harness/th-1226-visual-20260906/08-ad-return.png).
- 탐색 전체 동안에는 광고 소재가 바뀌었다. SDK 자동 갱신과 재요청의 원인을 이번 검증에서 구분하지 않았으므로 동일 AdView identity나 요청 0건을 보장하지 않는다.
- `adb -s emulator-5554 logcat -d -b crash --pid=18993` 결과는 0 byte였다. 이번 프로세스의 crash buffer 범위에서 오류가 없다.
- 검증 종료 후 [Expanded 상단으로 복원](../../../build/harness/th-1226-visual-20260906/10-restored-expanded.png)했다.

## 남은 범위

- 탭, 이미지 확대, 공유·방문·신고·좋아요 등 클릭 동작, 다른 가게/section 조합, font scale·회전·성능 수치는 이번 시각 검증 범위 밖이다.
- 이번 응답의 좋아요 취소 action은 `STICKER_ID="LIKE"`다. 9월 3일 기록의 빈 문자열과 다르므로 이전의 빈 문자열 보정 가설만으로 수정 범위를 확정하면 안 된다. ADD/CANCEL action 구분을 포함해 별도로 검증해야 한다.
- Figma와의 픽셀 단위 비교나 전체 유효 JSON 대조는 수행하지 않았다. CTA의 `footerLeftButton`은 현재 본문 오른쪽에 배치되어 있지만, payload만으로 배치 규약을 확정하지 않아 확정 결함에 포함하지 않았다.
- Gradle test/build는 실행하지 않았다. 기존 앱의 실화면 QA 결과이며 자동 회귀 테스트 통과를 의미하지 않는다.
