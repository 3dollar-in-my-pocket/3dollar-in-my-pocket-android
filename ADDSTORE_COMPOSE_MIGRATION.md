# AddStoreDetailFragment Compose 마이그레이션 가이드

## 개요

`AddStoreDetailFragment`를 기존 ViewBinding 기반에서 Jetpack Compose로 리팩토링하는 가이드입니다.
Navigation Component와의 통합을 유지하면서 점진적으로 Compose로 전환합니다.

## 목표

5개의 화면으로 구성된 가게 제보 플로우를 Compose로 구현:
1. **가게 필수 정보 입력** - 가게 이름, 위치, 카테고리
2. **음식 카테고리 선택** - 최대 3개 카테고리 선택
3. **메뉴 상세 정보 추가** - 선택한 카테고리별 메뉴 입력 (재사용 가능)
4. **가게 상세 정보 입력** - 결제방식, 출몰요일, 출몰시간대 (재사용 가능)
5. **작성 완료** - 최종 확인 및 제출

## 아키텍처 설계

### 전체 구조

```
Navigation Component (Fragment-based)
└── AddStoreDetailFragment (Container Fragment)
    └── ComposeView
        └── AddStoreFlowScreen (Compose Navigation)
            ├── RequiredInfoScreen
            ├── MenuCategoryScreen
            ├── MenuDetailScreen (재사용 가능)
            ├── StoreDetailScreen (재사용 가능)
            └── CompletionScreen
```

### Navigation 통합 전략

- **Fragment Navigation**: Navigation Component 유지 (`mobile_navigation.xml`)
- **Compose Navigation**: Fragment 내부에서 화면 전환
- **ViewModel 공유**: `activityViewModels()`로 전체 플로우 상태 공유

## Phase별 마이그레이션 계획

### Phase 1: Fragment를 ComposeView로 전환 ✅

**목표**: 기존 Fragment를 ComposeView 컨테이너로 전환 (기능 유지)

**작업 내용**:
- ViewBinding 제거
- ComposeView로 UI 진입점 변경
- `activityViewModels()`로 ViewModel 유지
- Navigation Component 통합 유지
- Back press 핸들링 유지

**참고 예시**: `NewAddressFragment.kt`, `MyPageFragment.kt`

```kotlin
@AndroidEntryPoint
class AddStoreDetailFragment : Fragment() {
    private val viewModel: AddStoreViewModel by activityViewModels()
    private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    AddStoreFlowScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() },
                        onComplete = { navigateToHome() }
                    )
                }
            }
        }
    }

    private fun navigateBack() {
        findNavController().navigateSafe(R.id.action_navigation_write_detail_to_navigation_write)
    }

    private fun navigateToHome() {
        findNavController().navigateSafe(R.id.action_navigation_write_detail_to_home)
    }
}
```

---

### Phase 2: 내부 Compose Navigation 구조 구축 ✅

**목표**: Compose Navigation으로 5개 화면 플로우 구성

**작업 내용**:
- `NavController` 생성
- 5개 화면 라우트 정의
- 공통 Scaffold (TopBar, BottomBar)
- 화면 전환 애니메이션

**Route 정의**:
```kotlin
object AddStoreRoute {
    const val REQUIRED_INFO = "required_info"
    const val MENU_CATEGORY = "menu_category"
    const val MENU_DETAIL = "menu_detail"
    const val STORE_DETAIL = "store_detail"
    const val COMPLETION = "completion"
}
```

