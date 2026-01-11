# Phase 2 구현 상세 문서

## 1. 개요

### 1.1 Phase 2의 목적
Edit Store 화면에서 사용할 Compose 공통 컴포넌트들을 `EditStoreComponents.kt`에 구현.
Phase 3에서 화면을 조립할 때 사용할 재사용 가능한 UI 컴포넌트 라이브러리 구축.

### 1.2 완료된 작업
- 디렉토리 생성: `ui/edit/ui/compose/`
- 문자열 리소스 추가: `strings.xml`에 10개 신규 문자열
- 공통 컴포넌트 구현: 10개 Composable 함수
- 빌드 검증: `assembleDebug` 성공

---

## 2. 생성된 파일

### 2.1 EditStoreComponents.kt
**경로:** `app/src/main/java/com/zion830/threedollars/ui/edit/ui/compose/EditStoreComponents.kt`

**구현된 컴포넌트:**

| 컴포넌트 | 용도 | 주요 파라미터 |
|---------|------|-------------|
| `EditStoreTopBar` | 상단 네비게이션 바 | title, onCloseClick |
| `EditStoreHeader` | 헤더 영역 + 수정 배지 | title, subtitle, changedCount |
| `EditSectionCard` | 섹션 카드 (위치/정보/메뉴) | title, description, iconResId, isModified |
| `SelectableChip` | 선택 칩 (결제방식/형태) | label, isSelected, showCheckIcon |
| `DayCircleButton` | 요일 선택 버튼 | day, isSelected |
| `TimePickerRow` | 시간 선택 행 | startTime, endTime |
| `EditStoreTextField` | 텍스트 입력 필드 | value, onValueChange, placeholder |
| `SectionHeader` | 섹션 제목 | title, showMultiSelectHint |
| `ExitConfirmDialog` | 닫기 확인 다이얼로그 | onDismiss, onConfirm |
| `MainButton` | 하단 버튼 | text, enabled |

### 2.2 strings.xml 추가된 문자열
```xml
<string name="edit_store_select_title">제보할 정보를\n선택해 주세요</string>
<string name="edit_store_select_subtitle">작은 제보로 가게 정보 완성에 힘을 보태요</string>
<string name="edit_store_changed_count">%d개의 수정된 정보가 있어요</string>
<string name="edit_store_complete">수정 완료</string>
<string name="edit_store_section_location">가게 위치</string>
<string name="edit_store_section_info">가게 정보</string>
<string name="edit_store_section_menu">가게 메뉴</string>
<string name="edit_store_menu_count">%d개의 제보된 메뉴가 있어요</string>
<string name="edit_store_no_info">제보된 정보가 없어요</string>
<string name="edit_store_exit_message">수정된 정보가 있어요.\n지금까지 입력한 정보가 저장되지 않아요.</string>
```

---

## 3. 사용된 디자인 시스템

### 3.1 Color.kt 색상 사용
```kotlin
import base.compose.Gray100    // #0F0F0F - 메인 텍스트
import base.compose.Gray70     // #5A5A5A - 부제 텍스트
import base.compose.Gray50     // #969696 - 설명 텍스트
import base.compose.Gray30     // #D0D0D0 - 테두리/비선택
import base.compose.Gray10     // #F4F4F4 - 입력 배경
import base.compose.Pink       // #FF858F - 메인 액션
import base.compose.Green100   // #F1FFF8 - 수정 배지 배경
import base.compose.Green      // #00C667 - 수정 배지 텍스트
import base.compose.Red        // #FF5C43 - 위험/나가기
import base.compose.ColorWhite // #FFFFFF - 흰색
```

### 3.2 Font.kt 폰트 사용
```kotlin
import base.compose.PretendardFontFamily

// 사용 예시
Text(
    text = "제목",
    fontFamily = PretendardFontFamily,
    fontWeight = FontWeight.W700,
    fontSize = 24.sp
)
```

### 3.3 사용된 아이콘 리소스
- `ic_close` - 닫기 버튼
- `ic_check` - 체크 아이콘 (수정됨 표시, 선택 체크)
- `ic_marker` - 위치 아이콘
- `ic_info` - 정보 아이콘
- `ic_completion_menu` - 메뉴 아이콘 (Phase 3에서 사용 예정)

