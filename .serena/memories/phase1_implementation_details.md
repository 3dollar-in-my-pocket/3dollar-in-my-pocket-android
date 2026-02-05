# Phase 1 구현 상세 문서

## 1. 개요

### 1.1 Phase 1의 목적
Phase 1은 기존 XML 기반 `EditStoreDetailFragment`를 Jetpack Compose로 마이그레이션하기 위한 **기반 작업**입니다. 
기존 ViewModel과 Contract를 새 패키지로 이동하고, Compose MVI 패턴에 맞게 확장하는 것이 목표입니다.

### 1.2 작업 범위
- 새 `ui/edit/` 패키지 생성
- `EditStoreContract.kt` 확장
- `EditStoreViewModel.kt` 확장
- 기존 참조 업데이트

---

## 2. 패키지 분리 결정 이유

### 2.1 왜 `ui/edit/`를 분리했는가?

**기존 구조의 문제점:**
```
ui/write/
├── ui/
│   ├── AddStoreDetailFragment.kt    # 가게 추가
│   ├── EditStoreDetailFragment.kt   # 가게 수정 (혼재)
│   └── EditAddressFragment.kt       # 주소 수정 (혼재)
└── viewModel/
    ├── AddStoreViewModel.kt         # 가게 추가
    ├── AddStoreContract.kt
    ├── EditStoreViewModel.kt        # 가게 수정 (혼재)
    └── EditStoreContract.kt         # 가게 수정 (혼재)
```

**문제점:**
1. **책임 혼재**: "write" 패키지에 "add"와 "edit" 기능이 함께 있음
2. **유지보수 어려움**: 가게 추가/수정 코드가 섞여 있어 수정 시 영향 범위 파악이 어려움
3. **네이밍 혼란**: "write"가 "추가"인지 "수정"인지 불명확

**분리 후 구조:**
```
ui/write/                              # 가게 추가 전용
├── ui/
│   ├── AddStoreDetailFragment.kt
│   └── NewAddressFragment.kt
└── viewModel/
    ├── AddStoreViewModel.kt
    └── AddStoreContract.kt

ui/edit/                               # 가게 수정 전용 (NEW)
├── ui/
│   ├── EditStoreFragment.kt          # Phase 3에서 생성
│   ├── EditAddressFragment.kt        # 이동 예정
│   └── compose/
│       ├── EditStoreScreen.kt        # Phase 3에서 생성
│       └── EditStoreInfoScreen.kt    # Phase 3에서 생성
└── viewModel/
    ├── EditStoreViewModel.kt
    └── EditStoreContract.kt
```

**이점:**
1. **명확한 책임 분리**: 각 패키지가 단일 기능에 집중
2. **독립적 개발**: edit 패키지 수정이 write 패키지에 영향 주지 않음
3. **Compose 마이그레이션 용이**: edit만 Compose로 전환 가능

---

## 3. EditStoreContract 확장 상세

### 3.1 State 확장 이유

#### 3.1.1 `currentScreen: EditScreen`
```kotlin
val currentScreen: EditScreen = EditScreen.Selection
```

**왜 필요한가?**
- 기존: Fragment 전환으로 화면 이동 관리
- Compose: 단일 Composable 내에서 when으로 화면 분기
- ViewModel이 현재 화면 상태를 관리해야 Compose에서 적절히 렌더링 가능

**EditScreen enum:**
```kotlin
enum class EditScreen {
    Selection,      // 메인 선택 화면 (가게 위치/정보/메뉴 선택)
    Location,       // 가게 위치 수정 (EditAddressFragment - 기존 유지)
    StoreInfo,      // 가게 정보 수정 (새 Compose 화면)
    StoreMenu       // 가게 메뉴 수정 (MenuDetailScreen 재활용)
}
```

#### 3.1.2 `originalStoreData: OriginalStoreData?`
```kotlin
data class OriginalStoreData(
    val storeName: String,
    val storeType: String?,
    val location: LatLng?,
    val address: String,
    val paymentMethods: Set<PaymentType>,
    val appearanceDays: Set<DayOfTheWeekType>,
    val openingHours: OpeningHourRequest,
    val categories: List<SelectCategoryModel>
)
```

**왜 필요한가?**
- Figma 디자인에서 "N개의 수정된 정보가 있어요" 배지 표시 필요
- 원본 데이터와 현재 데이터 비교하여 변경 여부 추적 필요
- 사용자가 화면을 오가도 원본 데이터 유지 필요

