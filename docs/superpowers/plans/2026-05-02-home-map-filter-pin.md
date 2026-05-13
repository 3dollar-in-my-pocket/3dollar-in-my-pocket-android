# 홈 상단 필터 Compose 전환 및 서버드리븐 보강 Implementation Plan

> **For agentic workers:** REQUIRED: Use `superpowers:subagent-driven-development` if available, or `superpowers:executing-plans` to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 현재 브랜치의 PR 커밋 `37cff3d4ed8ff9c8cd8cbe845afcd954c958cf00`가 만든 홈 상단 필터 서버드리븐 구조를 최대한 보존하면서, 필터 렌더링만 `ComposeView + LazyRow`로 전환하고 TH-1138/API 계약 누락과 기존 필터 회귀를 보강한다.

**Architecture:** 홈 화면 전체는 계속 XML/ViewBinding 기반으로 둔다. `HomeViewModel`의 서버드리븐 필터 상태(`filterCells`, `HomeFilterCellType`, `HomeFilterBar`)와 data/domain/network 매핑은 PR 구현을 기준으로 살리고, `RecyclerView + HomeFilterAdapter + item_home_filter_chip.xml`만 Compose 렌더러로 교체한다. `/api/v1/screen/home`에서 내려온 `HOME_FILTER` 순서를 그대로 렌더링하고, 주변 가게 조회는 기존 `/api/v4/stores/around`에 서버 `paramKey`/`paramValue`와 레거시 필터 상태를 함께 반영한다.

**Tech Stack:** Kotlin, XML/ViewBinding, Jetpack Compose interop(`ComposeView`), Compose `LazyRow`, `collectAsStateWithLifecycle`, Coil3 `AsyncImage`, Retrofit `@QueryMap`, MVVM, Hilt, JUnit4.

---

## 사용한 기준

- 현재 브랜치: `feature/TH-1134-filter-chip`
- 기준 PR 커밋: `37cff3d4ed8ff9c8cd8cbe845afcd954c958cf00` (`TH-1134 : 홈 화면 필터 서버 드리븐 UI 적용`)
- 서버 Jira/PDF: `docs/superpowers/plans/[#TH-1138] ... 서버드리븐 UI 적용.pdf`
- API 초안: `docs/superpowers/plans/api.txt`
- 기존 계획: `docs/superpowers/plans/2026-05-02-home-map-filter-pin.md`
- Compose skill 참고: `@compose-expert`
  - `references/view-composition.md`: XML 화면 안 `ComposeView` 생명주기
  - `references/lists-scrolling.md`: `LazyRow` key/contentType
  - `references/state-management.md`: ViewModel StateFlow state hoisting
  - `references/accessibility.md`: semantics, contentDescription, touch target

## 핵심 판단

PR 방향은 맞다. 서버드리븐 필터 도메인 모델, mapper, repository, ViewModel 상태, clickLog 처리, `/api/v1/screen/home` 호출은 새로 갈아엎지 않는다.

바꿀 부분은 필터 UI 렌더링 계층이다. 현재 PR은 `RecyclerView` 어댑터로 서버 칩을 렌더링하지만, 이 영역은 상태가 단순하고 item layout이 서버 스타일 확장에 취약하다. Compose로 바꾸면 `additionalText`, 이미지 width/height, 동적 style, 6dp spacing, accessibility를 더 작은 표면에서 처리할 수 있다.

API 보강은 UI 전환보다 먼저 또는 같은 커밋 안에서 처리한다. 현재 PR 기준으로 `filterCertifiedStores`가 주변 가게 query에서 누락되는 회귀가 있고, 서버 계약의 `viewLog`와 chip `additionalText`가 response/mapper에서 빠져 있다.

## 유지할 PR 변경

- `core/network/src/main/java/com/threedollar/network/api/ServerApi.kt`
  - `GET /api/v1/screen/home`
  - `/api/v4/stores/around`의 `@QueryMap dynamicParams`
