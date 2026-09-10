# TH-1226 QA 분석 — 2026-09-09

최신 SDUI 계약을 앱이 일부 지원하지 않는 문제와, 사용자 동작이 누락된 문제가 함께 있다. 가장 먼저 확인·수정할 대상은 Home 정보 수정 크래시이며, 지도·여백·인증 안내는 최신 계약에 맞춰 DTO → mapper → UI → action을 함께 보완해야 한다. `AD_MOB`는 구현되어 있어 미노출 원인을 별도로 재현해야 한다.

이 문서는 분석 결과다. 앱 코드 수정, 새 빌드·테스트·에뮬레이터 재현, Jira 상태/댓글 변경, Discord 메시지 전송은 하지 않았다.

## 조사 범위와 기준

- [TH-1226][parent]의 하위 QA 14개: 본문, 상태, 댓글 및 첨부 영역을 전부 읽었다. 12개는 `해야 할 일`, TH-1273/1274는 `배포 완료 & 닫음`이다. Jira 우선순위는 모두 Medium이다.
- 14개 중 TH-1271, TH-1280, TH-1281만 설명이 있다. 나머지 11개는 제목만 있으며, 하위 티켓에서 재현 영상·로그·댓글·첨부를 확인하지 못했다. 제목만으로 실제 기기, 가게 ID, 진입 경로, 기대 디자인의 모든 세부를 확정하지 않는다.
- [Discord 상세 바텀시트 스레드][thread]의 시작(8월 22일)부터 마지막 변경 안내(9월 8일)까지 읽고 첨부 이미지 4개를 확대해 확인했다. 첨부는 칩 간격, 기여자 행 아이콘/배경, 섹션 사이 여백, EDIT 통합 범위를 설명한다.
- 연결된 [MARGIN][margin], [EDIT][edit], [CALLOUT][callout], [Design System][design-system] Notion 명세를 읽었다.
- 현재 기준은 `feature/TH-1226-store-detail-bottomsheet`, HEAD `24c102eb`, 앱 `4.22.1 (128)`이다. 분석 시작 시 working tree는 깨끗했다.
- 기존 [클릭 검증 기록](06-click-verification-2026-09-06.md), [설계](00-design.md), 배포 영수증과 현재 코드를 대조했다. 기존 store `120024`의 10개 섹션 검증은 최신 섹션 계약 전체를 검증한 결과가 아니다.
- 공개 OpenAPI `https://dev.threedollars.co.kr/api/v3/api-docs`도 조회했으나 이 환경에서 30초 timeout으로 내용을 받지 못했다. 최신 실응답과 서버 validation은 이번 분석에서 검증하지 못했다.

## 14개 QA의 판단

아래 순서는 분석상 권장 순서이며 Jira 우선순위를 변경한 것이 아니다. “코드 확인”은 런타임 재현 완료를 의미하지 않는다.