**사용 예시:**
```kotlin
private fun checkInfoChanges(state: State): Boolean {
    val original = state.originalStoreData ?: return false
    return state.storeName != original.storeName ||
           state.storeType != original.storeType ||
           state.selectedPaymentMethods != original.paymentMethods ||
           state.selectedDays != original.appearanceDays ||
           state.openingHours != original.openingHours
}
```

#### 3.1.3 변경 추적 플래그
```kotlin
val hasLocationChanges: Boolean = false
val hasInfoChanges: Boolean = false
val hasMenuChanges: Boolean = false
```

**왜 3개로 분리했는가?**
- Figma 디자인: 각 섹션(위치/정보/메뉴)별로 수정 여부 표시
- 배지에 "N개의 수정된 정보가 있어요" 표시 (N = 변경된 섹션 수)
- 각 카드에 체크 아이콘으로 수정 완료 여부 표시

**파생 상태로 통합 계산:**
```kotlin
val totalChangedCount: Int
    get() = listOf(hasLocationChanges, hasInfoChanges, hasMenuChanges).count { it }

val hasAnyChanges: Boolean
    get() = hasLocationChanges || hasInfoChanges || hasMenuChanges
```

#### 3.1.4 `showExitConfirmDialog: Boolean`
```kotlin
val showExitConfirmDialog: Boolean = false
```

**왜 State에 포함했는가?**
- Compose에서 다이얼로그는 State 기반으로 표시/숨김
- `if (state.showExitConfirmDialog) { ExitConfirmDialog() }`
- ViewModel에서 다이얼로그 상태 관리로 테스트 용이성 확보

#### 3.1.5 `selectedCategoryId: String?`
```kotlin
val selectedCategoryId: String? = null
```

**왜 필요한가?**
- `MenuDetailScreen` 재활용 시 현재 선택된 카테고리 추적 필요
- 메뉴 추가/수정/삭제 시 어떤 카테고리인지 식별 필요

### 3.2 파생 상태(Computed Properties) 사용 이유

```kotlin
val menuCount: Int
    get() = selectCategoryList.sumOf { it.menuDetail?.size ?: 0 }

val isSubmitEnabled: Boolean
    get() = hasAnyChanges && !isLoading
```

**왜 파생 상태를 사용했는가?**
1. **중복 제거**: 여러 곳에서 같은 계산 로직 반복 방지
2. **일관성**: 항상 최신 상태에서 계산되어 불일치 방지
3. **가독성**: UI 코드에서 `state.menuCount`로 간단히 접근
4. **테스트 용이성**: 상태 변경 시 파생 값 자동 갱신 검증 가능

---

## 4. Intent 확장 상세

### 4.1 화면 네비게이션 Intent

```kotlin
data class NavigateToScreen(val screen: EditScreen) : Intent()
data object NavigateBack : Intent()
```

**왜 Intent로 처리했는가?**
- MVI 패턴: 모든 사용자 액션은 Intent로 표현
- UI는 `onIntent(NavigateToScreen(EditScreen.StoreInfo))` 호출
- ViewModel이 상태 변경 또는 Effect 발생 결정

**NavigateToScreen 처리 로직:**
```kotlin
private fun navigateToScreen(screen: EditScreen) {
    if (screen == EditScreen.Location) {
        // Location은 기존 Fragment 사용 → Effect로 네비게이션
        viewModelScope.launch {
            _effect.emit(Effect.NavigateToLocationEdit)
        }
    } else {
        // 나머지는 Compose 내부 화면 전환 → State 변경
        _state.update { it.copy(currentScreen = screen) }
    }
}
```

### 4.2 가게 정보 수정 Intent

```kotlin
data class UpdateStoreName(val name: String) : Intent()
data class UpdateStoreType(val type: String) : Intent()
data class TogglePaymentMethod(val method: PaymentType) : Intent()
data class ToggleAppearanceDay(val day: DayOfTheWeekType) : Intent()
data class UpdateStartTime(val time: String?) : Intent()
data class UpdateEndTime(val time: String?) : Intent()
```

**왜 각각 분리했는가?**
1. **단일 책임**: 각 Intent가 하나의 필드만 수정
2. **변경 추적 용이**: 각 수정 시 `hasInfoChanges` 업데이트 로직 명확
3. **테스트 용이성**: 개별 Intent 테스트 가능
4. **디버깅 용이**: 로그에서 어떤 변경이 발생했는지 추적 가능

