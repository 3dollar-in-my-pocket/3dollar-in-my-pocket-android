# SDUI 정비 구현 기록 — 2026-09-09

**최종 상태(2026-09-10): 승인 범위 구현·필수 검증 완료.** 아래 RED/진행 기록은 당시 상태를 보존한 것이다. 최종 단위 테스트 161개·Debug 빌드·기기 검증 결과는 14 문서에 정리했다.

## 기준

- 사용자 승인 계획: `12-sdui-alignment-plan-2026-09-09.md`.
- 시작 커밋 `24c102eb3573f3abb48155f1a051a099d403ac32`, 기존 feature checkout에서 작업.
- 운영 재조회: 최신 전체 배포 v4.76.1 / `3d269129`, 이후 유저 API 단독·단계별 배포 없음.
- 변경 전 common serverdriven/data screen/app home+v2 테스트 PASS(228 Gradle tasks,10초). `/private/tmp/gasam-sdui-baseline.log`.
- 계약 담당자가 DTO/model/mapper 및 fixture의 RED→GREEN 진행. 주 에이전트는 기기 기준 수집과 통합 준비.

## 기기 변경 전 재현

- emulator-5554, 설치돼 있던 `com.zion830.threedollars.dev` 4.22.0 (127). 이 APK를 현재 HEAD 빌드와 동일하다고 가정하지 않는다.
- Home 상세의 UI tree에서 정보 수정 버튼 `[253,799][393,862]` 확인 후 중앙 tap.
- 실제 예외: `IllegalArgumentException: The fragment EditStoreFragment ... is unknown to the FragmentNavigator. Please use the navigate() function to add fragments to the FragmentNavigator managed FragmentManager.`
- 기존 문서가 예상한 `No view found ... layout_container`와 예외 문구는 달랐다. NavHost 관리 FragmentManager에 직접 fragment를 추가한 것이 관측된 실패 경로다.
- 저장·삭제 등 서버 변경 버튼은 누르지 않았다. 앱 데이터는 보존했다.
- 증거: `build/harness/sdui-alignment-20260909/02-baseline-edit.xml`, `03-baseline-edit-result.xml`, `03-baseline-crash.log`.
- crash 로그에서 해당 FragmentNavigator 오류가 없음을 검사한 assertion이 실패(RED)했다. 이후 새 APK에서 Home/full-screen 왕복 검증 필요.

## 진행 상태

### 공통 렌더러 RED

- debug 전용 `ServerDrivenContractPreviewActivity`와 실제 UI tree/PNG 검사 스크립트 추가. 릴리스 경로에는 포함되지 않는다.
- 수정 전 확인: 288×180 이미지를 120dp 폭에 넣었을 때 120×180으로 비율 손실, 빈 chip이 16×27dp 공간 차지, END icon이 텍스트 앞에 위치. 세 assertion 모두 실패.
- `SDChipRenderer`에 빈 요소 생략과 END 정렬을 적용하고 `SDImageRenderer`에 폭 제약에 따른 aspect ratio 유지 및 기본 크기 인자를 추가했다.
- 검사 결과: `build/harness/sdui-alignment-20260909/04-components-red.{xml,png}`. GREEN은 모델·호스트 통합 후 새 APK에서 실행할 예정이다.

### 현재 HEAD 기반 debug APK의 진입 RED

- debug 검사 화면만 추가한 현재 HEAD 기반 4.22.1 (128) APK로 업데이트 후 `dollars-dev://store?storeId=120024`를 통해 상세에 진입했다.
- 서버 기여자 row를 눌러 기여자 화면이 열림을 UI tree로 확인. 서버 `SDPageViewLog` 및 `store_id` 추가값 전달 assertion이 실패했다. 기존 legacy pageview가 없다는 주장은 아니다.
- 다시 상세로 돌아와 방문 인증 버튼을 눌렀으나 `StoreCertificationActivity` 대신 MainActivity가 resumed가 됐다. 인증 화면 진입 assertion 실패. 실제 인증 전송은 하지 않았다.
- 증거: `07-contributors-red*`, `08-before-certification.xml`, `09-certification-red*`.

