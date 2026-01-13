# Phase 3 구현 상세 문서

## 1. 개요

### 1.1 Phase 3의 목적
Phase 1에서 확장한 Contract/ViewModel과 Phase 2에서 만든 공통 컴포넌트를 활용하여 
Edit Store의 실제 Compose 화면들을 구현하고 Fragment로 호스팅.

### 1.2 완료된 작업
- `EditStoreTopBar` 수정: 뒤로가기 버튼 지원 추가
- `EditStoreScreen.kt` 생성: 메인 선택 화면 (위치/정보/메뉴 섹션)
- `EditStoreInfoScreen.kt` 생성: 가게 정보 수정 화면
- `EditStoreFragment.kt` 생성: Compose 호스트 Fragment
- 빌드 검증: `assembleDebug` 성공

---

## 2. 수정/생성된 파일

### 2.1 EditStoreComponents.kt (수정)
**경로:** `app/src/main/java/com/zion830/threedollars/ui/edit/ui/compose/EditStoreComponents.kt`

**변경 내용:** EditStoreTopBar에 뒤로가기 버튼 지원 추가

```kotlin
@Composable
fun EditStoreTopBar(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,      // 추가
    onBackClick: (() -> Unit)? = null     // 추가
)
```

**사용 예시:**
```kotlin
// X 닫기 버튼만 (메인 화면)
EditStoreTopBar(
    title = "가게 수정",
    onCloseClick = { /* 닫기 */ }
)

// 뒤로가기 + X 닫기 버튼 (서브 화면)
EditStoreTopBar(
    title = "가게 정보",
    showBackButton = true,
    onBackClick = { /* 뒤로가기 */ },
    onCloseClick = { /* 닫기 */ }
)
```

### 2.2 EditStoreScreen.kt (신규)
**경로:** `app/src/main/java/com/zion830/threedollars/ui/edit/ui/compose/EditStoreScreen.kt`

**화면 구조:**
```
┌─────────────────────────────────┐
│ EditStoreTopBar (X 닫기)        │
├─────────────────────────────────┤
│ EditStoreHeader                 │
│ "제보할 정보를 선택해 주세요"    │
│ "N개의 수정된 정보가 있어요" 배지 │
├─────────────────────────────────┤
│ EditSectionCard - 가게 위치     │
│ EditSectionCard - 가게 정보     │
│ EditSectionCard - 가게 메뉴     │
├─────────────────────────────────┤
│ MainButton (수정 완료)          │
└─────────────────────────────────┘
```

**시그니처:**
```kotlin
@Composable
fun EditStoreScreen(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit,
    modifier: Modifier = Modifier
)
```

**핵심 로직:**
- 3개 섹션 카드 클릭 시 `NavigateToScreen(screen)` Intent 전송
- X 버튼 클릭 시 변경사항 있으면 ExitConfirmDialog 표시
- 수정 완료 버튼: `SubmitEdit(request)` Intent 전송

**헬퍼 함수:**
| 함수 | 용도 |
|-----|------|
| `buildStoreInfoDescription()` | 가게 정보 섹션 설명 텍스트 생성 (Composable) |
| `buildSubmitRequest()` | 제출용 UserStoreModelRequest 생성 |

### 2.3 EditStoreInfoScreen.kt (신규)
**경로:** `app/src/main/java/com/zion830/threedollars/ui/edit/ui/compose/EditStoreInfoScreen.kt`

**화면 구조:**
```
┌─────────────────────────────────┐
│ EditStoreTopBar (← 뒤로가기)    │
├─────────────────────────────────┤
│ 가게 이름                       │
│ EditStoreTextField              │
├─────────────────────────────────┤
│ 가게 형태 (단일 선택)           │
│ [길거리] [매장] [편의점]        │
├─────────────────────────────────┤
│ 결제 방식 *다중선택 가능        │
│ [✓현금] [카드] [계좌이체]       │
├─────────────────────────────────┤
│ 출몰 요일 *다중선택 가능        │
│ (월)(화)(수)(목)(금)(토)(일)    │
├─────────────────────────────────┤
│ 출몰 시간대                     │
│ [시작시간] 부터 [종료시간] 까지 │
├─────────────────────────────────┤
│ MainButton (수정하기)           │
└─────────────────────────────────┘
```

**시그니처:**
```kotlin
@Composable
fun EditStoreInfoScreen(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit,
    modifier: Modifier = Modifier
)
```