**Toggle 패턴 사용 이유:**
```kotlin
data class TogglePaymentMethod(val method: PaymentType) : Intent()
```
- 결제 방식, 출몰 요일은 다중 선택
- Toggle로 선택/해제를 단일 Intent로 처리
- `AddPaymentMethod` + `RemovePaymentMethod` 분리보다 간결

### 4.3 메뉴 수정 Intent

```kotlin
data class AddMenuToCategory(val categoryId: String) : Intent()
data class RemoveMenuFromCategory(val categoryId: String, val menuIndex: Int) : Intent()
data class UpdateMenuInCategory(
    val categoryId: String,
    val menuIndex: Int,
    val name: String,
    val price: String
) : Intent()
```

**왜 categoryId + menuIndex 조합을 사용했는가?**
- 카테고리별로 메뉴 리스트가 분리되어 있음
- `SelectCategoryModel.menuDetail: List<UserStoreMenuModel>`
- 특정 카테고리 내 특정 메뉴를 식별하려면 두 정보 모두 필요

**MenuDetailScreen 재활용을 위한 설계:**
```kotlin
// MenuDetailScreen에서 콜백으로 Intent 전달
MenuDetailScreen(
    selectCategoryList = state.selectCategoryList,
    onMenuAdd = { categoryId -> 
        onIntent(Intent.AddMenuToCategory(categoryId)) 
    },
    onMenuUpdate = { categoryId, index, name, price -> 
        onIntent(Intent.UpdateMenuInCategory(categoryId, index, name, price))
    }
)
```

---

## 5. Effect 확장 상세

### 5.1 기존 Effect 유지

```kotlin
data object StoreUpdated : Effect()
data class ShowError(val message: String) : Effect()
```

**유지 이유:**
- 기존 `EditStoreDetailFragment`에서 이미 사용 중
- API 호출 성공/실패 시 동일한 처리 필요

### 5.2 새로 추가된 Effect

```kotlin
data class ShowToast(val message: String) : Effect()
data object NavigateToLocationEdit : Effect()
data object NavigateBack : Effect()
data object CloseScreen : Effect()
```

**각 Effect의 역할:**

| Effect | 발생 시점 | 처리 방법 |
|--------|----------|----------|
| `ShowToast` | 사용자에게 메시지 표시 필요 시 | Toast 표시 |
| `NavigateToLocationEdit` | Location 화면 선택 시 | EditAddressFragment로 이동 |
| `NavigateBack` | Selection 화면에서 뒤로가기 | popBackStack() |
| `CloseScreen` | 다이얼로그에서 "나가기" 선택 | popBackStack() |

**왜 Effect로 처리했는가?**
- 네비게이션, Toast는 **일회성 이벤트**
- State에 넣으면 화면 재구성 시 중복 실행 위험
- SharedFlow로 한 번만 소비되도록 보장

---

## 6. ViewModel 확장 상세

### 6.1 변경 추적 로직

```kotlin
private fun updateStoreName(name: String) {
    _state.update { currentState ->
        val newState = currentState.copy(storeName = name)
        newState.copy(hasInfoChanges = checkInfoChanges(newState))
    }
}
```

**왜 이렇게 구현했는가?**
1. **즉각적 반영**: 필드 변경 즉시 `hasInfoChanges` 업데이트
2. **정확한 비교**: 새 상태를 만든 후 원본과 비교
3. **불변성 유지**: `copy()`로 새 객체 생성

**checkInfoChanges 로직:**
```kotlin
private fun checkInfoChanges(state: State): Boolean {
    val original = state.originalStoreData ?: return false
    return state.storeName != original.storeName ||
           state.storeType != original.storeType ||
           state.selectedPaymentMethods != original.paymentMethods ||
           state.selectedDays != original.appearanceDays ||
           state.openingHours != original.openingHours
}
```

**왜 모든 필드를 비교하는가?**
- 사용자가 A를 수정했다가 원래 값으로 되돌릴 수 있음
- 이 경우 `hasInfoChanges`는 false가 되어야 함
- 개별 필드만 비교하면 이 케이스를 놓칠 수 있음

### 6.2 메뉴 CRUD 로직

