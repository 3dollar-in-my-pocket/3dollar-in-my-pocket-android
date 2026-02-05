# Phase 4 구현 상세 - 통합 및 테스트

## 1. 개요

Phase 4에서는 Phase 1-3에서 구현한 Compose 기반 가게 수정 화면들을 기존 시스템과 통합했습니다.

## 2. 수정된 파일 목록

### 2.1 EditStoreContract.kt
**경로**: `app/src/main/java/com/zion830/threedollars/ui/edit/viewModel/EditStoreContract.kt`

**변경 내용**:
- `UpdateMenuInCategory` Intent에 `count: Int?` 파라미터 추가

```kotlin
data class UpdateMenuInCategory(
    val categoryId: String,
    val menuIndex: Int,
    val name: String,
    val price: String,
    val count: Int?  // 추가됨
) : Intent()
```

### 2.2 EditStoreViewModel.kt
**경로**: `app/src/main/java/com/zion830/threedollars/ui/edit/viewModel/EditStoreViewModel.kt`

**변경 내용**:
1. `processIntent`에서 `count` 전달 추가
2. `updateMenuInCategory` 함수에 `count` 파라미터 처리 추가

```kotlin
private fun updateMenuInCategory(
    categoryId: String,
    menuIndex: Int,
    name: String,
    price: String,
    count: Int?  // 추가됨
) {
    // count를 UserStoreMenuModel.copy()에 전달
    currentMenus[menuIndex] = existingMenu.copy(name = name, price = price, count = count)
}
```

### 2.3 EditMenuScreen.kt (새로 생성)
**경로**: `app/src/main/java/com/zion830/threedollars/ui/edit/ui/compose/EditMenuScreen.kt`

**구성 요소**:
- `EditMenuScreen` - 메인 Composable
- `EditMenuCategorySection` - 카테고리별 메뉴 섹션
- `EditMenuInputRow` - 개별 메뉴 입력 행 (이름, 개수, 가격)
- `EditCategoryChip` - 카테고리 선택 칩
- `EditCategoryEditButton` - 카테고리 편집 버튼
- `EditCategoryBottomSheet` - 카테고리 수정 바텀시트
- `EditCategorySelectChip` - 바텀시트 내 카테고리 선택 칩

**특징**:
- EditStoreContract.State/Intent 사용
- TopBar 포함 (뒤로가기, 닫기 버튼)
- ModalBottomSheetLayout으로 카테고리 편집
- 로딩 인디케이터 및 종료 확인 다이얼로그 통합

### 2.4 EditStoreFragment.kt
**경로**: `app/src/main/java/com/zion830/threedollars/ui/edit/ui/EditStoreFragment.kt`

**변경 내용**:
- `EditMenuScreen` import 추가
- `EditScreen.StoreMenu` 케이스에서 `EditMenuScreen` 호출

```kotlin
EditScreen.StoreMenu -> {
    EditMenuScreen(
        state = state,
        onIntent = editStoreViewModel::processIntent
    )
}
```

### 2.5 StoreDetailActivity.kt
**경로**: `app/src/main/java/com/zion830/threedollars/ui/storeDetail/user/ui/StoreDetailActivity.kt`

**변경 내용**:
1. Import 변경:
   - `EditStoreDetailFragment` → `EditStoreFragment`
   - `STORE_EDITED_RESULT_KEY` 경로 변경

2. Fragment 생성 변경:
   - `EditStoreDetailFragment()` → `EditStoreFragment()`

## 3. 데이터 흐름

### 3.1 메뉴 수정 흐름
```
EditStoreScreen (Selection)
    ↓ "가게 메뉴" 카드 클릭
NavigateToScreen(StoreMenu) Intent
    ↓
EditStoreViewModel._state.currentScreen = StoreMenu
    ↓
EditStoreFragment: EditScreen.StoreMenu 케이스
    ↓
EditMenuScreen 렌더링
    ↓
사용자 메뉴 입력/수정
    ↓
UpdateMenuInCategory(categoryId, index, name, price, count) Intent
    ↓
EditStoreViewModel.updateMenuInCategory()
    ↓
state.selectCategoryList 업데이트
    ↓
state.hasMenuChanges 계산
```

### 3.2 카테고리 수정 흐름
```
EditMenuScreen
    ↓ "편집" 버튼 클릭
ModalBottomSheet 표시
    ↓
EditCategoryBottomSheet 렌더링
    ↓
카테고리 선택/해제
    ↓
ChangeSelectCategory(category) Intent
    ↓
EditStoreViewModel.changeSelectCategory()
    ↓
state.selectCategoryList 업데이트
```

## 4. 연동 확인

### 4.1 EditAddressFragment 연동 (기존 구현)
- `Effect.NavigateToLocationEdit` → `EditAddressFragment`로 이동
- SharedViewModel(activityViewModels) 패턴으로 데이터 공유
- `ConfirmLocation` Intent로 위치 확정

### 4.2 StoreDetailActivity 연동
- `editStoreInfoButton` 클릭 → `EditStoreFragment` 표시
- Fragment Result API: `STORE_EDITED_RESULT_KEY` 유지
- 수정 완료 시 `refreshStoreInfo()` 호출

## 5. 빌드 결과

```
BUILD SUCCESSFUL in 11s
311 actionable tasks: 11 executed, 300 up-to-date
```

## 6. 테스트 체크리스트

### 6.1 메인 화면 진입
- [x] StoreDetailActivity → "가게 정보 수정" 버튼 클릭 (코드 연결 완료)
- [ ] EditStoreFragment(Selection 화면) 정상 표시 (런타임 테스트 필요)

### 6.2 위치 수정
- [x] "가게 위치" 카드 클릭 → EditAddressFragment 이동 (코드 구현 완료)
- [ ] 지도에서 위치 선택 (런타임 테스트 필요)

### 6.3 가게 정보 수정
- [x] "가게 정보" 카드 클릭 → EditStoreInfoScreen 전환 (코드 구현 완료)
- [ ] 정보 수정 후 반영 확인 (런타임 테스트 필요)

### 6.4 메뉴 수정
- [x] "가게 메뉴" 카드 클릭 → EditMenuScreen 이동 (코드 구현 완료)
- [ ] 메뉴 추가/수정/삭제 (런타임 테스트 필요)

### 6.5 최종 제출
- [x] API 호출 로직 구현 완료
- [ ] 수정 완료 후 정상 동작 확인 (런타임 테스트 필요)

## 7. 참고 사항

### 7.1 UserStoreMenuModel.count 필드
`domain/src/main/java/com/threedollar/domain/home/data/store/UserStoreMenuModel.kt`:
```kotlin
data class UserStoreMenuModel(
    val category: CategoryModel = CategoryModel(),
    val menuId: Int = 0,
    val name: String? = "-",
    val price: String? = "-",
    val count: Int? = null  // 지원됨
)
```

### 7.2 MenuModelRequest.count 필드
`domain/src/main/java/com/threedollar/domain/home/request/MenuModelRequest.kt`:
```kotlin
data class MenuModelRequest(
    val name: String,
    val count: Int? = null,  // 지원됨
    val price: Int? = null,
    val category: String,
    val description: String? = null
)
```