- `core/network/src/main/java/com/threedollar/network/data/screen/HomeFilterScreenResponse.kt`
- `data/src/main/java/com/threedollar/data/screen/HomeFilterScreenMapper.kt`
- `core/common/src/main/java/com/threedollar/common/serverdriven/model/HomeFilterModels.kt`
- `core/common/src/main/java/com/threedollar/common/serverdriven/model/ServerDrivenModels.kt`
- `core/common/src/main/java/com/threedollar/common/analytics/SDClickLogger.kt`
- `app/src/main/java/com/zion830/threedollars/ui/home/data/HomeFilterCellType.kt`
- `app/src/main/java/com/zion830/threedollars/ui/home/data/HomeUIState.kt`
- `app/src/main/java/com/zion830/threedollars/ui/home/viewModel/HomeViewModel.kt`의 서버드리븐 상태 흐름
- `HomeFragment`, `HomeListViewFragment`의 카테고리 dialog, deep link, list/map 전환 흐름

## 제거 또는 치환할 PR 변경

- 제거: `app/src/main/java/com/zion830/threedollars/ui/home/adapter/HomeFilterAdapter.kt`
- 제거: `app/src/main/res/layout/item_home_filter_chip.xml`
- 치환: `fragment_home.xml`, `fragment_home_list_view.xml`의 `RecyclerView filterRecyclerView`
- 치환: 두 Fragment의 `initFilterAdapter()`와 `filterAdapter.submitList(...)`

## 작업 범위

포함한다.

- 홈 지도와 홈 리스트의 상단 필터 row를 Compose로 전환한다.
- 서버 `HOME_FILTER` 섹션의 bar 순서를 유지한다.
- `CATEGORY_BAR`, `RADIO_BAR`, `ACTION_BAR`를 기존 PR 모델 그대로 렌더링한다.
- 선택 카테고리 칩은 음식 종류 칩 옆 별도 칩으로 유지한다.
- 칩 간격은 Figma 기준 `6dp`로 맞춘다.
- chip `additionalText`, image style width/height, surface border/background를 반영한다.
- `filterCertifiedStores` query 누락을 복구한다.
- `/api/v1/screen/home` 실패 fallback에서 기존 필터 기능을 최대한 보존한다.
- 서버 clickLog는 기존 PR의 `SDClickLogger` 흐름을 유지한다.
- 서버 `viewLog`는 response/model/mapper에 반영하고, 실제 전송은 기존 `BaseFragment` page view와 중복되지 않게 별도 판단 지점으로 둔다.

제외한다.

- 홈 화면 전체 Compose 전환
- Naver 지도/마커 정책 구현
- 새 Compose/이미지 라이브러리 추가
- 서버 API 구현
- 관련 없는 디자인 리소스 정리
- 대규모 ViewModel 리팩터링

## 파일 구조

Create:

- `app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeFilterChips.kt`
  - `HomeFilterCellType` 리스트를 `LazyRow`로 렌더링한다.
  - 서버 style, image, text, click action을 Compose에서 처리한다.
- `app/src/main/java/com/zion830/threedollars/ui/home/data/HomeFilterQueryParamsBuilder.kt`
  - `HomeUIState`와 `HomeFilterBar`에서 `/api/v4/stores/around` query map을 만드는 순수 로직을 분리한다.
- `data/src/test/java/com/threedollar/data/screen/HomeFilterScreenMapperTest.kt`
  - `viewLog`, `additionalText`, image style, clickLog extra mapping을 검증한다.
- `app/src/test/java/com/zion830/threedollars/ui/home/data/HomeFilterQueryParamsBuilderTest.kt`
  - `filterCertifiedStores`, `filterOpenStatuses`, `filterConditions`, `sortType` query map을 검증한다.

Modify:

- `app/src/main/res/layout/fragment_home.xml`
  - `RecyclerView filterRecyclerView`를 `ComposeView filterComposeView`로 교체한다.
  - `tv_retry_search` constraints를 `filterComposeView` 기준으로 수정한다.
- `app/src/main/res/layout/fragment_home_list_view.xml`
  - `RecyclerView filterRecyclerView`를 `ComposeView filterComposeView`로 교체한다.
  - title/certified/list constraints를 `filterComposeView` 기준으로 수정한다.