```kotlin
private fun addMenuToCategory(categoryId: String) {
    _state.update { currentState ->
        val updatedList = currentState.selectCategoryList.map { category ->
            if (category.menuType.categoryId == categoryId) {
                val currentMenus = category.menuDetail?.toMutableList() ?: mutableListOf()
                currentMenus.add(UserStoreMenuModel())
                category.copy(menuDetail = currentMenus)
            } else {
                category
            }
        }
        val newState = currentState.copy(selectCategoryList = updatedList)
        newState.copy(hasMenuChanges = checkMenuChanges(newState))
    }
}
```

**왜 이렇게 복잡한가?**
1. **불변 리스트 처리**: Kotlin의 List는 불변, map으로 새 리스트 생성
2. **특정 카테고리만 수정**: categoryId 일치하는 것만 수정
3. **변경 추적**: 수정 후 `hasMenuChanges` 업데이트

### 6.3 InitWithStoreData 수정

```kotlin
private fun initWithStoreData(intent: Intent.InitWithStoreData) {
    _state.update {
        it.copy(
            // 기존 필드 설정
            storeId = intent.storeId,
            storeName = intent.storeName,
            // ...
            
            // 원본 데이터 저장 (NEW)
            originalStoreData = OriginalStoreData(
                storeName = intent.storeName,
                storeType = intent.storeType,
                location = intent.location,
                address = intent.address,
                paymentMethods = intent.paymentMethods,
                appearanceDays = intent.appearanceDays,
                openingHours = intent.openingHours,
                categories = intent.categories
            )
        )
    }
}
```

**address 파라미터 추가 이유:**
- 기존 Intent에는 address가 없었음
- 위치 변경 추적을 위해 원본 주소도 저장 필요
- `EditStoreDetailFragment`에서 `storeDetail.store.address?.fullAddress` 전달

---

## 7. 기존 코드 수정 상세

### 7.1 import 경로 수정

**수정된 파일 목록:**
1. `EditStoreDetailFragment.kt`
2. `EditAddressFragment.kt`
3. `AddStoreMenuCategoryDialogFragment.kt`
4. `StoreAddNaverMapFragment.kt`

**수정 내용:**
```kotlin
// Before
import com.zion830.threedollars.ui.write.viewModel.EditStoreContract
import com.zion830.threedollars.ui.write.viewModel.EditStoreViewModel

// After
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.edit.viewModel.EditStoreViewModel
```

### 7.2 Effect 처리 확장

**EditStoreDetailFragment.kt:**
```kotlin
editStoreViewModel.effect.collect { effect ->
    when (effect) {
        // 기존 Effect
        is Effect.StoreUpdated -> { ... }
        is Effect.ShowError -> { ... }
        
        // 새 Effect (빈 처리 또는 기본 동작)
        is Effect.ShowToast -> { showToast(effect.message) }
        is Effect.NavigateToLocationEdit -> { /* Compose에서 처리 */ }
        is Effect.NavigateBack -> { requireActivity().supportFragmentManager.popBackStack() }
        is Effect.CloseScreen -> { requireActivity().supportFragmentManager.popBackStack() }
    }
}
```

**왜 빈 처리도 추가했는가?**
- Kotlin의 `when`은 exhaustive 검사
- 모든 sealed class 하위 타입 처리 필수
- 빌드 에러 방지

---

## 8. 결론

### 8.1 Phase 1 완료 상태

| 항목 | 상태 | 비고 |
|-----|------|-----|
| 패키지 분리 | ✅ 완료 | `ui/edit/viewModel/` 생성 |
| Contract 확장 | ✅ 완료 | State, Intent, Effect 확장 |
| ViewModel 확장 | ✅ 완료 | 새 Intent 처리 로직 추가 |
| 참조 업데이트 | ✅ 완료 | 4개 파일 import 수정 |
| 빌드 검증 | ✅ 완료 | assembleDebug 성공 |

### 8.2 Phase 2-3를 위한 준비 완료

Phase 1 완료로 다음이 가능해짐:
1. **Compose 화면에서 State 구독**: `state.collectAsStateWithLifecycle()`
2. **Intent로 액션 전달**: `onIntent(Intent.UpdateStoreName("새 이름"))`
3. **Effect로 네비게이션**: `NavigateToLocationEdit` 발생 시 Fragment 이동
4. **변경 추적**: `state.hasAnyChanges`, `state.totalChangedCount` 활용

### 8.3 다음 단계

- **Phase 2**: 공통 컴포넌트 구현 (`EditStoreComponents.kt`)
- **Phase 3**: Compose 화면 구현 (`EditStoreScreen.kt`, `EditStoreInfoScreen.kt`)
- **Phase 4**: 통합 및 테스트
