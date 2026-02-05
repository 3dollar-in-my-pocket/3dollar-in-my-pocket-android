# Edit Store 리팩토링 계획서

## 1. 개요

### 1.1 목적
기존 XML 기반 `EditStoreDetailFragment`를 Jetpack Compose로 마이그레이션하여 Figma 디자인에 맞게 리팩토링

### 1.2 관련 Figma 디자인
- **메인 화면 (수정할 정보 선택)**: `node-id=6816-26612`
- **수정된 정보 표시**: `node-id=6816-29819`
- **닫기 확인 다이얼로그**: `node-id=6816-27933`
- **가게 정보 수정 화면**: `node-id=6816-27119`
- **전체 섹션**: `node-id=6817-11504`

---

## 2. 기존 코드 분석

### 2.1 현재 파일 구조
```
app/src/main/java/com/zion830/threedollars/ui/write/
├── ui/
│   ├── EditStoreDetailFragment.kt    # XML 기반 (리팩토링 대상)
│   ├── EditAddressFragment.kt        # 주소 수정 화면 (유지)
│   └── compose/
│       └── MenuDetailScreen.kt       # Compose 메뉴 화면 (재활용)
└── viewModel/
    ├── EditStoreViewModel.kt         # MVI 패턴 ViewModel
    └── EditStoreContract.kt          # State/Intent/Effect 정의
```

### 2.2 현재 EditStoreContract.State
```kotlin
data class State(
    val storeId: Int = 0,
    val storeName: String = "",
    val storeType: String? = null,
    val address: String = "",
    val selectedLocation: LatLng? = null,
    val tempLocation: LatLng? = null,
    val selectCategoryList: List<SelectCategoryModel> = emptyList(),
    val availableSnackCategories: List<CategoryModel> = emptyList(),
    val availableMealCategories: List<CategoryModel> = emptyList(),
    val selectedPaymentMethods: Set<PaymentType> = emptySet(),
    val selectedDays: Set<DayOfTheWeekType> = emptySet(),
    val openingHours: OpeningHourRequest = OpeningHourRequest(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isInitialized: Boolean = false
)
```

---

## 3. 신규 화면 구조 (Figma 기반)

### 3.1 메인 화면 - 수정할 정보 선택
**디자인 특징:**
- 상단 네비게이션: "가게 정보 수정" 타이틀 + X 닫기 버튼
- 헤더: "제보할 정보를 선택해 주세요" + 부제
- 수정된 정보가 있을 때 녹색 배지: "N개의 수정된 정보가 있어요"
- 3개의 카드 (클릭 가능):
  1. **가게 위치** - 지도 아이콘, 현재 주소 표시
  2. **가게 정보** - 메가폰 아이콘, 상세 정보 (이름, 형태, 결제방식, 출몰요일, 출몰시간)
  3. **가게 메뉴** - 포크나이프 아이콘, 메뉴 개수 표시