**AddStoreFlowScreen 구조**:
```kotlin
@Composable
fun AddStoreFlowScreen(
    viewModel: AddStoreViewModel,
    onNavigateBack: () -> Unit,
    onComplete: () -> Unit
) {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()

    Scaffold(
        topBar = {
            AddStoreTopBar(
                title = "가게 제보",
                onBackClick = {
                    if (!navController.popBackStack()) {
                        onNavigateBack()
                    }
                }
            )
        },
        bottomBar = {
            AddStoreBottomBar(
                currentRoute = currentRoute?.destination?.route,
                onNextClick = { navController.navigate(nextRoute) },
                onCompleteClick = { submitStore() }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AddStoreRoute.REQUIRED_INFO,
            modifier = Modifier.padding(padding)
        ) {
            composable(AddStoreRoute.REQUIRED_INFO) {
                RequiredInfoScreen(viewModel = viewModel)
            }
            composable(AddStoreRoute.MENU_CATEGORY) {
                MenuCategoryScreen(viewModel = viewModel)
            }
            composable(AddStoreRoute.MENU_DETAIL) {
                MenuDetailScreen(viewModel = viewModel)
            }
            composable(AddStoreRoute.STORE_DETAIL) {
                StoreDetailScreen(viewModel = viewModel)
            }
            composable(AddStoreRoute.COMPLETION) {
                CompletionScreen(viewModel = viewModel)
            }
        }
    }
}
```

---

### Phase 3: 각 화면별 Composable 구현 🔄 (다음 단계)

**목표**: 각 화면의 실제 UI 구현

#### 3-1. RequiredInfoScreen (가게 필수 정보)

**UI 요소**:
- 가게 이름 입력 (EditText)
- 현재 위치 표시 (Naver Map)
- 주소 수정 버튼
- 카테고리 선택 칩 (길거리, 매장, 푸드트럭, 편의점)

**검증**:
- 가게 이름 필수 입력
- 위치 선택 필수

```kotlin
@Composable
fun RequiredInfoScreen(
    viewModel: AddStoreViewModel,
    modifier: Modifier = Modifier
) {
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val storeName by viewModel.storeName.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        // 가게 이름 입력
        OutlinedTextField(
            value = storeName,
            onValueChange = { viewModel.updateStoreName(it) },
            label = { Text("가게 이름") },
            placeholder = { Text("붕어빵 2만 중구 삼거리 근처 붕어빵 장") }
        )

        // 지도 섹션
        NaverMapSection(
            selectedLocation = selectedLocation,
            onLocationChanged = { viewModel.updateLocation(it) }
        )

        // 가게 형태 선택
        StoreTypeChipGroup(
            selectedType = storeType,
            onTypeSelected = { viewModel.updateStoreType(it) }
        )
    }
}
```

#### 3-2. MenuCategoryScreen (음식 카테고리 선택)

**UI 요소**:
- 간식 카테고리 (붕어빵, 문어빵, 꼬치, 호떡 등)
- 식사 카테고리 (한식, 양식, 일식, 중식 등)
- 기타 카테고리
- 최대 3개 선택 제한
- 선택 취소 기능

**검증**:
- 최소 1개 카테고리 선택 필수

```kotlin
@Composable
fun MenuCategoryScreen(
    viewModel: AddStoreViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategories by viewModel.selectCategoryList.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "음식 카테고리 선택 (${selectedCategories.size}/10)",
            style = MaterialTheme.typography.h6
        )

        // 카테고리별 그룹
        CategorySection(
            title = "간식",
            categories = snackCategories,
            selectedCategories = selectedCategories,
            onCategoryClick = { viewModel.toggleCategory(it) }
        )

        CategorySection(
            title = "식사",
            categories = mealCategories,
            selectedCategories = selectedCategories,
            onCategoryClick = { viewModel.toggleCategory(it) }
        )
    }
}
```

#### 3-3. MenuDetailScreen (메뉴 상세 정보) - 재사용 가능

**UI 요소**:
- 선택한 카테고리별 메뉴 그룹
- 각 메뉴: 이름, 가격 입력
- 메뉴 추가/삭제 버튼
- 카테고리별 최소 1개 메뉴 필수

**재사용 설계**:
- 다른 화면에서도 독립적으로 사용 가능
- Props를 통한 데이터 주입