- `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt`
  - `HomeFilterAdapter` import/field/collection 제거
  - `initFilterComposeView()` 추가
  - `filterDeepLink` 수집은 유지
- `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeListViewFragment.kt`
  - `HomeFilterAdapter` import/field/collection 제거
  - `initFilterComposeView()` 추가
  - 리스트용 카테고리 clickLog 처리 보강
- `app/src/main/java/com/zion830/threedollars/ui/home/viewModel/HomeViewModel.kt`
  - `collectDynamicParams()`를 `HomeFilterQueryParamsBuilder.build(...)`로 대체
  - fallback filter cell을 기존 기능 수준으로 확장
  - 서버 category clickLog를 지도/리스트에서 공통으로 쓰도록 정리
- `core/network/src/main/java/com/threedollar/network/data/screen/HomeFilterScreenResponse.kt`
  - `data.viewLog`
  - chip `additionalText`
  - 필요 시 `paramValue` 안전 파싱
- `data/src/main/java/com/threedollar/data/screen/HomeFilterScreenMapper.kt`
  - `viewLog`, `additionalText`, image style mapping 보강
- `core/common/src/main/java/com/threedollar/common/serverdriven/model/HomeFilterModels.kt`
  - `HomeFilterScreenModel.viewLog` 추가
- `core/common/src/main/java/com/threedollar/common/serverdriven/model/ServerDrivenModels.kt`
  - 필요 시 `SDViewLogModel` 추가

Delete after references are gone:

- `app/src/main/java/com/zion830/threedollars/ui/home/adapter/HomeFilterAdapter.kt`
- `app/src/main/res/layout/item_home_filter_chip.xml`

---

## Chunk 1: API 계약과 query 회귀 보강

### Task 1: Mapper 테스트 추가

**Files:**

- Create: `data/src/test/java/com/threedollar/data/screen/HomeFilterScreenMapperTest.kt`
- Modify: `core/network/src/main/java/com/threedollar/network/data/screen/HomeFilterScreenResponse.kt`
- Modify: `core/common/src/main/java/com/threedollar/common/serverdriven/model/HomeFilterModels.kt`
- Modify: `core/common/src/main/java/com/threedollar/common/serverdriven/model/ServerDrivenModels.kt`
- Modify: `data/src/main/java/com/threedollar/data/screen/HomeFilterScreenMapper.kt`

- [ ] **Step 1: 실패 테스트 작성**

`HomeFilterScreenMapperTest`에 다음 케이스를 먼저 추가한다.

```kotlin
@Test
fun homeFilterMapper_mapsViewLogAdditionalTextAndImageStyle() {
    val response = HomeFilterScreenResponse(
        viewLog = HomeFilterViewLogResponse(screenName = "home"),
        sections = listOf(
            HomeFilterSectionResponse(
                type = "HOME_FILTER",
                bars = listOf(
                    HomeFilterBarResponse(
                        type = "CATEGORY_BAR",
                        categoriesFilter = HomeFilterChipResponse(
                            image = HomeFilterImageResponse(
                                url = "https://example.com/icon.png",
                                style = HomeFilterImageStyleResponse(width = 18.0, height = 18.0),
                            ),
                            text = HomeFilterTextResponse(text = "음식 종류", isHtml = false, fontColor = "#5A5A5A"),
                            additionalText = HomeFilterTextResponse(text = "NEW", isHtml = false, fontColor = "#FF858F"),
                        ),
                    ),
                ),
            ),
        ),
    )

    val model = response.asModel()
    val section = model.sections.single() as HomeScreenSection.HomeFilterSectionModel
    val categoryBar = section.bars.single() as HomeFilterBar.CategoryBar

    assertEquals("home", model.viewLog?.screenName)
    assertEquals("NEW", categoryBar.categoriesFilter.additionalText?.text)
    assertEquals(18.0, categoryBar.categoriesFilter.image?.style?.width)
    assertEquals(18.0, categoryBar.categoriesFilter.image?.style?.height)
}
```

- [ ] **Step 2: 테스트 실패 확인**

Run:

```bash
./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.HomeFilterScreenMapperTest"
```