- 하단 버튼: "수정 완료" (핑크색 #FF858F)

### 3.2 가게 정보 수정 화면
**입력 항목:**
- 가게 이름: 텍스트 입력 (회색 배경 #F4F4F4, radius 8dp)
- 가게 형태: 단일 선택 (길거리, 매장, 푸드트럭, 편의점)
- 결제 방식: 다중 선택 (현금, 카드, 계좌이체) - 체크박스 포함
- 출몰 요일: 다중 선택 (월~일) - 원형 버튼
- 출몰 시간대: 시작시간 ~ 종료시간 선택
- 하단 버튼: "수정하기"

### 3.3 닫기 확인 다이얼로그
- 제목: "다음에 할까요?"
- 설명: "수정된 정보가 있어요. 지금까지 입력한 정보가 저장되지 않아요."
- 버튼: "닫기" (회색 테두리), "나가기" (빨강 #FF5C43)

---

## 4. 리팩토링 계획

### 4.1 새로운 패키지 구조

**기존 write 패키지 (가게 추가) - 정리 후:**
```
app/src/main/java/com/zion830/threedollars/ui/write/
├── adapter/
│   ├── AddCategoryRecyclerAdapter.kt
│   └── PhotoRecyclerAdapter.kt
├── ui/
│   ├── AddStoreDetailFragment.kt
│   ├── NewAddressFragment.kt
│   ├── BossDownloadDialog.kt
│   └── compose/
│       ├── AddStoreFlowScreen.kt
│       ├── RequiredInfoScreen.kt
│       ├── MenuCategoryScreen.kt
│       ├── MenuDetailScreen.kt        # ⚠️ edit에서도 import하여 재활용
│       ├── StoreDetailScreen.kt
│       └── CompletionScreen.kt
└── viewModel/
    ├── AddStoreViewModel.kt
    └── AddStoreContract.kt
```

**신규 edit 패키지 (가게 수정) - 분리:**
```
app/src/main/java/com/zion830/threedollars/ui/edit/
├── ui/
│   ├── EditStoreFragment.kt           # NEW: Compose 호스트 Fragment
│   ├── EditAddressFragment.kt         # write에서 이동
│   └── compose/
│       ├── EditStoreScreen.kt         # NEW: 메인 선택 화면
│       ├── EditStoreInfoScreen.kt     # NEW: 가게 정보 수정 화면
│       └── EditStoreComponents.kt     # NEW: 공통 컴포넌트
└── viewModel/
    ├── EditStoreViewModel.kt          # write에서 이동 + 확장
    └── EditStoreContract.kt           # write에서 이동 + 확장
```

**공유 컴포넌트:**
- `write/ui/compose/MenuDetailScreen.kt` → edit에서 import하여 재활용
- 필요시 `core:ui` 모듈로 공통 컴포넌트 이동 검토

### 4.2 파일 이동 및 생성 계획

| 작업 | 파일 | 설명 |
|-----|-----|------|
| **이동** | `EditStoreViewModel.kt` | `write/viewModel` → `edit/viewModel` |
| **이동** | `EditStoreContract.kt` | `write/viewModel` → `edit/viewModel` |
| **이동** | `EditAddressFragment.kt` | `write/ui` → `edit/ui` |
| **삭제** | `EditStoreDetailFragment.kt` | 새 EditStoreFragment로 대체 |
| **삭제** | `EditCategoryMenuRecyclerAdapter.kt` | Compose로 대체 |
| **삭제** | `EditMenuRecyclerAdapter.kt` | Compose로 대체 |
| **생성** | `EditStoreFragment.kt` | Compose 호스트 Fragment |
| **생성** | `EditStoreScreen.kt` | 메인 선택 화면 |
| **생성** | `EditStoreInfoScreen.kt` | 가게 정보 수정 화면 |
| **생성** | `EditStoreComponents.kt` | 공통 컴포넌트 |

### 4.3 Navigation 업데이트

기존 `EditStoreDetailFragment` 참조를 `EditStoreFragment`로 변경 필요:
- `nav_graph.xml` 업데이트
- Deep link 경로 유지
- Fragment Result API 키 유지 (`STORE_EDITED_RESULT_KEY`)

### 4.4 Contract 확장 계획

#### 4.4.1 State 확장
```kotlin
data class State(
    // 기존 필드 유지
    val storeId: Int = 0,
    val storeName: String = "",
    val storeType: String? = null,
    val selectedLocation: LatLng? = null,
    val tempLocation: LatLng? = null,
    val selectCategoryList: List<SelectCategoryModel> = emptyList(),
    val availableSnackCategories: List<CategoryModel> = emptyList(),
    val availableMealCategories: List<CategoryModel> = emptyList(),
    val selectedPaymentMethods: Set<PaymentType> = emptySet(),
    val selectedDays: Set<DayOfTheWeekType> = emptySet(),
    val openingHours: OpeningHourRequest = OpeningHourRequest(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isInitialized: Boolean = false,
    
    // NEW: 현재 화면 상태
    val currentScreen: EditScreen = EditScreen.Selection,
    
    // NEW: 수정 추적
    val originalStoreData: OriginalStoreData? = null,
    val hasLocationChanges: Boolean = false,
    val hasInfoChanges: Boolean = false,
    val hasMenuChanges: Boolean = false,
    
    // NEW: 다이얼로그 상태
    val showExitConfirmDialog: Boolean = false,
    
    // NEW: 메뉴 관련 (MenuDetailScreen 재활용)
    val selectedCategoryId: String? = null
) {
    val totalChangedCount: Int
        get() = listOf(hasLocationChanges, hasInfoChanges, hasMenuChanges).count { it }
    
    val hasAnyChanges: Boolean
        get() = hasLocationChanges || hasInfoChanges || hasMenuChanges
}

enum class EditScreen {
    Selection,      // 메인 선택 화면
    Location,       // 가게 위치 (EditAddressFragment)
    StoreInfo,      // 가게 정보
    StoreMenu       // 가게 메뉴 (MenuDetailScreen)
}

data class OriginalStoreData(
    val storeName: String,
    val storeType: String?,
    val location: LatLng?,
    val paymentMethods: Set<PaymentType>,
    val appearanceDays: Set<DayOfTheWeekType>,
    val openingHours: OpeningHourRequest,
    val categories: List<SelectCategoryModel>
)
```

#### 4.4.2 Intent 확장
```kotlin
sealed class Intent {
    // 기존 Intent 유지
    ...
    
    // NEW: 화면 네비게이션
    data class NavigateToScreen(val screen: EditScreen) : Intent()
    object NavigateBack : Intent()
    
    // NEW: 가게 정보 수정
    data class UpdateStoreName(val name: String) : Intent()
    data class UpdateStoreType(val type: String) : Intent()
    data class TogglePaymentMethod(val method: PaymentType) : Intent()
    data class ToggleAppearanceDay(val day: DayOfTheWeekType) : Intent()
    data class UpdateStartTime(val time: String?) : Intent()
    data class UpdateEndTime(val time: String?) : Intent()
    
    // NEW: 다이얼로그
    object ShowExitConfirmDialog : Intent()
    object HideExitConfirmDialog : Intent()
    object ConfirmExit : Intent()
    
    // NEW: 메뉴 관련 (MenuDetailScreen 연동)
    data class SetSelectedCategoryId(val categoryId: String?) : Intent()
    data class AddMenuToCategory(val categoryId: String) : Intent()
    data class RemoveMenuFromCategory(val categoryId: String, val menuIndex: Int) : Intent()
    data class UpdateMenuInCategory(
        val categoryId: String,
        val menuIndex: Int,
        val name: String,
        val price: String,
        val count: Int?
    ) : Intent()
}
```

#### 4.4.3 Effect 확장
```kotlin
sealed class Effect {
    object StoreUpdated : Effect()
    data class ShowError(val message: String) : Effect()
    
    // NEW: 네비게이션
    object NavigateToLocationEdit : Effect()
    object NavigateBack : Effect()
    object CloseScreen : Effect()
}
```

### 4.5 새로운 Compose 화면

#### 4.5.1 EditStoreScreen.kt (메인 선택 화면)
```kotlin
@Composable
fun EditStoreScreen(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit,
    modifier: Modifier = Modifier
)
```

**구성 요소:**
- `EditStoreTopBar`: 상단 네비게이션 (타이틀 + X 버튼)
- `EditStoreHeader`: 제목 + 수정된 정보 배지
- `EditSectionCard`: 가게 위치/정보/메뉴 카드
- `EditStoreFooter`: 수정 완료 버튼
- `ExitConfirmDialog`: 닫기 확인 다이얼로그

#### 4.5.2 EditStoreInfoScreen.kt (가게 정보 수정)
```kotlin
@Composable
fun EditStoreInfoScreen(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit,
    modifier: Modifier = Modifier
)
```

**구성 요소:**
- `StoreNameInput`: 가게 이름 입력
- `StoreTypeSelector`: 가게 형태 선택 (단일)
- `PaymentMethodSelector`: 결제 방식 선택 (다중)
- `AppearanceDaySelector`: 출몰 요일 선택 (다중)
- `OpeningHoursPicker`: 출몰 시간대 선택

#### 4.5.3 EditStoreComponents.kt (공통 컴포넌트)
- `EditSectionCard`: 섹션 카드 (아이콘 + 타이틀 + 설명)
- `StoreInfoItem`: 가게 정보 항목 (체크아이콘 + 라벨 + 값)
- `SelectableChip`: 선택 가능한 칩 (단일/다중)
- `DayCircleButton`: 요일 선택 원형 버튼
- `TimePicker`: 시간 선택기

---

## 5. MVI 패턴 구조

### 5.1 MVI 아키텍처 개요

```
┌─────────────────────────────────────────────────────────────────┐
│                         Composable UI                            │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────┐  │
│  │ EditStoreScreen │    │EditStoreInfoScreen│   │ MenuDetail  │  │
│  └────────┬────────┘    └────────┬────────┘    └──────┬──────┘  │
│           │                      │                     │         │
│           └──────────────────────┼─────────────────────┘         │
│                                  │                               │
│                    state.collectAsState()                        │
│                    onIntent: (Intent) -> Unit                    │
└──────────────────────────────────┼───────────────────────────────┘
                                   │
                    ┌──────────────▼──────────────┐
                    │      EditStoreViewModel      │
                    │  ┌─────────────────────────┐ │
                    │  │ _state: MutableStateFlow │ │
                    │  │ _effect: MutableSharedFlow│ │
                    │  │ processIntent(intent)    │ │
                    │  └─────────────────────────┘ │
                    └──────────────────────────────┘
                                   │
                    ┌──────────────▼──────────────┐
                    │      EditStoreContract       │
                    │  ┌────────┐ ┌────────┐      │
                    │  │ State  │ │ Intent │      │
                    │  └────────┘ └────────┘      │
                    │  ┌────────┐                 │
                    │  │ Effect │                 │
                    │  └────────┘                 │
                    └─────────────────────────────┘
```

### 5.2 Contract 구조 (EditStoreContract.kt)

```kotlin
object EditStoreContract {

    /**
     * UI 상태를 나타내는 불변 데이터 클래스
     * - Composable에서 collectAsState()로 구독
     * - 모든 UI 렌더링에 필요한 데이터 포함
     */
    data class State(
        // 가게 기본 정보
        val storeId: Int = 0,
        val storeName: String = "",
        val storeType: String? = null,
        val address: String = "",
        val selectedLocation: LatLng? = null,
        
        // 메뉴 정보
        val selectCategoryList: List<SelectCategoryModel> = emptyList(),
        val selectedCategoryId: String? = null,
        
        // 가게 상세 정보
        val selectedPaymentMethods: Set<PaymentType> = emptySet(),
        val selectedDays: Set<DayOfTheWeekType> = emptySet(),
        val openingHours: OpeningHourRequest = OpeningHourRequest(),
        
        // 화면 상태
        val currentScreen: EditScreen = EditScreen.Selection,
        val isLoading: Boolean = false,
        val error: String? = null,
        
        // 수정 추적
        val originalStoreData: OriginalStoreData? = null,
        val hasLocationChanges: Boolean = false,
        val hasInfoChanges: Boolean = false,
        val hasMenuChanges: Boolean = false,
        
        // 다이얼로그 상태
        val showExitConfirmDialog: Boolean = false
    ) {
        // 파생 상태 (Computed Properties)
        val totalChangedCount: Int
            get() = listOf(hasLocationChanges, hasInfoChanges, hasMenuChanges).count { it }
        
        val hasAnyChanges: Boolean
            get() = hasLocationChanges || hasInfoChanges || hasMenuChanges
            
        val menuCount: Int
            get() = selectCategoryList.sumOf { it.menuDetail?.size ?: 0 }
    }

    /**
     * 사용자 액션을 나타내는 sealed class
     * - UI에서 발생하는 모든 이벤트를 Intent로 변환
     * - ViewModel의 processIntent()로 전달
     */
    sealed class Intent {
        // 화면 네비게이션
        data class NavigateToScreen(val screen: EditScreen) : Intent()
        object NavigateBack : Intent()
        
        // 가게 정보 수정
        data class UpdateStoreName(val name: String) : Intent()
        data class UpdateStoreType(val type: String) : Intent()
        data class TogglePaymentMethod(val method: PaymentType) : Intent()
        data class ToggleAppearanceDay(val day: DayOfTheWeekType) : Intent()
        data class UpdateStartTime(val time: String?) : Intent()
        data class UpdateEndTime(val time: String?) : Intent()
        
        // 위치 수정
        data class UpdateLocation(val location: LatLng, val address: String) : Intent()
        
        // 메뉴 수정
        data class SetSelectedCategoryId(val categoryId: String?) : Intent()
        data class AddMenuToCategory(val categoryId: String) : Intent()
        data class RemoveMenuFromCategory(val categoryId: String, val menuIndex: Int) : Intent()
        data class UpdateMenuInCategory(
            val categoryId: String,
            val menuIndex: Int,
            val name: String,
            val price: String,
            val count: Int?
        ) : Intent()
        
        // 다이얼로그
        object ShowExitConfirmDialog : Intent()
        object HideExitConfirmDialog : Intent()
        object ConfirmExit : Intent()
        
        // 제출
        object SubmitChanges : Intent()
        object ClearError : Intent()
    }

    /**
     * 일회성 이벤트 (Side Effect)
     * - 토스트, 네비게이션, 스낵바 등
     * - SharedFlow로 emit하여 한 번만 소비
     */
    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
        data class ShowError(val message: String) : Effect()
        object StoreUpdated : Effect()
        object NavigateToLocationEdit : Effect()
        object NavigateBack : Effect()
        object CloseScreen : Effect()
    }
}
```

### 5.3 ViewModel 구조 (EditStoreViewModel.kt)

```kotlin
@HiltViewModel
class EditStoreViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : BaseViewModel() {

    // State: UI 상태 (StateFlow)
    private val _state = MutableStateFlow(EditStoreContract.State())
    val state: StateFlow<EditStoreContract.State> = _state.asStateFlow()

    // Effect: 일회성 이벤트 (SharedFlow)
    private val _effect = MutableSharedFlow<EditStoreContract.Effect>()
    val effect: SharedFlow<EditStoreContract.Effect> = _effect.asSharedFlow()

    /**
     * Intent 처리 함수
     * - 모든 사용자 액션은 이 함수를 통해 처리
     * - when 표현식으로 Intent 타입별 분기
     */
    fun processIntent(intent: EditStoreContract.Intent) {
        when (intent) {
            is EditStoreContract.Intent.NavigateToScreen -> navigateToScreen(intent.screen)
            is EditStoreContract.Intent.NavigateBack -> navigateBack()
            is EditStoreContract.Intent.UpdateStoreName -> updateStoreName(intent.name)
            is EditStoreContract.Intent.UpdateStoreType -> updateStoreType(intent.type)
            is EditStoreContract.Intent.TogglePaymentMethod -> togglePaymentMethod(intent.method)
            is EditStoreContract.Intent.ToggleAppearanceDay -> toggleAppearanceDay(intent.day)
            // ... 나머지 Intent 처리
        }
    }

    // State 업데이트는 항상 _state.update {} 사용
    private fun updateStoreName(name: String) {
        _state.update { currentState ->
            val hasChanges = name != currentState.originalStoreData?.storeName
            currentState.copy(
                storeName = name,
                hasInfoChanges = hasChanges || checkOtherInfoChanges(currentState)
            )
        }
    }

    // Effect emit은 viewModelScope에서 실행
    private fun submitChanges() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // API 호출
            homeRepository.putUserStore(request, storeId).collect { response ->
                if (response.ok) {
                    _effect.emit(EditStoreContract.Effect.StoreUpdated)
                } else {
                    _effect.emit(EditStoreContract.Effect.ShowError(response.message ?: ""))
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
```

### 5.4 Composable UI 연동

```kotlin
@Composable
fun EditStoreScreen(
    viewModel: EditStoreViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    // State 구독
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Effect 처리
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is EditStoreContract.Effect.ShowToast -> {
                    // Toast 표시
                }
                is EditStoreContract.Effect.StoreUpdated -> {
                    // 성공 처리 후 화면 종료
                    onNavigateBack()
                }
                is EditStoreContract.Effect.CloseScreen -> {
                    onNavigateBack()
                }
                // ... 나머지 Effect 처리
            }
        }
    }
    
    // UI 렌더링 - state 기반
    EditStoreContent(
        state = state,
        onIntent = viewModel::processIntent  // Intent 전달 함수
    )
}

@Composable
private fun EditStoreContent(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit
) {
    // 현재 화면에 따른 분기
    when (state.currentScreen) {
        EditScreen.Selection -> {
            SelectionContent(
                state = state,
                onLocationClick = { onIntent(EditStoreContract.Intent.NavigateToScreen(EditScreen.Location)) },
                onInfoClick = { onIntent(EditStoreContract.Intent.NavigateToScreen(EditScreen.StoreInfo)) },
                onMenuClick = { onIntent(EditStoreContract.Intent.NavigateToScreen(EditScreen.StoreMenu)) },
                onSubmit = { onIntent(EditStoreContract.Intent.SubmitChanges) },
                onClose = { onIntent(EditStoreContract.Intent.ShowExitConfirmDialog) }
            )
        }
        EditScreen.StoreInfo -> {
            EditStoreInfoScreen(
                state = state,
                onIntent = onIntent
            )
        }
        EditScreen.StoreMenu -> {
            // MenuDetailScreen 재활용
            MenuDetailScreen(
                selectCategoryList = state.selectCategoryList,
                selectedCategoryId = state.selectedCategoryId,
                onCategorySelected = { onIntent(EditStoreContract.Intent.SetSelectedCategoryId(it)) },
                onMenuAdd = { onIntent(EditStoreContract.Intent.AddMenuToCategory(it)) },
                onMenuRemove = { catId, idx -> onIntent(EditStoreContract.Intent.RemoveMenuFromCategory(catId, idx)) },
                onMenuUpdate = { catId, idx, name, price, count -> 
                    onIntent(EditStoreContract.Intent.UpdateMenuInCategory(catId, idx, name, price, count))
                },
                onBack = { onIntent(EditStoreContract.Intent.NavigateBack) }
            )
        }
        EditScreen.Location -> {
            // EditAddressFragment로 네비게이션 (Fragment 연동)
        }
    }
    
    // 다이얼로그
    if (state.showExitConfirmDialog) {
        ExitConfirmDialog(
            onDismiss = { onIntent(EditStoreContract.Intent.HideExitConfirmDialog) },
            onConfirm = { onIntent(EditStoreContract.Intent.ConfirmExit) }
        )
    }
    
    // 로딩 인디케이터
    if (state.isLoading) {
        LoadingOverlay()
    }
}
```

### 5.5 MVI 패턴 핵심 원칙

| 원칙 | 설명 |
|-----|------|
| **단방향 데이터 흐름** | UI → Intent → ViewModel → State → UI |
| **불변 State** | State는 data class로 copy()를 통해서만 변경 |
| **단일 진실 공급원** | 모든 UI 상태는 State에서 관리 |
| **Intent 기반 액션** | 모든 사용자 액션은 Intent로 표현 |
| **Effect로 부수효과** | Toast, Navigation 등은 Effect로 처리 |
| **Stateless Composable** | UI는 State만 받아 렌더링 (로직 없음) |

### 5.6 파생 상태 (Computed Properties) 활용

```kotlin
data class State(...) {
    // 수정된 항목 개수 계산
    val totalChangedCount: Int
        get() = listOf(hasLocationChanges, hasInfoChanges, hasMenuChanges).count { it }
    
    // 변경 사항 존재 여부
    val hasAnyChanges: Boolean
        get() = hasLocationChanges || hasInfoChanges || hasMenuChanges
    
    // 메뉴 총 개수
    val menuCount: Int
        get() = selectCategoryList.sumOf { it.menuDetail?.size ?: 0 }
    
    // 제출 버튼 활성화 여부
    val isSubmitEnabled: Boolean
        get() = hasAnyChanges && !isLoading
    
    // 결제 방식 표시 문자열
    val paymentMethodsText: String
        get() = selectedPaymentMethods.joinToString(", ") { it.displayName }
    
    // 출몰 요일 표시 문자열
    val appearanceDaysText: String
        get() = selectedDays.sortedBy { it.ordinal }.joinToString(", ") { it.shortName }
}
```

---

## 6. 리소스 매핑

### 5.1 색상 (기존 Color.kt 활용)

**경로:** `core/designsystem/src/main/java/base/compose/Color.kt`

| Figma 색상 | 헥사값 | 기존 Color.kt | 사용처 |
|-----------|-------|--------------|-------|
| gray/100 | #0F0F0F | `Gray100` ✅ | 메인 텍스트 |
| gray/70 | #5A5A5A | `Gray70` ✅ | 부제 텍스트 |
| gray/60 | #787878 | `Gray60` ✅ | 설명 텍스트 |
| gray/50 | #969696 | `Gray50` ✅ | 비활성 텍스트 |
| gray/40 | #B7B7B7 | ❌ **추가 필요** | 비활성 체크, 비선택 테두리 |
| gray/30 | #D0D0D0 | `Gray30` ✅ | 비선택 테두리 |
| gray/10 | #F4F4F4 | `Gray10` ✅ | 입력 필드 배경 |
| main/pink | #FF858F | `Pink` ✅ | 선택된 항목, 버튼 |
| main/green | #00C667 | `Green` ✅ | 수정 완료 배지 |
| green/100 | #F1FFF8 | `Green100` ✅ | 배지 배경 |
| main/red | #FF5C43 | `Red` ✅ | 나가기 버튼 |
| system/white | #FFFFFF | `ColorWhite` ✅ | 배경, 카드 |

**추가 필요한 색상:**
```kotlin
// Color.kt에 추가
val Gray40 = Color(0xFFB7B7B7)  // 기존 0xFF878787 → 0xFFB7B7B7로 수정 필요
```

> ⚠️ **주의**: 기존 `Gray40`은 `#878787`이지만 Figma에서는 `#B7B7B7`을 사용. 기존 사용처 확인 후 새로운 값 추가 또는 별도 상수 생성 검토 필요.

### 5.2 문자열 (기존 strings.xml 활용)

**경로:** `core/common/src/main/res/values/strings.xml`

| 화면/컴포넌트 | 기존 리소스 | 비고 |
|-------------|-----------|------|
| 상단 타이틀 | `edit_store` ✅ | "가게 정보 수정" |
| 닫기 버튼 | `close` ✅ | "닫기" |
| 가게 이름 | `store_name` ✅ | "가게 이름" |
| 가게 위치 | `store_location` ✅ | "가게 위치" |
| 가게 형태 | `store_type` ✅ | "가게형태" |
| 길거리 | `road` ✅ | "길거리" |
| 매장 | `store` ✅ | "매장" |
| 푸드트럭 | `food_truck` ✅ | "푸드트럭" |
| 편의점 | `convenience_store` ✅ | "편의점" |
| 결제방식 | `payment_type` ✅ | "결제방식" |
| 현금 | `cash` ✅ | "현금" |
| 카드 | `card` ✅ | "카드" |
| 계좌이체 | `banking` ✅ | "계좌이체" |
| 다중선택 가능 | `enable_multi_select` ✅ | "*다중선택 가능" |
| 출몰 요일 | `add_store_appearance_day` ✅ | "출몰 요일" |
| 출몰 시간대 | `add_store_appearance_time` ✅ | "출몰 시간대" |
| 월~일 | `monday`~`sunday` ✅ | 요일 |
| 부터 | `time_from` ✅ | "부터" |
| 까지 | `time_until` ✅ | "까지" |
| 수정하기 | `edit_store_finish` ✅ | "수정하기" |
| 수정 완료! | `edit_store_success` ✅ | 토스트 메시지 |
| 다이얼로그 제목 | `exit_confirm_title` ✅ | "다음에 할까요?" |
| 다이얼로그 닫기 | `exit_confirm_dismiss` ✅ | "닫기" |
| 다이얼로그 나가기 | `exit_confirm_exit` ✅ | "나가기" |

**새로 추가 필요한 문자열:**
```xml
<!-- Edit Store - Selection Screen -->
<string name="edit_store_select_title">제보할 정보를\n선택해 주세요</string>
<string name="edit_store_select_subtitle">작은 제보로 가게 정보 완성에 힘을 보태요</string>
<string name="edit_store_changed_count">%d개의 수정된 정보가 있어요</string>
<string name="edit_store_complete">수정 완료</string>

<!-- Edit Store - Section Cards -->
<string name="edit_store_section_location">가게 위치</string>
<string name="edit_store_section_info">가게 정보</string>
<string name="edit_store_section_menu">가게 메뉴</string>
<string name="edit_store_menu_count">%d개의 제보된 메뉴가 있어요</string>
<string name="edit_store_no_info">제보된 정보가 없어요</string>

<!-- Edit Store - Exit Dialog -->
<string name="edit_store_exit_message">수정된 정보가 있어요. 지금까지 입력한 정보가 저장되지 않아요.</string>
```

### 5.3 간격 및 크기 (Dimension 상수)

```kotlin
// EditStoreDimens.kt (새로 생성)
object EditStoreDimens {
    val ScreenHorizontalPadding = 20.dp
    val CardPadding = 12.dp
    val CardRadius = 16.dp
    val InputFieldRadius = 8.dp
    val DayButtonSize = 36.dp
    val SectionGap = 8.dp
    val ContentGap = 28.dp
    val IconSize = 24.dp
    val IconContainerSize = 32.dp
}
```

### 5.4 Shadow 스타일

```kotlin
// 카드 그림자
Modifier.shadow(
    elevation = 10.dp,
    shape = RoundedCornerShape(16.dp),
    spotColor = Color.Black.copy(alpha = 0.1f)
)
```

---

## 7. 구현 순서

### Phase 1: Contract 및 ViewModel 확장 ✅ 완료
1. `EditStoreContract.kt` 확장 (State, Intent, Effect)
2. `EditStoreViewModel.kt` 확장 (새로운 Intent 처리)
- 상세 문서: `.serena/memories/phase1_implementation_details.md`

### Phase 2: 공통 컴포넌트 구현 ✅ 완료
1. `EditStoreComponents.kt` 생성
2. 기존 컴포넌트 재활용 검토 (CategoryChip 등)
- 상세 문서: `.serena/memories/phase2_implementation_details.md`

### Phase 3: 화면 구현 ✅ 완료
1. `EditStoreScreen.kt` - 메인 선택 화면
2. `EditStoreInfoScreen.kt` - 가게 정보 수정 화면
3. `EditStoreFragment.kt` - Compose 호스트 Fragment
- 상세 문서: `.serena/memories/phase3_implementation_details.md`

### Phase 4: 통합 및 테스트 ✅ 완료
1. EditAddressFragment 연동 (위치 수정) - 기존 구현 유지
2. EditMenuScreen 생성 (메뉴 수정 Compose 화면)
3. StoreDetailActivity 연결 (EditStoreFragment로 변경)
4. 빌드 검증 완료
- 상세 문서: `.serena/memories/phase4_implementation_details.md`

---

## 8. 재활용 컴포넌트

### 7.1 MenuDetailScreen.kt에서 재활용
- `CategoryChip`: 카테고리 칩
- `CategoryEditBottomSheet`: 카테고리 편집 바텀시트
- `MenuInputRow`: 메뉴 입력 행
- `MenuCategorySection`: 메뉴 카테고리 섹션

### 7.2 기존 base.compose에서 재활용
- 색상 상수 (Gray, Pink 등)
- `PretendardFontFamily`

---

## 9. 주의사항

### 8.1 호환성
- 기존 XML 기반 EditAddressFragment와의 호환성 유지
- Fragment Result API 유지 (`STORE_EDITED_RESULT_KEY`)

### 8.2 상태 관리
- 원본 데이터와 수정된 데이터 비교하여 변경 여부 추적
- 화면 전환 시 상태 유지

### 8.3 문자열 리소스
- 모든 문자열은 `core:common/strings.xml`에 정의
- `CommonR.string.xxx` 형식으로 사용

---

## 10. 예상 작업량

| 작업 항목 | 예상 복잡도 |
|---------|-----------|
| Contract 확장 | 중 |
| ViewModel 확장 | 중 |
| 공통 컴포넌트 | 중 |
| EditStoreScreen | 상 |
| EditStoreInfoScreen | 상 |
| Fragment 통합 | 중 |
| 테스트 및 수정 | 중 |

---

## 11. 참고 자료

- Figma: https://www.figma.com/design/Gw367Wy4qqnEWcvSlNUqzB/가슴속-3천원-ver4.0.0
- 기존 AddStoreContract 참고 (메뉴 관련 Intent)
- MenuDetailScreen.kt (Compose 패턴 참고)