```kotlin
@Composable
fun MenuDetailScreen(
    viewModel: AddStoreViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategories by viewModel.selectCategoryList.collectAsState()

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(selectedCategories) { category ->
            MenuCategoryGroup(
                category = category,
                menus = category.menus,
                onMenuAdd = { viewModel.addMenu(category, it) },
                onMenuRemove = { viewModel.removeMenu(category, it) }
            )
        }
    }
}

// 재사용 가능한 독립 컴포넌트
@Composable
fun MenuCategoryGroup(
    category: CategoryModel,
    menus: List<MenuModel>,
    onMenuAdd: (MenuModel) -> Unit,
    onMenuRemove: (MenuModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "${category.emoji} ${category.name} 메뉴",
            style = MaterialTheme.typography.subtitle1
        )

        menus.forEachIndexed { index, menu ->
            MenuInputRow(
                index = index + 1,
                menu = menu,
                onMenuChange = { /* update */ },
                onRemove = { onMenuRemove(menu) }
            )
        }

        Button(onClick = { onMenuAdd(MenuModel()) }) {
            Text("메뉴 추가")
        }
    }
}
```

#### 3-4. StoreDetailScreen (가게 상세 정보) - 재사용 가능

**UI 요소**:
- 결제방식 선택 (현금, 카드, 계좌이체)
- 출몰 요일 선택 (월~일)
- 출몰 시간대 선택 (시작 시간 ~ 종료 시간)

**재사용 설계**:
- 다른 화면(가게 수정 등)에서도 사용 가능

```kotlin
@Composable
fun StoreDetailScreen(
    viewModel: AddStoreViewModel,
    modifier: Modifier = Modifier
) {
    val paymentMethods by viewModel.paymentMethods.collectAsState()
    val appearanceDays by viewModel.appearanceDays.collectAsState()
    val openingHours by viewModel.openingHours.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        // 결제방식
        PaymentMethodSection(
            selectedMethods = paymentMethods,
            onMethodToggle = { viewModel.togglePaymentMethod(it) }
        )

        // 출몰 요일
        AppearanceDaysSection(
            selectedDays = appearanceDays,
            onDayToggle = { viewModel.toggleAppearanceDay(it) }
        )

        // 출몰 시간대
        OpeningHoursSection(
            startTime = openingHours.startTime,
            endTime = openingHours.endTime,
            onStartTimeClick = { /* show time picker */ },
            onEndTimeClick = { /* show time picker */ }
        )
    }
}

// 재사용 가능한 독립 컴포넌트
@Composable
fun PaymentMethodSection(
    selectedMethods: List<PaymentType>,
    onMethodToggle: (PaymentType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("결제방식", style = MaterialTheme.typography.subtitle1)

        Row {
            PaymentMethodChip(
                method = PaymentType.CASH,
                isSelected = selectedMethods.contains(PaymentType.CASH),
                onClick = { onMethodToggle(PaymentType.CASH) }
            )
            // 카드, 계좌이체 동일
        }
    }
}
```

#### 3-5. CompletionScreen (작성 완료)

**UI 요소**:
- 작성 완료 안내 메시지
- 입력한 정보 요약 표시
- "제보 완료" 버튼

```kotlin
@Composable
fun CompletionScreen(
    viewModel: AddStoreViewModel,
    modifier: Modifier = Modifier
) {
    val storeInfo by viewModel.storeInfo.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Text(
            text = "가게 제보가 완료되었습니다",
            style = MaterialTheme.typography.h5
        )

        // 입력한 정보 요약
        StoreInfoSummary(storeInfo = storeInfo)
    }
}
```

---

### Phase 4: 재사용 컴포넌트 독립화 🔄 (다음 단계)

**목표**: MenuDetailScreen, StoreDetailScreen을 독립적으로 사용 가능하도록

**작업 내용**:
- ViewModel 의존성 제거
- Props 기반 데이터 주입
- Callback 패턴으로 변경
- 다른 Fragment/Activity에서 사용 가능