Expected: `viewLog` 또는 `additionalText` 필드가 없어 compile fail.

- [ ] **Step 3: response/model/mapper 보강**

반영할 모델 형태:

```kotlin
data class HomeFilterScreenResponse(
    @SerializedName("sections")
    val sections: List<HomeFilterSectionResponse>? = emptyList(),
    @SerializedName("viewLog")
    val viewLog: HomeFilterViewLogResponse? = null,
)

data class HomeFilterViewLogResponse(
    @SerializedName("screenName")
    val screenName: String? = null,
)

data class HomeFilterChipResponse(
    @SerializedName("image")
    val image: HomeFilterImageResponse? = null,
    @SerializedName("text")
    val text: HomeFilterTextResponse? = null,
    @SerializedName("additionalText")
    val additionalText: HomeFilterTextResponse? = null,
    @SerializedName("style")
    val style: HomeFilterChipStyleResponse? = null,
)
```

공통 모델:

```kotlin
data class SDViewLogModel(
    val screenName: String,
)

data class HomeFilterScreenModel(
    val sections: List<HomeScreenSection> = emptyList(),
    val viewLog: SDViewLogModel? = null,
)
```

mapper:

```kotlin
fun HomeFilterScreenResponse.asModel(): HomeFilterScreenModel = HomeFilterScreenModel(
    sections = sections.orEmpty().map { it.asModel() },
    viewLog = viewLog?.takeIf { !it.screenName.isNullOrBlank() }?.let {
        SDViewLogModel(screenName = it.screenName.orEmpty())
    },
)

private fun HomeFilterChipResponse.asModel(): SDChipModel = SDChipModel(
    image = image?.takeIf { !it.url.isNullOrBlank() }?.asModel(),
    text = text.asModel(),
    additionalText = additionalText?.asModel(),
    style = style?.let { SDSurfaceStyleModel(backgroundColor = it.backgroundColor, border = it.border?.asModel()) },
)
```

- [ ] **Step 4: 테스트 통과 확인**

Run:

```bash
./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.HomeFilterScreenMapperTest"
```

Expected: PASS.

### Task 2: 주변 가게 query map builder 분리

**Files:**

- Create: `app/src/main/java/com/zion830/threedollars/ui/home/data/HomeFilterQueryParamsBuilder.kt`
- Create: `app/src/test/java/com/zion830/threedollars/ui/home/data/HomeFilterQueryParamsBuilderTest.kt`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/viewModel/HomeViewModel.kt`

- [ ] **Step 1: 실패 테스트 작성**

검증할 규칙:

- `filterCertifiedStores=false`이면 query에서 생략한다.
- `filterCertifiedStores=true`이면 `filterCertifiedStores=true`를 보낸다.
- 서버 radio bar의 `filterOpenStatuses=OPEN`, `filterConditions=RECENT_ACTIVITY`, `sortType=...`는 선택된 option의 `paramValue`를 보낸다.
- `targetStores`는 현재 typed query(`@Query("targetStores")`)가 따로 있으므로 dynamic map에서 중복 전송하지 않는다.
- 서버 screen 로딩 실패 상태에서도 최소 `sortType`과 `filterCertifiedStores`는 유지한다.

- [ ] **Step 2: builder 구현**

```kotlin
internal object HomeFilterQueryParamsBuilder {
    fun build(
        state: HomeUIState,
        bars: List<HomeFilterBar>,
    ): Map<String, String> {
        val params = mutableMapOf<String, String>()

        bars.filterIsInstance<HomeFilterBar.RadioBar>().forEach { bar ->
            if (bar.paramKey == "targetStores") return@forEach
            val selectedIndex = state.radioSelection[bar.paramKey] ?: 0
            val value = bar.options.getOrNull(selectedIndex)?.paramValue ?: return@forEach
            params[bar.paramKey] = value
        }

        if (params["sortType"] == null) {
            params["sortType"] = state.homeSortType.name
        }
        if (state.filterCertifiedStores) {
            params["filterCertifiedStores"] = "true"
        }

        return params
    }
}
```

- [ ] **Step 3: ViewModel 연결**

`HomeViewModel.fetchAroundStores()`에서:

```kotlin
dynamicParams = HomeFilterQueryParamsBuilder.build(
    state = state,
    bars = allBars(),
),
```

기존 private `collectDynamicParams()`는 삭제한다.

- [ ] **Step 4: 테스트 통과 확인**

Run:

```bash
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.data.HomeFilterQueryParamsBuilderTest"
```

Expected: PASS.

- [ ] **Step 5: 커밋**

```bash
git add core/network/src/main/java/com/threedollar/network/data/screen/HomeFilterScreenResponse.kt \
  core/common/src/main/java/com/threedollar/common/serverdriven/model/HomeFilterModels.kt \
  core/common/src/main/java/com/threedollar/common/serverdriven/model/ServerDrivenModels.kt \
  data/src/main/java/com/threedollar/data/screen/HomeFilterScreenMapper.kt \
  data/src/test/java/com/threedollar/data/screen/HomeFilterScreenMapperTest.kt \
  app/src/main/java/com/zion830/threedollars/ui/home/data/HomeFilterQueryParamsBuilder.kt \
  app/src/test/java/com/zion830/threedollars/ui/home/data/HomeFilterQueryParamsBuilderTest.kt \
  app/src/main/java/com/zion830/threedollars/ui/home/viewModel/HomeViewModel.kt