| 티켓 | 현재 상태 | 코드·명세 대조 결과 | 후속 범위 |
| --- | --- | --- | --- |
| [TH-1271: EDIT 지도 누락][1271] | 해야 할 일 | **최신 계약 미지원 확인.** 현재 EDIT는 `actionBars`만 읽고 `map`은 버린다. 새 지도 action 이름도 지원하지 않는다. | 통합 EDIT 모델·지도 표시·복사/확대 및 좌표 소비 경로를 함께 보완. |
| [TH-1272: 사진 없는 경우][1272] | 해야 할 일 | **빈 상태 안내 없음 확인.** 카드가 비면 헤더만 남고 별도 안내가 없다. 헤더의 사진 제보 버튼은 남을 수 있다. | 빈 사진 상태와 제보 진입, 0→1장 갱신 검증. |
| [TH-1273: 미설정 영업 요일][1273] | 배포 완료 & 닫음 | 서버 완료 항목. 앱은 받은 `items`를 표시한다. | 최신 응답에서 전체 요일과 미설정 문구가 오는지 확인 후 회귀 검증. |
| [TH-1274: 리뷰 미리보기 제거][1274] | 배포 완료 & 닫음 | 서버 완료 항목. `bodies=[]`는 처리되지만 V2 mapper는 필드 누락/null이면 PREVIEW 전체를 버린다. | 실제 제거 형태 확인. 누락/null이면 mapper 호환성 보완 필요. |
| [TH-1275: 위로 밀어 펼치기][1275] | 해야 할 일 | 핸들 드래그는 구현되어 있다. **Preview 본문 swipe는 sheet 확장에 연결되지 않음.** 본문 클릭으로는 펼쳐진다. | 핸들/본문/이미지/리스트 스크롤의 제스처 전달과 우선순위 검증. |
| [TH-1276: 메뉴 접기·펼치기][1276] | 해야 할 일 | **누락 확인.** INFO_V1/V2가 전체 메뉴를 순회하며 접힘 상태가 없다. | USER/BOSS별 접힘 기준과 펼치기·접기 UX를 디자인 기준으로 적용. |
| [TH-1277: 정보 수정 크래시][1277] | 해야 할 일 | **Home의 FragmentManager/container 불일치가 유력한 직접 원인.** 전체 화면 상세는 다른 경로다. | 최우선 Home 재현 및 manager 수정, Home/full-screen 모두 왕복 검증. |
| [TH-1278: 사진 제보가 목록으로 이동][1278] | 해야 할 일 | **보고와 코드 일치.** 두 host 모두 `MoreImageActivity`를 연다. 앨범은 거기서 제보 버튼을 다시 눌러야 열린다. | 사진 제보에서 picker 직접 진입 및 취소·업로드 후 복귀 처리. |
| [TH-1279: 딥링크 상세에서 지도로 전환][1279] | 해야 할 일 | **현재 흐름에서 미지원.** 딥링크는 독립 V2 Activity이며 Home sheet의 제스처/상태가 없다. | TH-1280과 함께 딥링크 → 상세 → 가게 중심 지도 흐름 설계. |
| [TH-1280: 지도 중심이 내 위치][1280] | 해야 할 일 | **가게 위치를 Home에 넘기지 않는 경로 확인.** 종료 시 일반 MainActivity로 복귀/진입하며 대상 가게/좌표 전달이 없다. | 가게 중심 camera, 선택 marker, 주변 가게 조회를 함께 연결. |
| [TH-1281: CALLOUT][1281] | 해야 할 일 | **최신 계약 미지원 확인.** 함수는 있지만 `title` 기반 모델이며 명세의 `content.image/text/style`를 읽지 못해 mapper에서 섹션을 버린다. | CTA와 다른 CALLOUT 모델·표시 구현. VERIFIED 가게로 검증. |
| [TH-1282: ADMOB][1282] | 해야 할 일 | **미구현으로 확정할 수 없음.** `AD_MOB` mapper, 상태, 광고 load, 성공/실패 처리와 표시가 존재한다. | 실제 section/card와 AdMob 오류를 수집해 서버 누락·파싱 탈락·no-fill·UI 문제를 구분. |
| [TH-1283: 전체 디자인][1283] | 해야 할 일 | 구체적 재현 내용 없음. **MARGIN 미지원, SDChip END 무시**는 Discord/명세/코드로 확인했다. | 계약 보완 후 USER/BOSS 및 긴/빈 데이터로 Figma 대조. 전 화면 픽셀 검증은 아직 하지 않았다. |
| [TH-1284: 글+사진 리뷰 작성][1284] | 해야 할 일 | **USER 경로 기능 누락 확인.** USER는 별점/글 Dialog, BOSS는 이미지 업로드까지 지원한다. 둘은 같은 v3 작성 endpoint를 쓴다. | 기존 업로드와 create API 재사용 우선. USER 이미지 허용 validation 확인 후 DTO/UI 연결. |

## 계약 변경이 UI 누락으로 이어지는 경로

### EDIT: 지도만 붙이면 끝나는 변경이 아님

[EDIT 명세][edit]에는 nullable `map`, 그 안의 `location`/`footerLeft`/`footerRight`, 수정·제보 `actionBars`가 있다. 위치가 없으면 map이 null이고, 사장님 가게는 actionBars가 빈 목록이라는 설명이다. Discord의 9월 7일 첨부에서도 상단 TAB을 제외한 지도와 두 버튼이 하나의 빨간 테두리로 묶여 있다.

