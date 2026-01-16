# EditStoreViewModel 라이프사이클 리팩토링 계획

## 현재 문제
- `EditStoreViewModel`이 `activityViewModels()`로 Activity 스코프에 바인딩됨
- `EditStoreFragment`가 닫혀도 ViewModel 데이터가 남아있음
- 다른 가게 수정 시 이전 가게 데이터가 보일 수 있음

## 현재 사용처
| 파일 | 용도 | ViewModel 접근 방식 |
|------|------|---------------------|
| `EditStoreFragment.kt` | 메인 편집 화면 | `activityViewModels()` |
| `EditAddressFragment.kt` | 위치 수정 | `activityViewModels()` |
| `AddStoreMenuCategoryDialogFragment.kt` | 카테고리 추가 다이얼로그 | `activityViewModels()` |
| `StoreAddNaverMapFragment.kt` | 지도 | `activityViewModels()` |

## 리팩토링 옵션

### Option 1: EditAddressFragment를 BottomSheetDialogFragment로 변경
**변경 범위:** 중간

**장점:**
- `EditStoreFragment`의 child로 동작
- `viewModels()` 사용 가능하여 라이프사이클 자동 관리
- Dialog/BottomSheet가 UX적으로도 자연스러움

**수정 내용:**
1. `EditAddressFragment` → `EditAddressBottomSheetDialogFragment`로 변경
2. `EditStoreFragment`에서 `viewModels()` 사용
3. 자식 Fragment/Dialog들은 `viewModels({ requireParentFragment() })` 사용
4. Navigation 방식을 `replaceFragment` → `show()` 다이얼로그로 변경

**예시 코드:**
```kotlin
// EditStoreFragment.kt
private val editStoreViewModel: EditStoreViewModel by viewModels()

// EditAddressBottomSheetDialogFragment.kt
private val editStoreViewModel: EditStoreViewModel by viewModels({ requireParentFragment() })
```

### Option 2: 위치 수정을 Compose 화면으로 통합
**변경 범위:** 큼

**장점:**
- 가장 깔끔한 아키텍처
- 모든 화면이 `EditStoreFragment` 내부 Compose로 통합
- Fragment 간 데이터 공유 문제 완전 해결

**수정 내용:**
1. `EditAddressFragment` 삭제
2. `EditScreen.Location`에 해당하는 Compose 화면 구현 (EditLocationScreen)
3. Naver Map Compose 통합 필요
4. 현재 `EditAddressFragment`의 기능을 Compose로 이식

**현재 EditScreen enum:**
```kotlin
enum class EditScreen {
    Selection,  // 메인 선택 화면
    Location,   // 위치 수정 (현재는 EditAddressFragment로 이동)
    StoreInfo,  // 가게 정보
    StoreMenu   // 메뉴
}
```

### Option 3: ResetState Intent 추가 (Quick Fix)
**변경 범위:** 작음

**장점:**
- 빠르게 적용 가능
- 기존 구조 유지

**단점:**
- 라이프사이클에 종속되지 않아 불안정
- Reset 호출을 빠뜨리면 버그 발생

**수정 내용:**
1. `EditStoreContract.Intent.ResetState` 추가
2. `EditStoreFragment.initializeStoreData()`에서 storeId 다르면 reset 호출

## 권장 순서
1. **단기:** Option 3 (ResetState) - 즉시 적용 가능
2. **중기:** Option 1 (BottomSheet) - 안정적인 라이프사이클 관리
3. **장기:** Option 2 (Compose 통합) - 가장 깔끔한 아키텍처

## 관련 파일
- `app/src/main/java/com/zion830/threedollars/ui/edit/ui/EditStoreFragment.kt`
- `app/src/main/java/com/zion830/threedollars/ui/edit/ui/EditAddressFragment.kt`
- `app/src/main/java/com/zion830/threedollars/ui/edit/viewModel/EditStoreViewModel.kt`
- `app/src/main/java/com/zion830/threedollars/ui/edit/viewModel/EditStoreContract.kt`
- `app/src/main/java/com/zion830/threedollars/ui/dialog/AddStoreMenuCategoryDialogFragment.kt`
- `app/src/main/java/com/zion830/threedollars/ui/map/ui/StoreAddNaverMapFragment.kt`

## 작성일
2026-01-17