**리팩토링 예시**:

```kotlin
// Before (ViewModel 의존)
@Composable
fun MenuDetailScreen(
    viewModel: AddStoreViewModel
) {
    val categories by viewModel.selectCategoryList.collectAsState()
    // ...
}

// After (독립적 컴포넌트)
@Composable
fun MenuDetailScreen(
    selectedCategories: List<CategoryModel>,
    onMenuAdd: (CategoryModel, MenuModel) -> Unit,
    onMenuRemove: (CategoryModel, MenuModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(selectedCategories) { category ->
            MenuCategoryGroup(
                category = category,
                onMenuAdd = { menu -> onMenuAdd(category, menu) },
                onMenuRemove = { menu -> onMenuRemove(category, menu) }
            )
        }
    }
}

// 사용처 1: AddStoreFlowScreen
@Composable
fun AddStoreFlowScreen(viewModel: AddStoreViewModel) {
    val categories by viewModel.selectCategoryList.collectAsState()

    MenuDetailScreen(
        selectedCategories = categories,
        onMenuAdd = { category, menu -> viewModel.addMenu(category, menu) },
        onMenuRemove = { category, menu -> viewModel.removeMenu(category, menu) }
    )
}

// 사용처 2: EditStoreFragment (가게 수정)
@Composable
fun EditStoreScreen(editViewModel: EditStoreViewModel) {
    val categories by editViewModel.categories.collectAsState()

    MenuDetailScreen(
        selectedCategories = categories,
        onMenuAdd = { category, menu -> editViewModel.updateMenu(category, menu) },
        onMenuRemove = { category, menu -> editViewModel.deleteMenu(category, menu) }
    )
}
```

---

### Phase 5: 레거시 컴포넌트 Compose 전환 🔄 (다음 단계)

**목표**: Dialog, Adapter 등 레거시 컴포넌트를 Compose로 전환

#### 5-1. AddStoreMenuCategoryDialogFragment → Compose Dialog

```kotlin
// Before: DialogFragment
class AddStoreMenuCategoryDialogFragment : DialogFragment() {
    // ...
}

// After: Compose Dialog
@Composable
fun MenuCategoryDialog(
    onDismiss: () -> Unit,
    onCategorySelected: (CategoryModel) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.surface
        ) {
            MenuCategoryList(
                onCategoryClick = { category ->
                    onCategorySelected(category)
                    onDismiss()
                }
            )
        }
    }
}
```

#### 5-2. OpeningHourNumberPickerDialog → Compose Dialog

```kotlin
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (Int?) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        // NumberPicker 구현
    }
}
```

#### 5-3. RecyclerView Adapters → LazyColumn

```kotlin
// Before: RecyclerView + Adapter
class EditMenuRecyclerAdapter : RecyclerView.Adapter<ViewHolder>() {
    // ...
}

// After: LazyColumn
@Composable
fun MenuList(
    menus: List<MenuModel>,
    onMenuChange: (Int, MenuModel) -> Unit,
    onMenuRemove: (Int) -> Unit
) {
    LazyColumn {
        itemsIndexed(menus) { index, menu ->
            MenuInputRow(
                index = index,
                menu = menu,
                onMenuChange = { onMenuChange(index, it) },
                onRemove = { onMenuRemove(index) }
            )
        }
    }
}
```

---

## ViewModel 개선

### 현재 AddStoreViewModel 구조

```kotlin
@HiltViewModel
class AddStoreViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val repository: UserRepository
) : BaseViewModel() {
    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation

    private val _selectCategoryList = MutableStateFlow<List<CategoryModel>>(emptyList())
    val selectCategoryList: StateFlow<List<CategoryModel>> = _selectCategoryList

    // ...
}
```

### Compose 최적화를 위한 개선

