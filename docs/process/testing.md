# 테스트 작성 가이드

테스트는 **diff가 아니라 의도 문서(테크스펙)의 TC에서 도출한다.** AI가 코드를 쓰더라도 "무엇을 검증할지"는 사람이 TC 목록으로 먼저 승인하고, 코드는 그 다음이다.
프로세스 전체 그림은 `docs/process/pr-process.md`, 테크스펙은 `docs/process/tech-spec-process.md`, 변경 유형별 최소 검증은 `docs/context/verification-matrix.md`.

## 흐름

```
노션 테크스펙 TC-1..n
   │  /3dollars:test-cases  (브랜치명 → 티켓 키 → 지라 `테크스펙` 필드 → 노션)
   ▼
TC별 계층 배정 (유닛 / 자동화 / 수동)  ──▶ 사람 승인
   │              │                  │
   ▼              ▼                  ▼
유닛 테스트 코드   simulator-test     PR 본문 체크박스
   │            (에뮬레이터)          │
   ▼              ▼                  ▼
CI 커버리지 표    스크린샷·영상 증거    작성자 체크
```

## 테스트의 세 계층

| 계층 | 무엇을 검증 | 실행 주체·시점 | 증거 |
|---|---|---|---|
| **1. 유닛 테스트 코드** | ViewModel/State 전이, mapper·formatter·query builder, 응답 DTO 파싱 | `./gradlew testDebugUnitTest` — CI가 PR마다 | CI 코멘트의 TC 커버리지 표 |
| **2. 자동화 테스트 TC** | 에뮬레이터를 에이전트가 조작해 화면을 실제로 거치는 E2E. 탭·스와이프·딥링크를 넣고 스크린샷·영상으로 판정 | `3dollars:simulator-test` — PR 올리기 전, 이번 티켓 TC만 | PR 본문 "증거" 섹션의 스크린샷·영상 |
| **3. 수동 테스트 TC** | 에뮬레이터로 상황 자체를 만들 수 없어 실기기·사람이 필요한 것 | 작성자 — PR 올리기 전 | PR 본문 체크박스 |

- 테크스펙 TC 하나는 **반드시 셋 중 하나 이상**에 배정된다. 어디에도 없는 TC가 있으면 PR을 열 수 없다.
- **위 계층을 먼저 쓴다.** 유닛으로 덮이면 유닛, 안 되면 자동화, 그것도 안 되면 수동. 계층을 내릴 때는 이유가 있어야 한다.
- 한 TC가 두 계층에 걸쳐도 된다 (로그 파라미터는 유닛, 화면 전환은 자동화).
- **2와 3의 경계는 "에뮬레이터로 그 상황을 만들 수 있는가" 하나다.** 판정이 사람 눈이어야 한다는 건 2번이지 3번이 아니다 — 조작은 에이전트가 하고 스크린샷·영상을 증거로 남긴다.

### 무엇이 어느 계층인가

| 대상 | 계층 | 이유 |
|---|---|---|
| ViewModel State/UiEvent 전이, 로그 파라미터 | 1 유닛 | ViewModel 경계에서 단언된다 |
| 서버 JSON → DTO → domain model 매핑, query param 조립 | 1 유닛 | 순수 함수 (`:data`, `:domain`) |
| 서버드리븐 섹션 매퍼, 포맷터, 치수 상수 | 1 유닛 | 이미 `app/src/test`, `data/src/test`에 선례가 있다 |
| 화면 진입·전환, 버튼 탭 후 결과, 목록 갱신 | 2 자동화 | 실제 화면을 거쳐야 의미가 있다 |
| 애니메이션·제스처, 바텀시트 스냅 | 2 자동화 | 영상으로 판정 |
| 지도(네이버맵) 마커·클러스터·카메라 | 2 자동화 | 스크린샷으로 판정 |
| 런타임 권한 팝업 | 2 자동화 | `adb shell pm revoke` 후 재현 |
| 푸시 수신·딥링크 진입 | 2 자동화 | FCM 테스트 발송 / `adb shell am start -a android.intent.action.VIEW -d "…"` |
| 다크모드·기기 폭·긴 텍스트·빈 데이터·오프라인 | 2 자동화 | 에뮬레이터 설정(`adb shell cmd uimode night yes`, `svc data disable`)으로 상태를 만든다 |
| 카카오 로그인·공유 | 3 수동 | 에뮬레이터에 카카오톡·계정이 없다 |
| 애드몹 실광고, 인앱 리뷰·결제, 플레이스토어 이동 | 3 수동 | 에뮬레이터에 기능 자체가 없거나 Play 서비스 제약 |