git commit -m "fix: preserve home filter server query contract"
```

---

## Chunk 2: 홈 필터 Compose 렌더러 추가

### Task 3: Stateless Compose 컴포넌트 작성

**Files:**

- Create: `app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeFilterChips.kt`

- [ ] **Step 1: composable 골격 추가**

```kotlin
@Composable
fun HomeFilterChipsRow(
    cells: List<HomeFilterCellType>,
    onCategoryClick: () -> Unit,
    onRadioClick: (paramKey: String, optionIndex: Int) -> Unit,
    onActionClick: (SDLinkModel) -> Unit,
    onCloseSelectedCategoryClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = contentPadding,
    ) {
        items(
            items = cells,
            key = { it.stableHomeFilterKey() },
            contentType = { it.homeFilterContentType() },
        ) { cell ->
            HomeFilterCell(
                cell = cell,
                onCategoryClick = onCategoryClick,
                onRadioClick = onRadioClick,
                onActionClick = onActionClick,
                onCloseSelectedCategoryClick = onCloseSelectedCategoryClick,
            )
        }
    }
}
```

- [ ] **Step 2: 안정 key/contentType 추가**

`LazyRow`는 서버 응답과 선택 카테고리에 따라 리스트가 변하므로 index key를 쓰지 않는다.

```kotlin
private fun HomeFilterCellType.stableHomeFilterKey(): String = when (this) {
    is HomeFilterCellType.Chip -> when (val action = action) {
        ChipAction.OpenCategoryFilter -> "category:${chip.text.text}"
        is ChipAction.SelectRadio -> "radio:${action.paramKey}:${action.optionIndex}:${chip.text.text}"
        is ChipAction.DeepLink -> "link:${action.link.type}:${action.link.link}:${chip.text.text}"
    }
    is HomeFilterCellType.SelectedCategoryChip -> "selected-category:${current?.fontColor}:${chip.text.text}:${chip.image?.url}"
    is HomeFilterCellType.Button -> "button:${button.link?.type}:${button.link?.link}:${button.text.text}"
}