현재 [응답 DTO](../../../core/network/src/main/java/com/threedollar/network/data/screen/StoreDetailSectionResponses.kt)의 `StoreDetailEditSectionResponse`(42행), [mapper](../../../data/src/main/java/com/threedollar/data/screen/StoreDetailScreenMapper.kt)의 EDIT 분기(151행), [renderer](../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2PrimarySections.kt)의 `StoreDetailEditSection`(239행)은 버튼 목록만 처리한다.

- top-level `actionBars`가 있으면 버튼은 표시되지만 `map`은 유실된다.
- `actionBars`가 없고 `map.actionBars`만 있으면 EDIT 전체가 탈락한다.
- 빈 actionBars는 현재 Row의 세로 padding만 남길 수 있다.
- 새 명세의 `STORE_EDIT_SECTION_COPY_ADDRESS`, `STORE_EDIT_SECTION_MAP_ENLARGE`를 [ViewModel](../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2ViewModel.kt)은 지원하지 않는다. 기존 `STORE_MAP_SECTION_COPY_ADDRESS`, `STORE_MAP_SECTION_MAP_ENLARGE`만 처리한다.
- [V2 Activity](../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2Activity.kt)의 방문 인증(268행), 지도 확대(325행), 길안내(338행), 공유(348행)는 독립 `StoreDetailSectionModel.Map`에서 좌표를 찾는다. 통합 EDIT만 오는 경우 방문 인증의 위치 오류 및 좌표 fallback 실패까지 확인해야 한다.

**문서 불일치:** Notion 필드 표는 `actionBars`를 EDIT 최상위에 두지만 JSON 예제는 `map` 안에 둔다. 실제 필드 위치는 최신 payload/OpenAPI로 확정해야 한다. 분석만으로 임의의 JSON 구조를 정답으로 고르지 않는다.

### MARGIN과 칩: 서버가 제어하도록 합의됐지만 클라이언트가 아직 읽지 않음

9월 7일 19:08의 `sectionStyle.marginBottom`은 중간 제안이다. 22:16의 재제안과 23:19의 동의 이후, [9월 8일 00:16 최종 안내][discord-final]에서 **별도 MARGIN 섹션**으로 확정됐다. [현재 명세][margin]의 예시는 `{"type":"MARGIN","height":8}`이며 `height`는 필수 Int다. 현재 DTO/model/mapper/render 분기가 없어 알 수 없는 섹션으로 버린다.

명세대로 MARGIN이 내려오면 현재 앱은 이를 버린다. 해당 QA 화면에서 간격이 없는 직접 원인인지는 최신 응답으로 확인해야 한다. 구현은 서버 배열의 MARGIN 위치와 height를 반영해야 하며 모든 섹션 사이에 임의의 간격을 넣는 방식으로 일반화하지 않는다. MARGIN에는 색상 필드가 없으므로 배경색까지 서버 계약에 있다고 단정하지 않는다.

SDChip은 DTO/model/mapper에 `imageAlignment`, `contentSpacing`이 이미 있다. [SDChipRenderer](../../../core/ui/src/main/java/com/zion830/threedollars/core/ui/serverdriven/ServerDrivenRenderer.kt)(286행)는 spacing을 적용하지만 항상 이미지 → text → additionalText 순서로 그려 `END`를 무시한다. 첨부의 별점 예시는 이미지·별점·개수 사이 2px 간격을 표시한다. 이 항목은 공통 renderer 보완이 필요하다.

아이콘 URL의 base path 오류는 [9월 7일 서버 수정 안내][discord-icons]가 있다. 앱의 기존 6개 URL 보정은 과거 응답을 위한 호환 처리이므로 이번 분석에서는 제거하지 않았다. 현재도 아이콘이 빠지는지는 최신 URL/실화면을 다시 확인할 문제다.

### CALLOUT: 코드 존재와 계약 지원은 다름