**사용된 Intent:**
| UI 요소 | Intent |
|--------|--------|
| 가게 이름 | `UpdateStoreName(name)` |
| 가게 형태 | `UpdateStoreType(type)` |
| 결제 방식 | `TogglePaymentMethod(method)` |
| 출몰 요일 | `ToggleAppearanceDay(day)` |
| 시작 시간 | `UpdateStartTime(time)` |
| 종료 시간 | `UpdateEndTime(time)` |
| 뒤로가기 | `NavigateBack` |

**TimePicker 구현:**
- `ModalBottomSheetLayout` 사용
- `TimePickerBottomSheet` 재활용 (`ui.write.ui.compose` 패키지)
- `TimeType` enum으로 START/END 구분

**내부 컴포넌트:**
| 컴포넌트 | 용도 |
|---------|------|
| `StoreTypeSection` | 가게 형태 단일 선택 (ROAD, STORE, CONVENIENCE_STORE) |
| `PaymentMethodSection` | 결제 방식 다중 선택 (CASH, CARD, ACCOUNT_TRANSFER) |
| `AppearanceDaySection` | 출몰 요일 다중 선택 (월~일) |

### 2.4 EditStoreFragment.kt (신규)
**경로:** `app/src/main/java/com/zion830/threedollars/ui/edit/ui/EditStoreFragment.kt`

**역할:**
- Compose 화면 호스팅
- ViewModel 연결 (activityViewModels)
- Effect 처리 (네비게이션, 토스트 등)
- Fragment Result API로 결과 전달

**시그니처:**
```kotlin
@AndroidEntryPoint
class EditStoreFragment : Fragment() {
    private val editStoreViewModel: EditStoreViewModel by activityViewModels()
    private val storeDetailViewModel: StoreDetailViewModel by activityViewModels()
}
```

**화면 분기 로직:**
```kotlin
when (state.currentScreen) {
    EditScreen.Selection -> EditStoreScreen(...)
    EditScreen.StoreInfo -> EditStoreInfoScreen(...)
    EditScreen.Location -> EditStoreScreen(...)   // Effect로 Fragment 이동
    EditScreen.StoreMenu -> EditStoreScreen(...)  // Effect로 Fragment 이동
}
```

**Effect 처리:**
```kotlin
when (effect) {
    Effect.StoreUpdated -> {
        setFragmentResult(STORE_EDITED_RESULT_KEY, bundleOf(STORE_UPDATED to true))
        storeDetailViewModel.getUserStoreDetail(...)
        popBackStack()
    }
    Effect.NavigateToLocationEdit -> {
        replaceFragment(R.id.container, EditAddressFragment())
    }
    Effect.NavigateBack -> popBackStack()
    Effect.CloseScreen -> popBackStack()
    Effect.ShowError -> showToast(message)
    Effect.ShowToast -> showToast(message)
}
```

**초기화 로직:**
- `initializeStoreData()`: StoreDetailViewModel에서 기존 가게 데이터 로드
- `InitWithStoreData` Intent로 ViewModel에 데이터 전달

---

## 3. 빌드 이슈 및 해결

### 3.1 ic_back 아이콘 미존재
**문제:** `DesignSystemR.drawable.ic_back` 리소스 없음

**해결:** 
```kotlin
// Before
DesignSystemR.drawable.ic_back

// After
DesignSystemR.drawable.back_navigation
```

### 3.2 store_name_hint 문자열 미존재
**문제:** `CommonR.string.store_name_hint` 리소스 없음

**해결:** 기존 문자열 재활용
```kotlin
// Before
stringResource(CommonR.string.store_name_hint)

// After
stringResource(CommonR.string.add_store_name_placeholder)
```

### 3.3 DesignSystemR import 경로 오류
**문제:** `import core.designsystem.R as DesignSystemR` 경로 불일치

**해결:**
```kotlin
// Before
import core.designsystem.R as DesignSystemR

// After
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
```

### 3.4 @Composable 컨텍스트 오류
**문제:** `joinToString` 람다 내부에서 `stringResource` 호출 불가

**해결:** stringResource를 람다 외부로 이동
```kotlin
// Before
state.selectedPaymentMethods.joinToString(", ") { method ->
    when (method.name) {
        "CASH" -> stringResource(CommonR.string.cash)  // 오류!
        ...
    }
}

// After
val cashLabel = stringResource(CommonR.string.cash)
val cardLabel = stringResource(CommonR.string.card)
val bankingLabel = stringResource(CommonR.string.banking)
state.selectedPaymentMethods.joinToString(", ") { method ->
    when (method.name) {
        "CASH" -> cashLabel
        "CARD" -> cardLabel
        "ACCOUNT_TRANSFER" -> bankingLabel
        else -> method.name
    }
}
```