private fun HomeFilterCellType.homeFilterContentType(): String = when (this) {
    is HomeFilterCellType.Chip -> "chip"
    is HomeFilterCellType.SelectedCategoryChip -> "selected-category-chip"
    is HomeFilterCellType.Button -> "button"
}
```

- [ ] **Step 3: chip UI 구현**

요구사항:

- 높이 `34.dp`
- radius `10.dp`
- horizontal padding `10.dp`
- image와 text 간격 `6.dp`
- item 간격은 `LazyRow`에서 `6.dp`
- `SDChipModel.style.backgroundColor`, `style.border.color`, `style.border.width` 반영
- invalid color는 기본값으로 fallback
- `SDChipModel.image.style.width/height` 반영, 없으면 `20.dp`
- `SDChipModel.additionalText`가 있으면 text 뒤에 이어서 표시
- HTML text는 기존 어댑터처럼 `HtmlCompat.fromHtml(...).toString()` 수준으로 처리

색상 파싱은 기존 유틸을 재사용한다.

```kotlin
import com.threedollar.common.compose.utils.toColor
```

- [ ] **Step 4: accessibility 처리**

요구사항:

- 일반 칩은 `Role.Button` semantics를 가진다.
- 이미지가 텍스트를 보조하는 장식이면 `contentDescription = null`.
- 선택 카테고리 닫기 영역은 "카테고리 필터 해제" label을 가진다.
- 닫기 아이콘의 시각 크기는 작게 유지해도, 클릭 영역은 최소 `34.dp` 높이 안에서 충분히 확보한다.

- [ ] **Step 5: Preview는 선택 사항**

필요하면 같은 파일에 private preview를 둔다. 서버 모델을 직접 만들어 확인하되 production code에 영향을 주지 않는다.

---

## Chunk 3: Fragment/XML interop 전환

### Task 4: XML의 RecyclerView를 ComposeView로 교체

**Files:**

- Modify: `app/src/main/res/layout/fragment_home.xml`
- Modify: `app/src/main/res/layout/fragment_home_list_view.xml`

- [ ] **Step 1: 홈 지도 XML 수정**

`fragment_home.xml`의 `filterRecyclerView`를 다음 형태로 교체한다.

```xml
<androidx.compose.ui.platform.ComposeView
    android:id="@+id/filterComposeView"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_marginTop="14dp"
    app:layout_constraintEnd_toEndOf="@id/layout_address"
    app:layout_constraintStart_toStartOf="@id/layout_address"
    app:layout_constraintTop_toBottomOf="@id/layout_address" />