위 내용은 구현 도중 남긴 기록이며 최종 결과는 [검증 기록](14-sdui-alignment-verification-2026-09-09.md)에 정리했다.

## 2026-09-10 재개 후 구현 및 검토

### 계약과 상세

- 운영 응답 4개를 익명화한 fixture와 배포 생성 코드 기반 16종 fixture를 추가했다. DTO → mapper → model에서 섹션 수 14/15/15/16과 순서를 보존한다. 계약 독립 리뷰는 승인됐다.
- EDIT의 nullable map과 최상위 actionBars, CALLOUT 전용 image/text/style, MARGIN을 연결했다. 독립 MAP과 기존 아이콘 URL 보정은 유지한다.
- 좌표 resolver는 유효한 액션 좌표 쌍 → EDIT.map → 독립 MAP → 같은 가게의 Home marker 순서다. 좌표가 없을 때 현재 상세를 닫지 않는다.
- 새 EDIT 지도 액션, 쿠폰 `COUPON_ISSUED_KEY` 우선, plain 은행명+계좌번호 복사, POST 탭 및 대상 없는 로컬 탭 비활성을 연결했다. link 우선 및 outer clickLog 우선 규칙을 유지한다.
- 영업일·방문·정보·리뷰의 서버 surface, 이미지 크기, 답글 날짜, header 부제목·trailing action을 소비한다. AdMob은 카드별 state와 click/실패/해제 callback을 분리했다.
- Compose `key` 내부 `return@key`가 D8의 `NON_LOCAL_RETURN` 합성 코드 오류를 일으킨 것을 bytecode로 확인했다. 조건문 밖으로 key를 감싸는 구조로 고친 뒤 Debug 빌드가 통과했다.

### 공통 렌더러와 기여자

- 공통 chip은 START/END와 contentSpacing, 빈 요소 생략을 적용한다. 공통 이미지는 부모 폭이 작을 때 비율을 유지하며 null 크기에만 기존 기본값을 적용한다. 명시된 0/음수/유한하지 않은 크기는 렌더를 생략한다.
- 독립 리뷰에서 nested span 스타일 유실을 추가 확인했다. 중첩 색/굵기/크기 복원과 HTML entity 단일 해석의 2개 assertion RED를 확인하고 stack 기반 파싱으로 수정했다.
- 기여자 header·nullable description·button/actionBar 로그를 연결했다. RESUMED 진입별로 응답 상태가 결정된 뒤 서버 pageview 또는 로컬 fallback 하나를 선택한다.
- debug 전용 컴포넌트·운영 익명 fixture 화면과 UI tree/PNG checker를 추가했다. fixture는 data 테스트 리소스를 debug assets에서 재사용한다. 실제 기여자 하단 버튼을 fake callback으로 검증하기 위해 함수 접근만 private→internal로 변경했다.

### 홈 상태와 TDD 절차 차이

- 홈 담당자가 DTO 연결, 요청 gate/generation, pagination, focusBounds, 초기 줌, nullable marker/refs, 스타일/로그, Activity 소유 FragmentManager의 Edit 진입을 구현했다.
- 단, 새 홈 정책 일부는 unresolved reference 컴파일 오류만 관측한 뒤 구현되어 동작 assertion RED가 부족했다. 기존 변경을 보존한 회귀 검증으로 복구하는 승인을 요청했고, 2026-09-10 사용자가 해당 복구 문구를 인용하며 **“이어서진행해”로 승인**했다. 동일 승인을 반복 요청하지 않고 홈 후속 구현·검증을 재개했다.
- 승인 대기 중에는 홈의 후속 구현만 보류하고 독립 계약·상세·공통 검증을 계속했다. 기존 변경을 삭제하거나 테스트 순서를 재현하려고 되돌리지 않았다. 이후 새 결함의 동작 assertion RED→GREEN과 실제 ViewModel fake repository 테스트로 복구했다.
- 실제 HomeViewModel에는 firstPageGate와 selection helper가 연결돼 있다. 담당자 초기 보고의 “미연결” 문장은 오래된 상태이며 현재 소스와 다르다.
- 독립 홈 리뷰는 P2 7건을 확인했다: focus 세대/gesture 무효화, STOPPED 중 effect 유실, 복원/재개 시 강제 fit, pagination query snapshot, 제목 HTML 제거, map-ready 전 선택 marker 복원, 실패 후 필터 retry의 실제 호출 경로.
- 최초 7건과 후속 gesture/marker 경계는 모두 수정했다. 실제 HomeViewModel 테스트·기기 회귀와 독립 재리뷰가 통과했다.