세부 항목과 화면 변경 PR 공통 체크는 `docs/process/e2e-and-manual-tests.md`.

## 1. 유닛 테스트 코드

### 위치

| 모듈 | 검증 대상 | 위치 | 대표 예 |
|---|---|---|---|
| `:app` | 화면 로직, ViewModel/State, 포맷터, 레이아웃 상수 | `app/src/test/java/com/zion830/threedollars/**` | `HomeSheetLayoutTest`, `EditStoreContractStateTest` |
| `:data` | 응답 DTO 파싱, mapper | `data/src/test/java/com/threedollar/data/**` | `HomeBottomSheetScreenMapperTest`, `MyReviewResponseV2Test` |
| `:domain` | domain model 규칙 | `domain/src/test/java/com/threedollar/domain/**` | `StoreDisplayItemModelTest` |
| `:core:*` | 공통 유틸·네트워크 계약 | `core/*/src/test/**` | — |

- Activity/Fragment/Composable 자체는 테스트하지 않는다. 화면 로직은 ViewModel·State·순수 함수로 내려서 테스트한다.
- `androidTest`(instrumented)는 CI에서 돌리지 않는다. 기기가 필요한 검증은 **2번 계층(자동화 TC)** 이 대신한다.
- **새 모듈에 테스트를 추가할 땐 그 모듈의 `build.gradle.kts` 에 테스트 의존성이 있는지 먼저 확인한다.** kapt(Hilt)를 쓰는 모듈에 JUnit 없이 `@Test` 파일만 있으면 `kaptDebugUnitTestKotlin` 이 `NonExistentClass` 로 실패해 **전체 `testDebugUnitTest` 가 깨진다.** 의존성 추가는 승인 대상이므로(`AGENTS.md`) 먼저 묻는다.

### 네이밍

테스트 메서드명은 **`` `TH{티켓}_TC{n}_{조건}_{기대결과}` ``** — 백틱 함수명, 한글 허용. 티켓 키는 하이픈을 뺀다 (`TH-1340` → `TH1340`).

```kotlin
class HomeSheetLayoutTest {

    // TH-1340 TC1
    @Test
    fun `TH1340_TC1_접힌시트면_peek높이가_293dp다`() {
        // Given
        val layout = HomeSheetLayout
        // When
        val peek = layout.COLLAPSED_PEEK_HEIGHT_DP
        // Then
        assertEquals(293f, peek, 0f)
    }
}
```

- **TC 번호는 테크스펙(티켓) 안에서 유일하다. 노션 테크스펙의 `TC-n`을 그대로 가져다 쓴다.**
  - 모듈·파일이 갈라져도 번호는 스펙 순서 그대로다. **파일마다 1부터 다시 시작하지 않는다.**
  - 티켓 키가 네임스페이스라, 한 클래스에 여러 티켓의 테스트가 쌓여도 번호가 충돌하지 않는다.
  - 테크스펙에 없는 케이스를 테스트로 만들고 싶으면 **테크스펙에 TC를 먼저 추가**하고 그 번호를 쓴다. 코드가 스펙보다 앞서가지 않는다.
  - 테크스펙이 없는 티켓(버그·태스크)은 티켓 안에서 1부터 순서대로 붙인다. 이때 번호의 원본은 PR 본문이다.
- TC와 무관한 회귀 테스트는 `` `회귀_…` `` 접두.
- **기존 테스트(영문 백틱 설명형)는 그대로 둔다.** 새로 쓰는 TC 도출 테스트만 이 규칙을 따른다.
- `// TH-1234 TC1` 주석으로 묶고, Given / When / Then 주석 3개를 반드시 쓴다.

### 실행

```bash
# 전체
./gradlew testDebugUnitTest

# 모듈만
./gradlew :app:testDebugUnitTest
./gradlew :data:testDebugUnitTest

# 클래스·메서드만
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.ui.HomeSheetLayoutTest"
```

### 목·픽스처 규칙

- 가상 데이터를 손으로 만들지 말고 **실서버 응답**을 쓴다. 캡처한 JSON을 `*/src/test/resources/{화면}{상황}.json`으로 저장하고 `{ok, data}` 래퍼는 벗겨 `data`만 둔다.
- 서버가 안 내려주는 분기(알 수 없는 enum 값, 필드 누락)는 실데이터를 복사해 값만 바꾼 파일을 별도로 둔다.
- 알 수 없는 enum 값이 와도 크래시 없이 기본값으로 떨어지는지는 **필수 단언**이다. 서버가 값을 추가해도 구버전 앱이 죽지 않아야 한다.
- 테스트 파일 안에 private fake를 만들지 않는다. 공용으로 빼야 다음 테스트가 재사용한다.