```kotlin
@HiltViewModel
class AddStoreViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val repository: UserRepository
) : ViewModel() {

    // 기존 State 유지
    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation

    private val _selectCategoryList = MutableStateFlow<List<CategoryModel>>(emptyList())
    val selectCategoryList: StateFlow<List<CategoryModel>> = _selectCategoryList

    // 추가: 화면 플로우 관리
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep

    // 추가: 가게 이름
    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName

    // 추가: 가게 타입
    private val _storeType = MutableStateFlow<String?>(null)
    val storeType: StateFlow<String?> = _storeType

    // 추가: 결제 방식
    private val _paymentMethods = MutableStateFlow<List<PaymentType>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentType>> = _paymentMethods

    // 추가: 출몰 요일
    private val _appearanceDays = MutableStateFlow<List<DayOfTheWeekType>>(emptyList())
    val appearanceDays: StateFlow<List<DayOfTheWeekType>> = _appearanceDays

    // 추가: 출몰 시간
    private val _openingHours = MutableStateFlow(OpeningHourRequest(null, null))
    val openingHours: StateFlow<OpeningHourRequest> = _openingHours

    // 유효성 검증
    fun validateRequiredInfo(): Boolean {
        return _storeName.value.isNotEmpty() &&
               _selectedLocation.value != null &&
               _storeType.value != null
    }

    fun validateMenuCategory(): Boolean {
        return _selectCategoryList.value.isNotEmpty()
    }

    fun validateMenuDetail(): Boolean {
        return _selectCategoryList.value.all { category ->
            category.menus.isNotEmpty()
        }
    }

    // 화면 전환
    fun moveToNextStep() {
        _currentStep.value += 1
    }

    fun moveToPreviousStep() {
        _currentStep.value -= 1
    }
}
```

---

## 프로젝트 Compose 패턴 참고

### 1. Fragment에서 ComposeView 사용

**예시**: `NewAddressFragment.kt`, `MyPageFragment.kt`

```kotlin
@AndroidEntryPoint
class MyFragment : Fragment() {
    private val viewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    MyScreen(viewModel = viewModel)
                }
            }
        }
    }
}
```

### 2. Naver Map Compose 통합

**예시**: `NewAddressFragment.kt`의 `NaverMapSection`

```kotlin
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun NaverMapSection(
    selectedLocation: LatLng?,
    onCameraIdle: (LatLng, Double) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(selectedLocation ?: DEFAULT_LOCATION, 15.0)
    }

    Box {
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            locationSource = rememberFusedLocationSource(),
            properties = MapProperties(locationTrackingMode = LocationTrackingMode.Follow)
        )

        Icon(
            painter = painterResource(R.drawable.ic_mappin),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
```

### 3. Theme 및 디자인 시스템

**색상**: `core/designsystem/src/main/java/base/compose/Color.kt`
**폰트**: `core/designsystem/src/main/java/base/compose/Font.kt`

```kotlin
// 색상 사용
Text(
    text = "가게 제보",
    color = Gray100,
    style = MaterialTheme.typography.h6
)

// 폰트 사용
Text(
    text = "가게 이름",
    fontFamily = PretendardFontFamily,
    fontWeight = FontWeight.Bold
)
```

### 4. 공통 Dialog

**예시**: `core/common/src/main/java/com/threedollar/common/compose/dialog/CommonDialog.kt`

```kotlin
CommonDialog(
    title = "가게 이름을 입력해주세요",
    confirmButton = DialogButton(
        text = "확인",
        onClick = { /* handle */ }
    ),
    onDismissRequest = { /* dismiss */ }
)
```

---

## 테스트 체크리스트

### Phase 1-2 (Empty 화면)
- [ ] Fragment가 ComposeView로 정상 렌더링
- [ ] Back press가 정상 동작 (Compose Navigation → Fragment Navigation)
- [ ] ViewModel이 정상적으로 공유됨
- [ ] 5개 화면 간 Navigation이 정상 동작
- [ ] TopBar, BottomBar가 정상 표시