[CALLOUT 명세][callout]는 `content.image`, `content.text`, `content.style`을 필수로 정의한다. VERIFIED 라벨 가게에만 내려오며, 예제는 인증 이미지와 HTML 문구, `#232323` 배경이다.

현재는 CTA와 같은 `StoreDetailContentResponse(title, subTitle, footerLeftButton)`로 파싱한다. mapper의 `asModelOrNull()`(300행)은 title이 없으면 null을 반환하고, 상위 `mapNotNull`이 CALLOUT을 제거한다. 표시 함수 자체도 회색 배경/title/subTitle 구조이므로 최신 인증 안내를 렌더링할 수 없다. CALLOUT 전용 모델로 분리하면서 기존 CTA의 title/subTitle/button 동작을 보존해야 한다. 미인증 가게에서 CALLOUT이 없는 것은 정상이다.

## 동작 문제와 기존 검증의 한계

### 정보 수정 크래시를 놓친 이유

[HomeFragment.openStoreEdit](../../../app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt)(648행)는 `parentFragmentManager`에 `R.id.layout_container`로 EditStoreFragment를 추가한다. Home은 [NavHost 내부 destination](../../../app/src/main/res/navigation/mobile_navigation.xml)이며, [activity_home.xml](../../../app/src/main/res/layout/activity_home.xml)(50행)의 `layout_container`는 NavHost 바깥 Activity의 형제 overlay다. 하위 FragmentManager가 이 container를 찾을 수 없는 구조다. 예상 예외는 `No view found for id ... layout_container`이며, 실제 티켓의 stack trace는 제공되지 않았다.

같은 Home의 주소 검색(401행)은 `requireActivity().supportFragmentManager`로 해당 overlay를 연다. 전체 화면 V2 Activity는 자기 `supportFragmentManager`와 자기 container를 사용한다.

기존 클릭 기록의 “정보 수정 정상” 증거 `47-edit-store.xml`에는 `store_detail_v2_fragment_container`가 있다. 즉 **full-screen V2 경로의 성공이며 Home 경로 성공 증거가 아니다.** 기존 기록을 Home까지 검증한 것으로 확대해서는 안 된다. 이번 분석에서는 이 누락을 확인했으며, 새 런타임 재현은 하지 않았다.

### 펼치기·메뉴·사진

- [HomeBottomSheetContent](../../../app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeBottomSheetContent.kt)는 핸들의 `detectVerticalDragGestures`로 확장을 지원하지만 Preview 본문은 별도 LazyColumn이다. 제목/이미지 클릭은 확장에 연결되어 있으나 본문 swipe는 연결되지 않는다. TH-1275의 시작 영역과 scroll 충돌을 나눠 검증해야 한다.
- [InfoV1/V2 renderer](../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2InfoSections.kt)는 메뉴 전체를 표시한다. 기존 USER는 카테고리 2개, BOSS는 메뉴 5개 기준으로 접는 흐름이 있으나 신규 디자인의 정확한 기준이 같다고 확정하지 않는다.
- [사진 renderer](../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2EngagementSections.kt)(137행)는 빈 cards에 대한 안내가 없다. 기존 `photo_empty` 문자열을 재사용할 수 있다.
- 현재 Home/V2의 사진 제보는 [MoreImageActivity](../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/MoreImageActivity.kt)로 이동한다. 이 화면은 먼저 사진 목록을 표시하고 별도 등록 버튼으로 picker를 연다. 정상적으로 그 화면에 도착했다는 과거 검증은 “제보 버튼에서 바로 앨범”이라는 QA 기대 충족을 의미하지 않는다.

### 딥링크와 지도 복귀는 하나의 흐름으로 처리

[DynamicLinkActivity](../../../app/src/main/java/com/zion830/threedollars/DynamicLinkActivity.kt)(119행)는 가게 딥링크를 V2 Activity로 연결한다. V2 Activity에는 Home의 Preview/Expanded와 지도 복귀 gesture가 없다. 종료 시 [navigateToMainActivityOnCloseIfNeeded](../../../app/src/main/java/com/zion830/threedollars/utils/SystemUtils.kt)(256행)를 사용하지만 대상 store ID/좌표를 전달하지 않는다.