### CI (PR 증거)

`.github/workflows/test.yml`이 PR마다 `./gradlew testDebugUnitTest`를 돌리고:
- `scripts/test-summary.sh`로 JUnit XML 결과를 마크다운(전체 결과 / 실패 목록 / **TC 커버리지 표**)으로 만들어 **PR 코멘트(갱신형)** 와 Job Summary에 붙인다
- 테스트 리포트를 아티팩트로 올린다(14일)
- 실패한 테스트가 있으면 체크가 빨간불

TC 커버리지 표는 메서드명 `TH{티켓}_TC{n}_` 접두로 뽑아 티켓별·스펙 번호순으로 정렬한다. 그래서 네이밍 규칙이 곧 증거 규칙이다. 스펙 TC 중 표에 없는 번호가 곧 미커버 TC다.
스크린샷 테스트(Paparazzi 등)는 쓰지 않는다. UI 회귀는 2번 계층(자동화 TC)이 대신한다.

## 2. 자동화 테스트 TC

에뮬레이터를 에이전트가 직접 조작해 TC 조건을 만들고, 스크린샷·영상으로 판정한다. 실행은 `3dollars:simulator-test`.

- **테스트 코드를 만들지 않는다.** Espresso/UI Automator도 쓰지 않는다. 산출물은 코드가 아니라 증거다.
- **시나리오의 원본은 테크스펙 TC 한 줄이다.** Given/When/Then을 그대로 스킬에 넘긴다. 재현 단계를 레포에 따로 파일로 두지 않는다 (스펙과 이중 관리가 되면 둘 다 썩는다).
- **언제**: PR 올리기 전, **이번 티켓의 자동화 TC만** 돌린다. 과거 TC 회귀는 기본으로 돌리지 않는다.
- **판정**: 스킬이 케이스마다 `PASS` / `FAIL` / `UNCLEAR` / `BLOCKED`를 매긴다. **UNCLEAR·BLOCKED는 통과가 아니다** — 재현 경로를 고치거나 수동 TC로 내린다.
- **증거**: 스크린샷·영상을 PR 본문 "증거" 섹션에 TC 번호와 함께 붙인다. 파일은 레포의 `verification-assets` prerelease에 올려 링크한다 (`gh release upload verification-assets <파일> --clobber`).

에뮬레이터 함정 두 가지는 매번 걸린다:

```bash
# 1) 멀티 디스플레이 AVD는 exec-out screencap이 깨진다 — display id를 명시
adb shell dumpsys SurfaceFlinger --display-id
adb shell screencap -d <id> -p /sdcard/shot.png && adb pull /sdcard/shot.png

# 2) 죽은 프록시가 남아 있으면 스플래시에서 멈춘다
adb shell settings put global http_proxy :0
```

```
| TC | 시나리오 | 결과 | 증거 |
|---|---|---|---|
| TC-3 | 전체화면에서 "지도 보기" 탭 → 시트가 접힘 | ✅ PASS | before.png / after.png |
| TC-5 | 시트 드래그 중 버튼 알파 연속 변화 | ✅ PASS | drag.mp4 |
```

## 3. 수동 테스트 TC

에뮬레이터로 **상황 자체를 만들 수 없는 것만** 남는다 (카카오 로그인·공유, 애드몹 실광고, 인앱 리뷰·결제, 플레이스토어 이동, 실기기 푸시).
목록과 화면 변경 PR 공통 체크 항목은 `docs/process/e2e-and-manual-tests.md`.
PR 본문에 체크박스로 넣고 **작성자가 올리기 전에 체크**한다. 리뷰어는 미체크 항목이 있으면 머지하지 않는다.

## PR에 남기는 것

`/3dollars:test-cases`가 아래 표를 만들어 PR 본문에 넣는다. 사람은 이 표만 본다.

| 티켓 | TC | 계층 | 테스트 / 증거 | 결과 |
|---|---|---|---|---|
| TH-1340 | TC1 | 유닛 | `` `TH1340_TC1_접힌시트면_peek높이가_293dp다` `` (HomeSheetLayoutTest) | ✅ |
| TH-1340 | TC3 | 자동화 | 마커 탭 → 미리보기 시트 (marker.mp4) | ✅ PASS |
| TH-1340 | TC7 | 수동 | 카카오 공유로 진입 (실기기) | ☐ 체크리스트 |