```

`tv_retry_search`의 constraints는 `@id/filterComposeView` 기준으로 바꾼다.

- [ ] **Step 2: 홈 리스트 XML 수정**

`fragment_home_list_view.xml`도 같은 방식으로 `filterComposeView`를 추가한다. 리스트 화면은 기존 좌우 여백 `22dp`를 `ComposeView` padding으로 둘지, composable `contentPadding`으로 둘지 하나로만 둔다. 추천은 XML `paddingHorizontal=22dp`를 유지하지 않고 `ComposeView` constraints는 full width, `HomeFilterChipsRow(contentPadding = PaddingValues(horizontal = 22.dp))`로 처리하는 방식이다.

### Task 5: Fragment에서 Compose content 설정

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt`
- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeListViewFragment.kt`

- [ ] **Step 1: adapter field/import 제거**

제거:

- `HomeFilterAdapter` import
- `private lateinit var filterAdapter`
- `LinearLayoutManager` import가 필터에서만 쓰이면 함께 제거
- `collectFilterCells()` 또는 `filterAdapter.submitList(...)`

- [ ] **Step 2: 홈 지도 ComposeView 설정**

```kotlin
private fun initFilterComposeView() {
    binding.filterComposeView.setViewCompositionStrategy(
        ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
    )
    binding.filterComposeView.setContent {
        AppTheme {
            val cells by viewModel.filterCells.collectAsStateWithLifecycle()
            HomeFilterChipsRow(
                cells = cells,
                onCategoryClick = {
                    viewModel.sendClickCategoryFilter()
                    showSelectCategoryDialog()
                },
                onRadioClick = viewModel::selectRadioOption,
                onActionClick = viewModel::handleActionLink,
                onCloseSelectedCategoryClick = viewModel::closeSelectedCategory,
            )
        }
    }
}
```

`initView()`에서 `initFilterAdapter()` 대신 `initFilterComposeView()`를 호출한다.

- [ ] **Step 3: 홈 리스트 ComposeView 설정**

리스트 화면도 같은 composable을 사용한다. 카테고리 클릭 로그는 하드코딩된 리스트 전용 로그보다 서버 `categoriesFilterClickLog`를 우선 사용하도록 ViewModel 함수를 정리한다.

권장 형태:

```kotlin
onCategoryClick = {
    viewModel.sendClickCategoryFilter()
    showSelectCategoryDialog()
}
```

리스트에서 별도 objectId가 반드시 필요하다는 기획/분석 요구가 있으면 `sendClickCategoryFilter(entryPoint = HOME_LIST)`처럼 명시 인자를 추가하되, 서버 clickLog가 있으면 서버 값을 우선한다.

- [ ] **Step 4: deep link flow는 유지**

`filterDeepLink` 수집과 `handleFilterDeepLink()`는 기존 PR 코드를 유지한다. Compose click callback은 ViewModel 함수만 호출한다.

### Task 6: RecyclerView 어댑터 제거

**Files:**

- Delete: `app/src/main/java/com/zion830/threedollars/ui/home/adapter/HomeFilterAdapter.kt`
- Delete: `app/src/main/res/layout/item_home_filter_chip.xml`

- [ ] **Step 1: 참조 확인**

Run:

```bash
rg "HomeFilterAdapter|item_home_filter_chip|filterRecyclerView"
```

Expected: 참조 없음.

- [ ] **Step 2: 파일 삭제**

참조가 모두 사라진 뒤 삭제한다.

---

## Chunk 4: fallback과 기존 기능 보존

### Task 7: fallback filter cells 확장

**Files:**

- Modify: `app/src/main/java/com/zion830/threedollars/ui/home/viewModel/HomeViewModel.kt`
- Test: `app/src/test/java/com/zion830/threedollars/ui/home/data/HomeFilterQueryParamsBuilderTest.kt` 또는 별도 pure helper test

- [ ] **Step 1: 현재 fallback 문제 확인**

현재 `makeFallbackFilterCells()`는 `음식 종류`와 선택 카테고리만 만든다. `/api/v1/screen/home` 실패 시 기존 `최근 활동`, `거리순`, `사장님 직영점만`, `영업 중` 등 필터 표면이 사라진다.

- [ ] **Step 2: fallback 기준 정의**

fallback은 최소 다음 칩을 만든다.

- 음식 종류
- 선택된 카테고리 칩
- 영업 중
- 최근 활동
- 거리순/인기순 또는 현재 sort toggle
- 사장님 직영점만

서버가 없는 상태의 fallback clickLog는 기존 `LogManager.sendEvent(...)` 레거시 로그를 사용한다.

- [ ] **Step 3: fallback 구현**

PR의 `HomeFilterCellType`을 그대로 재사용한다. 서버 bar가 없을 때도 `ChipAction.SelectRadio(paramKey, optionIndex)`가 동작하도록 fallback용 `HomeFilterBar.RadioBar` 리스트를 만들거나, `makeFallbackFilterCells()`와 `selectRadioOption()` 사이의 계약을 정리한다.

권장 방식은 fallback bar를 `filterSections`에 넣지 않고, `selectRadioOption()`이 fallback paramKey도 처리할 수 있게 하는 것이다. 단, ViewModel 복잡도가 커지면 `fallbackBars(state)` helper를 만들어 `allBars()`가 서버 bar 없을 때 fallback bar를 반환하도록 한다.

- [ ] **Step 4: fallback query 검증**

서버 screen 실패 상태에서도 `HomeFilterQueryParamsBuilder.build(state, bars = emptyList())`가 다음을 유지하는지 확인한다.

- `sortType`
- `filterCertifiedStores=true`일 때 `filterCertifiedStores`
- 레거시 `homeStoreType`은 typed `targetStores`로 계속 전달

---

## Chunk 5: 최종 검증

### Task 8: 정적 검증

- [ ] **Step 1: 참조 정리 확인**

Run:

```bash
rg "HomeFilterAdapter|item_home_filter_chip|filterRecyclerView"
```

Expected: 결과 없음.

- [ ] **Step 2: 단위 테스트**

Run:

```bash
./gradlew :data:testDebugUnitTest --tests "com.threedollar.data.screen.HomeFilterScreenMapperTest"
./gradlew :app:testDebugUnitTest --tests "com.zion830.threedollars.ui.home.data.HomeFilterQueryParamsBuilderTest"
```

Expected: PASS.

- [ ] **Step 3: Kotlin compile**

Run:

```bash
./gradlew :app:compileDebugKotlin
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: 리소스 compile**