TH-1280 본문은 “현재 내 위치 → 가게를 중심으로 주변 가게”를 요구한다. 상세 안의 작은 지도 중심만 수정해서 해결되는 티켓이 아니다. 앱 종료 상태와 이미 Home이 열린 상태 모두에서 가게 선택, camera 위치, 주변 목록 갱신, sheet 상태를 함께 다뤄야 한다.

기존 [설계](00-design.md)는 Home 외 caller/딥링크를 full-screen으로 분리했다. TH-1279/1280은 이 설계에서 지원하지 않은 사용자 흐름을 요구하므로 구현 단계에서 해당 설계와 수용 기준을 함께 갱신해야 한다. 제목의 “위로 모션”만으로 상세→지도 전환 방향을 임의 확정하지 않는다.

### 광고와 리뷰는 별도 판단 필요

`AD_MOB`는 [mapper](../../../data/src/main/java/com/threedollar/data/screen/StoreDetailScreenMapper.kt)(126행)와 [renderer/광고 load callback](../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2EngagementSections.kt)(312행 이후)이 있다. 첫 로드 실패 시 슬롯을 숨기는 정책도 있다. cardId/clickLog/impressionLog가 없는 카드의 mapper 탈락, section 미제공, AdMob no-fill 등 서로 다른 원인이 가능하다. 실제 ticket 대상 가게와 응답, load error 없이는 단일 원인을 확정할 수 없다. 광고 클릭은 검증 대상으로 요구하지 않는다.

USER 리뷰는 [AddReviewDialog](../../../app/src/main/java/com/zion830/threedollars/ui/dialog/AddReviewDialog.kt)와 [StoreReviewRequest](../../../core/network/src/main/java/com/threedollar/network/request/StoreReviewRequest.kt)에 글/별점/가게 ID만 있다. BOSS의 [BossReviewWriteActivity](../../../app/src/main/java/com/zion830/threedollars/ui/storeDetail/boss/ui/BossReviewWriteActivity.kt)는 이미지 선택·선업로드를 지원하고, [BossStoreReviewRequest](../../../core/network/src/main/java/com/threedollar/network/request/BossStoreReviewRequest.kt)는 `images[{url,width,height}]`를 보낸다.

[ServerApi](../../../core/network/src/main/java/com/threedollar/network/api/ServerApi.kt)(326~344행)에서 두 작성 함수는 모두 `POST /api/v3/store/review`를 사용한다. 업로드도 기존 `POST /api/v1/upload/{fileType}`와 `STORE_REVIEW_IMAGE`를 재사용할 수 있다. 새 endpoint 신설보다 기존 create payload/UI 확장을 우선 검토할 근거가 있다. 단, USER_STORE에 images를 허용하는 서버 validation은 로컬 코드만으로 증명되지 않는다. 리뷰 수정 endpoint의 현재 요청에는 images가 없으므로 이번 티켓의 “작성” 범위를 사진 수정/삭제까지 임의 확대하지 않는다.

## 시간순 변화와 권장 작업 순서

| 시각 (KST) | 확인된 변화 | 의미 |
| --- | --- | --- |
| 9월 6일 | 기존 앱 실응답/클릭 검증 | 독립 MAP + EDIT 등 10개 섹션 및 일부 진입 경로 기준. |
| 9월 7일 18:29 | 서버 아이콘 base path 수정 안내 | 기존 URL 오류와 현재 상태를 구분할 필요. |
| 9월 7일 19:39 | 서버가 EDIT 통합·간격 정보·이미지 수정 배포 안내 | 앱이 기존 DTO로 읽으면 지도 유실 가능. |
| 9월 7일 21:21 | 앱 4.22.1 (128) Firebase 배포 | 로컬 배포 영수증 기준. 소스는 현재 HEAD와 동일. |
| 9월 8일 00:16 | 별도 MARGIN 섹션 포함 최종 정리 | 앱 배포 후 확정된 계약도 있어 기존 QA 결과 재사용에 한계. |

