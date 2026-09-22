# 자동화·수동 테스트 체크리스트

유닛 테스트 코드가 못 덮는 범위를 **에이전트가 에뮬레이터로 할 것(자동화)** 과 **사람이 실기기로 할 것(수동)** 으로 가른다.
계층 정의는 `docs/process/testing.md`("테스트의 세 계층"). PR 본문의 **TC 섹션**에 들어가는 체크리스트의 출처이며, `/3dollars:pr-body`가 아래 규칙대로 자동으로 채운다.

## 1. 유닛 코드로 덮지 않는 것의 분류

경계는 하나다: **에뮬레이터로 그 상황을 만들 수 있으면 자동화, 없으면 수동.** 판정이 사람 눈이어야 한다는 건 자동화를 막는 이유가 아니다(스크린샷·영상이 증거).

| 범주 | 예 | 계층 | 확인 방법 |
|---|---|---|---|
| 화면 진입·전환 | 버튼 탭 → 화면 이동, 목록 갱신 | **자동화** | `simulator-test` 스크린샷 |
| 애니메이션·제스처 | 바텀시트 스냅, 스와이프, 마커 확대 | **자동화** | `simulator-test` 영상 (`adb shell screenrecord`) |
| 지도 렌더링 | 네이버맵 마커·클러스터·카메라 이동 | **자동화** | 에뮬레이터 스크린샷 + `adb emu geo fix <lon> <lat>` |
| 런타임 권한 팝업 | 위치·사진·알림 | **자동화** | `adb shell pm revoke <pkg> <permission>` 후 재현 |
| 딥링크 진입 | `dollars://` / `dollars-dev://`, 앱링크(https) | **자동화** | `adb shell am start -a android.intent.action.VIEW -d "dollars-dev://…"` |
| 푸시 수신 | FCM 알림 → 탭 랜딩 | **자동화** | Firebase 콘솔 테스트 발송 또는 `adb shell am broadcast` |
| 네트워크 상태 | 오프라인·느린 응답·에러 코드 | **자동화** | `adb shell svc data disable` / `adb shell settings put global http_proxy` |
| 다크모드·기기 폭 | 야간 테마, 작은/큰 화면 | **자동화** | `adb shell cmd uimode night yes`, AVD 리사이즈 |
| 카카오 로그인·공유 | 카카오톡 앱 경유 | **수동** | 실기기 (에뮬레이터에 카카오톡·계정 없음) |
| 광고 | 애드몹 실광고 노출·실패 | **수동** | 실기기 |
| 결제·스토어 | 인앱 리뷰 요청, 플레이스토어 이동 | **수동** | 실기기 (Play 서비스 제약) |

자동화로 분류했는데 `simulator-test`가 `BLOCKED`/`UNCLEAR`로 끝나면, 그 TC만 수동으로 내리고 **이유를 PR에 한 줄 남긴다.**

## 2. 화면 변경 PR 공통 체크 항목

**diff에 UI 파일이 있을 때만** PR TC 섹션에 자동으로 붙는다 (로직·CI·문서 PR에는 안 붙음).

UI 파일 판정:
- `**/src/main/res/**`
- `core/designsystem/**`, `core/ui/**`
- `app/src/main/java/com/zion830/threedollars/ui/**`, `app/src/main/java/com/zion830/threedollars/customview/**`
- `**/*Activity.kt`, `**/*Fragment.kt`, `**/*Screen.kt`, `**/*Adapter.kt`, `**/*ViewHolder.kt`, `**/*Dialog.kt`, `**/*BottomSheet*.kt`

단 `git diff --name-status`에서 **`R100`(내용 변경 없는 이동)은 제외**한다.

전부 **자동화 계층**이다 — 에뮬레이터 설정·AVD 리사이즈·adb로 상태를 만들 수 있다. 작성자가 손으로 확인하는 게 아니라 `simulator-test`가 돌고 증거를 남긴다.

```markdown
- [ ] 공통 — 다크모드에서 색·아이콘 깨짐 없음
- [ ] 공통 — 작은 화면(5인치급)·큰 화면(태블릿/폴더블)에서 레이아웃 깨짐 없음
- [ ] 공통 — 긴 텍스트/빈 데이터/0건에서 레이아웃 유지
- [ ] 공통 — 오프라인·느린 네트워크에서 로딩/에러 상태 표시
- [ ] 공통 — 비로그인(익명) 상태에서 진입 시 동작
```

필요 없는 항목은 작성자가 지우되, 지운 이유를 한 단어로 남긴다 (예: `~~다크모드~~ 색 변경 없음`).

## 3. 영역별 추가 항목

diff가 아래 경로/키워드를 건드리면 해당 블록을 추가한다.

| 트리거 (경로·키워드) | 추가 항목 |
|---|---|
| `ui/home/**`, `ui/map/**`, `NaverMap`, `NaverMapUtils` | 마커 탭·클러스터·현위치 이동·지도 드래그 후 재검색 |
| `DeepLinkInfo`, `DynamicLinkActivity`, `dollars://`, `AndroidManifest.xml` intent-filter | 콜드 스타트/백그라운드 두 경우 모두에서 딥링크 진입 |
| `initializer/**`, `FirebaseMessaging`, `Notification` | 푸시 수신(포그라운드/백그라운드), 알림 탭 랜딩 |
| `ui/login/**`, `Kakao`, `Signin`, `UserInfoViewModel` | 로그인 성공·취소·실패, 로그아웃 후 재로그인, 세션 만료(401) 처리 |
| `Admob`, `AdBanner`, `NativeAd`, `ui/community/**` | 광고 로드 실패 시 레이아웃, 광고 없는 상태 |
| `ui/write/**`, `ui/edit/**`, `ImageUpload`, `Camera` | 마법사 중간 이탈 후 복귀, 뒤로가기로 입력 유지, 권한 거부 상태, 대용량 사진 |
| `Location`, `FusedLocation`, `ui/splash/**` | 위치 권한 거부·"앱 사용 중만 허용"·정확도 낮음, 최초 진입 위치 획득 |
| `core/abtest/**`, `RemoteConfig` | 실험 그룹 A/B 양쪽에서 화면 동작 |
| `gradle/libs.versions.toml`, `common.gradle`, `proguard-rules.pro` | release 빌드(R8) 설치·실행 smoke |

## 4. PR 본문에 적는 법

`/3dollars:pr-body`가 TC 섹션을 이렇게 만든다:

```markdown
## TC
- [x] TC-2 — 마커 확대 애니메이션 (자동화) → 증거 marker.mp4   ← 에이전트가 돌리고 증거 첨부
- [ ] TC-7 — 카카오 공유로 진입 (수동, 실기기)                  ← 작성자가 직접
- [x] 공통 — 다크모드 … (자동화) → 증거 dark.png                ← 2절 (UI 파일 있을 때만)
- [ ] 지도 — 마커 탭·클러스터·현위치 이동 … (자동화)             ← 3절 (트리거 매칭 시)
(유닛 테스트 결과는 CI 코멘트 참고)
```

- **`(자동화)` 항목**은 `3dollars:simulator-test`가 PR 올리기 전에 돌고, 체크와 증거 링크를 함께 남긴다. 증거 없는 체크는 체크가 아니다.
- **`(수동)` 항목**은 작성자가 실기기로 확인하고 체크한다.
- 리뷰어는 체크되지 않은 항목이 있으면 머지하지 않는다.