Run:

```bash
./gradlew :app:processDebugResources
```

Expected: BUILD SUCCESSFUL.

### Task 9: 수동 QA

- [ ] 홈 지도 진입 시 필터 row가 보인다.
- [ ] 음식 종류 클릭 시 카테고리 bottom sheet가 열린다.
- [ ] 카테고리 선택 후 음식 종류 칩은 유지되고, 선택 카테고리 칩이 별도로 보인다.
- [ ] 선택 카테고리 칩의 닫기 버튼으로 카테고리가 해제된다.
- [ ] 영업 중/최근 활동/sort/사장님 필터 클릭 후 주변 가게 API query가 기대값으로 나간다.
- [ ] 홈 리스트 화면에서도 같은 필터 row가 보이고 동작한다.
- [ ] `filterCertifiedStores` 체크를 켠 뒤 주변 가게 API에 `filterCertifiedStores=true`가 포함된다.
- [ ] 서버 `/api/v1/screen/home` 실패 시에도 fallback 필터가 사라지지 않는다.
- [ ] 서버 chip image width/height가 UI에 반영된다.
- [ ] chip `additionalText`가 있는 응답에서 텍스트가 누락되지 않는다.
- [ ] TalkBack 기준으로 칩은 버튼으로 읽히고, 닫기 버튼은 카테고리 필터 해제로 읽힌다.

### Task 10: 최종 커밋

```bash
git add app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeFilterChips.kt \
  app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt \
  app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeListViewFragment.kt \
  app/src/main/res/layout/fragment_home.xml \
  app/src/main/res/layout/fragment_home_list_view.xml \
  app/src/main/java/com/zion830/threedollars/ui/home/viewModel/HomeViewModel.kt \
  app/src/main/java/com/zion830/threedollars/ui/home/data/HomeFilterQueryParamsBuilder.kt \
  app/src/test/java/com/zion830/threedollars/ui/home/data/HomeFilterQueryParamsBuilderTest.kt \
  data/src/test/java/com/threedollar/data/screen/HomeFilterScreenMapperTest.kt \
  core/network/src/main/java/com/threedollar/network/data/screen/HomeFilterScreenResponse.kt \
  data/src/main/java/com/threedollar/data/screen/HomeFilterScreenMapper.kt \
  core/common/src/main/java/com/threedollar/common/serverdriven/model/HomeFilterModels.kt \
  core/common/src/main/java/com/threedollar/common/serverdriven/model/ServerDrivenModels.kt
git add -u app/src/main/java/com/zion830/threedollars/ui/home/adapter/HomeFilterAdapter.kt \
  app/src/main/res/layout/item_home_filter_chip.xml
git commit -m "refactor: render home server filters with compose"
```

---

## 구현 시 주의점

- `ComposeView`는 반드시 `ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed`를 설정한다.
- Fragment에서 StateFlow를 직접 collect해서 `setContent`를 반복 호출하지 않는다. `setContent`는 한 번만 호출하고 Compose 안에서 `collectAsStateWithLifecycle()`을 사용한다.
- `LazyRow` item key는 index를 쓰지 않는다.
- 새 dependency를 추가하지 않는다. `app/build.gradle.kts`에는 이미 Compose, lifecycle compose, Coil3 Compose가 있다.
- 서버 색상 문자열은 항상 fallback을 둔다.
- 서버가 모르는 bar/type을 내려도 crash 없이 무시한다.
- `targetStores`는 현재 typed query로 이미 전달되므로 dynamic query map에 중복으로 넣지 않는다.
- `filterCertifiedStores`는 PR에서 누락된 기존 기능이므로 반드시 복구한다.
- 서버 `viewLog`를 실제로 보낼 경우 기존 `BaseFragment.onResume()` page view와 중복 전송되지 않게 먼저 정책을 정한다.
- 현재 계획은 상단 필터 Compose 전환 계획이다. 지도 핀 디자인 변경은 별도 계획으로 분리한다.