### 최종 통합에서 추가 확인한 경계

- Home pageview가 Activity 수명 동안 한 번만 나가던 것을 기존 `onResume` 진입 기준으로 복구했다. 같은 진입 중 필터 재시도는 추가 전송하지 않고 다음 진입부터 최신 서버 로그를 사용한다. 실제 Home→마이페이지→Home에서 서버 pageview와 preset이 각 1회, legacy 중복 0회였다.
- NearStore 지도 준비 시 권한이 있으면 NoFollow, 없으면 None으로 설정한다. 위치 source/overlay와 명시적 현재 위치 버튼은 유지한다. 이 한 줄 변경은 담당자와 probe 준비가 겹쳐 변경 후 회복 검증했다. 기존 NearStore bytecode SHA를 보존하고 새 probe만 담은 baseline APK에서 `expected NoFollow, actual Follow` RED(84)를 확인했으며, 정상 최신 소스 빌드의 기기 검증(86/89)에서 통과했다. baseline 패키징을 최신 소스 빌드 통과로 계산하지 않았다.
- 최종 리뷰에서 발견한 Home 무좌표 방문 인증의 강제 닫기 두 줄은 markerless 카드+map 없는 정상 상세를 실제 VM/host에 넣어 RED(85)를 확인한 뒤 제거했다. 안내 후 선택 가게와 펼친 상세를 유지하는 GREEN(86/89)을 확인했다.
- 실제 Activity FragmentManager에서 빠른 Edit 요청 2회→화면 1개, 취소→갱신 0회, fake 저장 성공→목록/상세 각 1회 갱신을 검증했다. 실제 SDK projection으로 bounds가 필터·바텀시트에 가려지지 않고 page2의 다른 bounds에도 카메라/검색 조건이 유지됨을 확인했다.
- 기존 리뷰 저장 ViewModel의 repository를 debug runner 안에서만 fake로 교체해 payload와 성공 후 Home 목록/상세 각 1회 갱신을 확인했다. 서버 리뷰 저장은 호출하지 않았다.
- 계약·상세/공통·홈·최종 통합 리뷰 모두 Approved, 확인된 잔여 P1/P2 없음. 후속 UX QA는 원래 승인 범위 밖으로 유지한다.

검증의 실제 통과·미실행 항목은 [검증 기록](14-sdui-alignment-verification-2026-09-09.md)에 구분한다. 서버 변경·버전/의존성 변경·원격 반영은 하지 않았다.

## 배포 custom action 18종의 파라미터와 실행 대상

배포 커밋 `SDCustomActionType`과 각 composer, Android `StoreDetailV2ViewModel`·Home/V2 host·`ServerApi`를 대조했다. 아래 `STORE_ID`는 composer가 현재 상세 가게 ID로 생성한다. mutation은 현재 선택된 상세 ID에 귀속되며, coupon/review/post의 개별 식별자는 액션에서 읽는다. 모든 action에서 `button.link`가 있으면 기존 link 경로가 우선한다.