### Phase 3 (실제 UI 구현)
- [ ] RequiredInfoScreen: 가게 이름, 위치, 카테고리 입력 가능
- [ ] MenuCategoryScreen: 최대 10개 카테고리 선택 가능
- [ ] MenuDetailScreen: 카테고리별 메뉴 추가/삭제 가능
- [ ] StoreDetailScreen: 결제방식, 출몰요일, 시간 선택 가능
- [ ] CompletionScreen: 입력 정보 요약 표시
- [ ] 각 화면 유효성 검증 정상 동작

### Phase 4 (재사용 컴포넌트)
- [ ] MenuDetailScreen을 다른 화면에서 사용 가능
- [ ] StoreDetailScreen을 다른 화면에서 사용 가능
- [ ] ViewModel 의존성 제거 완료

### Phase 5 (레거시 전환)
- [ ] Dialog가 Compose로 전환
- [ ] RecyclerView가 LazyColumn으로 전환
- [ ] 모든 레거시 컴포넌트 제거

---

## 참고 파일 위치

### 현재 파일
- Fragment: `app/src/main/java/com/zion830/threedollars/ui/write/ui/AddStoreDetailFragment.kt`
- ViewModel: `app/src/main/java/com/zion830/threedollars/ui/write/viewModel/AddStoreViewModel.kt`
- Navigation: `app/src/main/res/navigation/mobile_navigation.xml`
- Layout: `app/src/main/res/layout/fragment_add_store.xml`

### 새로 생성할 파일
- Compose Screen: `app/src/main/java/com/zion830/threedollars/ui/write/ui/compose/`
  - `AddStoreFlowScreen.kt`
  - `RequiredInfoScreen.kt`
  - `MenuCategoryScreen.kt`
  - `MenuDetailScreen.kt`
  - `StoreDetailScreen.kt`
  - `CompletionScreen.kt`

### 프로젝트 Compose 참고 예시
- Fragment Compose: `app/src/main/java/com/zion830/threedollars/ui/write/ui/NewAddressFragment.kt`
- Compose Screen: `app/src/main/java/com/zion830/threedollars/ui/my/page/screen/MyPageScreen.kt`
- Theme: `core/designsystem/src/main/java/base/compose/`
- Common Dialog: `core/common/src/main/java/com/threedollar/common/compose/dialog/CommonDialog.kt`

---

## 마이그레이션 후 이점

1. **코드 간결성**: ViewBinding + RecyclerView → Compose로 코드량 50% 감소
2. **재사용성**: MenuDetailScreen, StoreDetailScreen을 다른 화면에서도 사용 가능
3. **유지보수성**: 선언적 UI로 상태 관리 단순화
4. **일관성**: 프로젝트 전체가 Compose로 통일
5. **성능**: LazyColumn의 효율적인 렌더링
6. **테스트**: Composable 단위 테스트 용이

---

## 주의사항

1. **Navigation Component 유지**: Fragment destination은 변경하지 않음
2. **ViewModel 공유**: `activityViewModels()`로 다른 Fragment와 상태 공유
3. **Back Stack 관리**: Compose Navigation과 Fragment Navigation의 Back 동작 구분
4. **테마 일관성**: 기존 앱 테마를 Compose Theme으로 적용
5. **점진적 전환**: 한 번에 모든 화면을 전환하지 않고 단계별로 진행
6. **기존 기능 유지**: 리팩토링 중에도 기존 기능이 정상 동작해야 함

---

## Phase 진행 상황

- [x] Phase 1: Fragment → ComposeView 전환
- [x] Phase 2: Compose Navigation 구조 구축 + Empty 화면
- [ ] Phase 3: 각 화면 실제 UI 구현
- [ ] Phase 4: 재사용 컴포넌트 독립화
- [ ] Phase 5: 레거시 컴포넌트 Compose 전환

---

**작성일**: 2025-11-15
**작성자**: Claude Code
**프로젝트**: 3dollar-in-my-pocket-android