1. **크래시:** Home 정보 수정 재현 → container 소유자에 맞는 FragmentManager 사용 → Home/full-screen 취소·완료·뒤로가기 검증.
2. **계약:** 최신 USER/BOSS/VERIFIED 응답 확보 → EDIT 실제 nesting 확정 → EDIT/새 action/좌표 소비 경로, MARGIN, CALLOUT, chip END 지원. PREVIEW bodies 누락/null도 함께 확인.
3. **기본 사용자 동작:** 사진 빈 상태/바로 picker, 메뉴 접기·펼치기, Preview 본문 swipe를 연결하고 취소·복귀 시 선택/스크롤 상태를 검증.
4. **흐름 확장:** 딥링크의 가게 중심 지도 복귀를 두 티켓 공통 요구로 설계·구현. USER 글+사진 리뷰는 기존 업로드/create API를 활용하되 서버 허용 여부를 확인.
5. **광고 및 디자인 QA:** 광고 요청과 no-fill 원인 분리 후, 최신 계약이 표시되는 상태에서 USER/BOSS/VERIFIED·빈/긴 데이터의 전체 화면을 대조. 서버 완료 2건도 이때 회귀 확인.

다음 구현의 필수 증거는 Home/full-screen을 구분한 크래시/복귀 검증, 최신 계약 fixture, USER/BOSS 데이터, 빈 사진·메뉴 경계값, 광고 load 결과다. 미확인 항목은 EDIT actionBars의 실제 위치, CALLOUT이 존재하는 가게, 광고 미노출 대상과 오류, USER 이미지 작성 validation, 메뉴 접힘/딥링크 gesture의 상세 수용 기준이다. 먼저 API·디자인·재현으로 해소하고 자료로 결정할 수 없는 부분만 사용자에게 확인한다.

분석 문서의 로컬 파일 참조, 14개 티켓 포함 여부, Markdown 공백 오류를 검사했다. 앱 테스트는 이번 턴에 실행하지 않았으며 이 문서를 수정 완료/QA 통과 기록으로 사용하지 않는다.

[parent]: https://3dollarinmypocket.atlassian.net/browse/TH-1226
[thread]: https://discord.com/channels/1505467409488678952/1540626483582603295
[margin]: https://app.notion.com/p/MARGIN-3d47ad52990e8093b89acfa8f9abb4ae?source=copy_link
[edit]: https://app.notion.com/p/EDIT-3a67ad52990e80eaa2c4f23aae69921f?source=copy_link
[callout]: https://app.notion.com/p/CALLOUT-3a67ad52990e808e9130cc365fc353b6?source=copy_link
[design-system]: https://app.notion.com/p/Design-System-UI-Elements-2e87ad52990e80cd8e18e7d9ffc631d5?source=copy_link#2e87ad52990e80698f81e392868c700b
[discord-final]: https://discord.com/channels/1505467409488678952/1540626483582603295/1546539768727478293
[discord-icons]: https://discord.com/channels/1505467409488678952/1540626483582603295/1546452251193778288
[1271]: https://3dollarinmypocket.atlassian.net/browse/TH-1271
[1272]: https://3dollarinmypocket.atlassian.net/browse/TH-1272
[1273]: https://3dollarinmypocket.atlassian.net/browse/TH-1273
[1274]: https://3dollarinmypocket.atlassian.net/browse/TH-1274
[1275]: https://3dollarinmypocket.atlassian.net/browse/TH-1275
[1276]: https://3dollarinmypocket.atlassian.net/browse/TH-1276
[1277]: https://3dollarinmypocket.atlassian.net/browse/TH-1277
[1278]: https://3dollarinmypocket.atlassian.net/browse/TH-1278
[1279]: https://3dollarinmypocket.atlassian.net/browse/TH-1279
[1280]: https://3dollarinmypocket.atlassian.net/browse/TH-1280
[1281]: https://3dollarinmypocket.atlassian.net/browse/TH-1281
[1282]: https://3dollarinmypocket.atlassian.net/browse/TH-1282
[1283]: https://3dollarinmypocket.atlassian.net/browse/TH-1283
[1284]: https://3dollarinmypocket.atlassian.net/browse/TH-1284