### 3.5 Nullable 타입 처리 오류
**문제:** `menu.name`이 `String?`인데 `isNotEmpty()` 직접 호출

**해결:**
```kotlin
// Before
if (menu.name.isNotEmpty() || menu.price != null) {
    MenuModelRequest(name = menu.name, ...)
}

// After
if (!menu.name.isNullOrEmpty() || menu.price != null) {
    MenuModelRequest(name = menu.name ?: "", ...)
}
```

### 3.6 Import 경로 오류 (Fragment)
**문제:** `getMonthFirstDate`, `replaceFragment` 경로 불일치

**해결:**
```kotlin
// Before
import com.zion830.threedollars.utils.getMonthFirstDate
import zion830.com.common.base.replaceFragment

// After
import com.threedollar.common.ext.getMonthFirstDate
import com.threedollar.common.ext.replaceFragment
```

### 3.7 SelectCategoryModel 패키지 경로 오류
**문제:** `com.home.domain.data.store.SelectCategoryModel` 경로 불일치

**해결:**
```kotlin
// Before
com.home.domain.data.store.SelectCategoryModel(...)

// After
com.threedollar.domain.home.data.store.SelectCategoryModel(...)
```

---

## 4. 데이터 흐름

### 4.1 초기화 흐름
```
StoreDetailViewModel.userStoreDetailModel
        ↓
EditStoreFragment.initializeStoreData()
        ↓
EditStoreContract.Intent.InitWithStoreData
        ↓
EditStoreViewModel.processIntent()
        ↓
EditStoreContract.State 업데이트
        ↓
Compose UI 렌더링
```

### 4.2 사용자 인터랙션 흐름
```
사용자 입력 (UI)
        ↓
onIntent(Intent.XXX)
        ↓
EditStoreViewModel.processIntent()
        ↓
State 업데이트 또는 Effect 발행
        ↓
UI 리컴포지션 또는 Fragment 네비게이션
```

### 4.3 제출 흐름
```
MainButton 클릭
        ↓
buildSubmitRequest(state)
        ↓
Intent.SubmitEdit(request)
        ↓
ViewModel.updateStore()
        ↓
Effect.StoreUpdated
        ↓
Fragment Result + popBackStack()
```

---

## 5. 파일 경로 요약

| 파일 | 경로 | 상태 |
|-----|-----|------|
| EditStoreComponents.kt | `app/.../ui/edit/ui/compose/EditStoreComponents.kt` | 수정 |
| EditStoreScreen.kt | `app/.../ui/edit/ui/compose/EditStoreScreen.kt` | 신규 |
| EditStoreInfoScreen.kt | `app/.../ui/edit/ui/compose/EditStoreInfoScreen.kt` | 신규 |
| EditStoreFragment.kt | `app/.../ui/edit/ui/EditStoreFragment.kt` | 신규 |

---

## 6. 다음 단계 (Phase 4)

### 6.1 위치 수정 화면 연동
- `EditScreen.Location` 선택 시 `EditAddressFragment`로 이동
- 기존 Fragment 재활용, Effect 기반 네비게이션

### 6.2 메뉴 수정 화면 연동
- `EditScreen.StoreMenu` 선택 시 메뉴 수정 화면으로 이동
- 기존 `MenuDetailScreen` 또는 새 Compose 화면 구현 검토

### 6.3 네비게이션 그래프 연결
- `nav_graph.xml` 수정하여 `EditStoreFragment` 연결
- 기존 `EditStoreDetailFragment` 교체 또는 병행

### 6.4 통합 테스트
- 각 화면 간 네비게이션 테스트
- 데이터 초기화 및 변경사항 저장 테스트
- Effect 기반 네비게이션 테스트

---

## 7. 참고 파일

| 파일 | 용도 |
|-----|------|
| `EditStoreContract.kt` | State, Intent, Effect 정의 |
| `EditStoreViewModel.kt` | 비즈니스 로직 처리 |
| `TimePickerBottomSheet` | 시간 선택 UI (재활용) |
| `StoreDetailScreen.kt` | TimePicker 패턴 참고 |
| `EditStoreDetailFragment.kt` | 기존 XML 기반 구현 (참고용) |