| 서버 actionType | 배포 파라미터 | Android 실행 대상 |
| --- | --- | --- |
| STORE_PREVIEW_SECTION_SHARE | STORE_ID, STORE_TYPE | 두 host의 공유 함수 → Kakao 공유 선택 화면. 공통 좌표 resolver |
| STORE_PREVIEW_SECTION_NAVIGATION | LATITUDE, LONGITUDE, STORE_NAME | 공통 좌표 resolver → DirectionBottomDialog |
| STORE_PREVIEW_SECTION_REVIEW_WRITE | STORE_ID | 가게 유형에 따라 AddReviewDialog / BossReviewWriteActivity |
| STORE_EDIT_SECTION_UPDATE | STORE_ID | layout_container 소유 Activity FragmentManager의 EditStoreFragment |
| STORE_EDIT_SECTION_REPORT | STORE_ID | DeleteStoreDialog의 사유 선택 → 기존 신고 repository |
| STORE_COUPON_SECTION_COUPON_ISSUE | STORE_ID, COUPON_ID | issueStoreCoupon → POST `/api/v1/store/{storeId}/coupon/{couponId}/issue` |
| STORE_COUPON_SECTION_COUPON_USE | COUPON_ISSUED_KEY | useIssuedCoupon → PUT `/api/v1/issued-coupon/{issuedKey}/use`. ISSUED_KEY는 호환 fallback |
| STORE_POST_SECTION_ADD_LIKE | STORE_ID, POST_ID, STICKER_ID | putStorePostStickers → PUT `/api/v1/store/{storeId}/news-post/{postId}/stickers`, `[LIKE]` |
| STORE_POST_SECTION_CANCEL_LIKE | STORE_ID, POST_ID, STICKER_ID | 같은 API에 빈 stickers 배열. STICKER_ID 유무와 관계없이 취소 유지 |
| STORE_IMAGE_SECTION_ADD_IMAGE | STORE_ID | MoreImageActivity |
| STORE_IMAGE_SECTION_IMAGE_ENLARGE | IMAGE_ID, IMAGE_URL | 현재 이미지 목록에서 index 해석 → StorePhotoDialog |
| STORE_REVIEW_SECTION_REVIEW_WRITE | STORE_ID | AddReviewDialog / BossReviewWriteActivity |
| STORE_REVIEW_SECTION_REPORT | STORE_ID, REVIEW_ID | 신고 사유 조회/선택 → reportStoreReview(`/api/v1/store/{storeId}/review/{reviewId}/report`) |
| STORE_REVIEW_SECTION_DELETE | STORE_ID, REVIEW_ID | deleteStoreReview → DELETE `/api/v2/store/review/{reviewId}` |
| STORE_REVIEW_SECTION_ADD_LIKE | STORE_ID, REVIEW_ID, STICKER_ID | 기존 HomeRepository.putStickers → PUT `/api/v1/store/{storeId}/review/{reviewId}/stickers`, `[LIKE]` |
| STORE_REVIEW_SECTION_CANCEL_LIKE | STORE_ID, REVIEW_ID, STICKER_ID | 같은 API에 빈 stickers 배열 |
| STORE_EDIT_SECTION_COPY_ADDRESS | ADDRESS | ClipboardManager + 기존 주소 복사 안내 |
| STORE_EDIT_SECTION_MAP_ENLARGE | 없음 | EDIT.map 우선 fallback 좌표 → FullScreenMapActivity |

서버 enum 외 호환 경로로 기존 `STORE_MAP_SECTION_COPY_ADDRESS`/`STORE_MAP_SECTION_MAP_ENLARGE`를 유지한다. 계좌 복사는 서버 custom action이 없는 버튼을 기존 `ACCOUNT_COPY` 로컬 action으로 연결하며, 은행명+계좌번호의 HTML만 제거하고 예금주는 전달하지 않는다. 추가 API 호출은 없다. 방문 인증·기여자·탭은 각각 기존 link/local anchor 경로이며 custom action 18개 수에 중복 계산하지 않는다.

실제 외부 변경은 수행하지 않았다. mutation action의 식별자·stickers·결과 갱신은 fake repository 테스트에서, 지도/공유/클립보드는 기기 진입과 테스트 대역에서 검증한다.