---

## 4. 컴포넌트 구현 패턴

### 4.1 기본 Composable 구조
```kotlin
@Composable
fun ComponentName(
    // 필수 파라미터
    param1: Type,
    param2: Type,
    // 콜백
    onClick: () -> Unit,
    // 선택 파라미터
    modifier: Modifier = Modifier,
    optionalParam: Boolean = false
) {
    // UI 구현
}
```

### 4.2 Preview 패턴
```kotlin
@Preview(showBackground = true)
@Composable
private fun ComponentNamePreview() {
    ComponentName(
        param1 = "샘플 값",
        onClick = {}
    )
}
```

### 4.3 조건부 스타일링 패턴
```kotlin
// 선택 상태에 따른 색상 변경
.background(if (isSelected) Pink else Gray10)

// 선택 상태에 따른 테두리
.border(
    width = 1.dp,
    color = if (isSelected) Pink else Gray30,
    shape = RoundedCornerShape(8.dp)
)
```

---

## 5. 빌드 이슈 및 해결

### 5.1 DayOfTheWeekType import 오류
**문제:** `home.domain.model.store.DayOfTheWeekType` 경로 불일치

**해결:** 
```kotlin
// Before
import home.domain.model.store.DayOfTheWeekType

// After
import com.threedollar.domain.home.data.store.DayOfTheWeekType
```

### 5.2 when 표현식 exhaustive 오류
**문제:** DayOfTheWeekType에 추가 case가 있을 수 있음

**해결:**
```kotlin
val dayLabel = when (day) {
    DayOfTheWeekType.MONDAY -> "월"
    // ... 다른 요일들
    else -> ""  // 추가
}
```

### 5.3 ic_edit 아이콘 미존재
**문제:** `DesignSystemR.drawable.ic_edit` 리소스 없음

**해결:** `ic_info` 아이콘으로 대체

---

## 6. Phase 3를 위한 준비 완료

### 6.1 Phase 3에서 활용 방법
```kotlin
@Composable
fun EditStoreScreen(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit
) {
    Column {
        EditStoreTopBar(
            title = stringResource(CommonR.string.edit_store),
            onCloseClick = { onIntent(Intent.ShowExitConfirmDialog) }
        )
        
        EditStoreHeader(
            title = stringResource(CommonR.string.edit_store_select_title),
            subtitle = stringResource(CommonR.string.edit_store_select_subtitle),
            changedCount = state.totalChangedCount
        )
        
        EditSectionCard(
            title = stringResource(CommonR.string.edit_store_section_location),
            description = state.address,
            iconResId = DesignSystemR.drawable.ic_marker,
            isModified = state.hasLocationChanges,
            onClick = { onIntent(Intent.NavigateToScreen(EditScreen.Location)) }
        )
        
        // ... 다른 섹션 카드들
        
        MainButton(
            text = stringResource(CommonR.string.edit_store_complete),
            enabled = state.isSubmitEnabled,
            onClick = { onIntent(Intent.SubmitChanges) }
        )
    }
    
    if (state.showExitConfirmDialog) {
        ExitConfirmDialog(
            onDismiss = { onIntent(Intent.HideExitConfirmDialog) },
            onConfirm = { onIntent(Intent.ConfirmExit) }
        )
    }
}
```

### 6.2 다음 단계
- **Phase 3**: EditStoreScreen.kt, EditStoreInfoScreen.kt 화면 구현
- 공통 컴포넌트를 조합하여 실제 화면 구성
- EditStoreFragment.kt (Compose 호스트) 생성

---

## 7. 파일 경로 요약

| 파일 | 경로 |
|-----|-----|
| EditStoreComponents.kt | `app/.../ui/edit/ui/compose/EditStoreComponents.kt` |
| strings.xml | `core/common/src/main/res/values/strings.xml` |
| Color.kt | `core/designsystem/src/main/java/base/compose/Color.kt` |
| Font.kt | `core/designsystem/src/main/java/base/compose/Font.kt` |
| DayOfTheWeekType | `domain/src/main/java/com/threedollar/domain/home/data/store/DayOfTheWeekType.kt` |
