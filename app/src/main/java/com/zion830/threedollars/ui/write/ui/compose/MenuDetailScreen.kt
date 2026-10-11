package com.zion830.threedollars.ui.write.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.threedollar.common.R as CommonR
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ScreenName
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray50
import base.compose.Pink
import base.compose.Pink200
import base.compose.PretendardFontFamily
import coil3.compose.AsyncImage
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.zion830.threedollars.ui.dialog.category.StoreCategory
import com.zion830.threedollars.ui.write.viewModel.AddStoreContract

@Composable
fun MenuDetailScreen(
    state: AddStoreContract.State,
    onIntent: (AddStoreContract.Intent) -> Unit,
    onShowCategoryEditSheet: () -> Unit,
    onImageMenuAddClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        LogManager.sendPageView(ScreenName.WRITE_DETAIL_MENU, "MenuDetailScreen")
    }
    LaunchedEffect(state.selectCategoryList, state.selectedCategoryId) {
        if (state.selectCategoryList.isNotEmpty() && state.selectedCategoryId == null) {
            onIntent(AddStoreContract.Intent.SetSelectedCategoryId(state.selectCategoryList.first().menuType.categoryId))
        }
    }

    MenuDetailScreenContent(
        selectCategoryList = state.selectCategoryList,
        selectedCategoryId = state.selectedCategoryId,
        onCategoryClick = onShowCategoryEditSheet,
        onImageMenuAddClick = onImageMenuAddClick,
        onSelectCategory = { categoryId -> onIntent(AddStoreContract.Intent.SetSelectedCategoryId(categoryId)) },
        onAddMenu = { categoryId -> onIntent(AddStoreContract.Intent.AddMenuToCategory(categoryId)) },
        onRemoveMenu = { categoryId, menuIndex ->
            onIntent(AddStoreContract.Intent.RemoveMenuFromCategory(categoryId, menuIndex))
        },
        onUpdateMenu = { categoryId, menuIndex, name, price, count ->
            onIntent(AddStoreContract.Intent.UpdateMenuInCategory(categoryId, menuIndex, name, price, count))
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MenuDetailScreenContent(
    selectCategoryList: List<SelectCategoryModel>,
    selectedCategoryId: String?,
    onCategoryClick: () -> Unit,
    onImageMenuAddClick: (() -> Unit)?,
    onSelectCategory: (String) -> Unit,
    onAddMenu: (String) -> Unit,
    onRemoveMenu: (String, Int) -> Unit,
    onUpdateMenu: (String, Int, String, String, Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedCategory = selectCategoryList.find { it.menuType.categoryId == selectedCategoryId }
    val listState = rememberLazyListState()
    val markMenuAdding = rememberScrollToAddedMenu(listState = listState, selectCategory = selectedCategory)
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        item {
            val menuDetailTitle = stringResource(CommonR.string.add_store_menu_detail_title)
            val optionalText = stringResource(CommonR.string.add_store_optional)
            Text(
                text = buildAnnotatedString {
                    append("$menuDetailTitle ")
                    withStyle(style = SpanStyle(color = Gray50, fontSize = 16.sp)) {
                        append(optionalText)
                    }
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp),
            )
            if (onImageMenuAddClick != null) {
                MenuImageAddButton(
                    onClick = onImageMenuAddClick,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                )
            }
        }
        stickyHeader {
            MenuCategoryStickyHeader(
                categories = selectCategoryList,
                selectedCategory = selectedCategory,
                onSelect = onSelectCategory,
                onAddMenu = { categoryId ->
                    markMenuAdding()
                    onAddMenu(categoryId)
                },
                onFilterClick = onCategoryClick,
            )
        }
        selectedCategory?.let { category ->
            val categoryId = category.menuType.categoryId
            menuCategoryEditorItems(
                selectCategory = category,
                onRemoveMenu = { menuIndex -> onRemoveMenu(categoryId, menuIndex) },
                onUpdateMenu = { menuIndex, name, price, count ->
                    onUpdateMenu(categoryId, menuIndex, name, price, count)
                },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryEditBottomSheet(
    selectCategoryList: List<SelectCategoryModel>,
    storeCategories: List<StoreCategory>,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var localSelectedIds by remember(selectCategoryList) {
        mutableStateOf(selectCategoryList.map { it.menuType.categoryId }.toSet())
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(CommonR.string.add_store_select_category_count, localSelectedIds.size),
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray100
        )

        Spacer(modifier = Modifier.height(20.dp))

        storeCategories.forEachIndexed { index, storeCategory ->
            Text(
                text = storeCategory.classification.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                storeCategory.items.forEach { item ->
                    val isSelected = localSelectedIds.contains(item.id)
                    val categoryModel = CategoryModel(
                        categoryId = item.id,
                        name = item.name,
                        description = item.description,
                        imageUrl = item.imageUrl,
                        disableImageUrl = item.disableImageUrl,
                        isNew = item.isNew,
                        isSelected = isSelected
                    )
                    CategoryChip(
                        category = categoryModel,
                        onClick = {
                            localSelectedIds = if (isSelected) {
                                localSelectedIds - item.id
                            } else {
                                if (localSelectedIds.size < 10) {
                                    localSelectedIds + item.id
                                } else {
                                    localSelectedIds
                                }
                            }
                        }
                    )
                }
            }

            if (index < storeCategories.size - 1) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (localSelectedIds.isEmpty()) Gray30 else Pink)
                .clickable(enabled = localSelectedIds.isNotEmpty()) {
                    if (localSelectedIds.isNotEmpty()) {
                        onConfirm(localSelectedIds.toList())
                        onDismiss()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(CommonR.string.add_store_edit_complete),
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Color.White
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: CategoryModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showBorder: Boolean = false,
    isSelected: Boolean? = null
) {
    val selected = isSelected ?: category.isSelected

    Row(
        modifier = modifier
            .clip(CircleShape)
            .then(
                if (showBorder) {
                    Modifier.border(1.dp, Pink, CircleShape)
                } else {
                    Modifier
                }
            )
            .background(
                when {
                    showBorder -> Color.Transparent
                    selected -> Pink200
                    else -> Gray10
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AsyncImage(
            model = category.imageUrl,
            contentDescription = category.name,
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = category.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            fontFamily = PretendardFontFamily,
            color = if (selected) Pink else Gray100
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryChipPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CategoryChip(
            category = CategoryModel(categoryId = "1", name = "붕어빵", isSelected = true),
            onClick = {}
        )
        CategoryChip(
            category = CategoryModel(categoryId = "2", name = "꼬치", isSelected = false),
            onClick = {}
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MenuDetailScreenContentPreview() {
    val mockSelectCategoryList = listOf(
        SelectCategoryModel(
            menuType = CategoryModel(categoryId = "1", name = "붕어빵", imageUrl = ""),
            menuDetail = listOf(
                UserStoreMenuModel(
                    category = CategoryModel(categoryId = "1", name = "붕어빵"),
                    menuId = 1,
                    name = "슈크림 붕어빵",
                    price = "2000",
                    count = 1
                ),
                UserStoreMenuModel(
                    category = CategoryModel(categoryId = "1", name = "붕어빵"),
                    menuId = 2,
                    name = "팥 붕어빵",
                    price = "1500",
                    count = 1
                )
            )
        ),
        SelectCategoryModel(
            menuType = CategoryModel(categoryId = "2", name = "꼬치", imageUrl = ""),
            menuDetail = listOf(
                UserStoreMenuModel(
                    category = CategoryModel(categoryId = "2", name = "꼬치"),
                    menuId = 3,
                    name = "오뎅꼬치",
                    price = "5000",
                    count = 3
                )
            )
        )
    )

    MenuDetailScreenContent(
        selectCategoryList = mockSelectCategoryList,
        selectedCategoryId = "1",
        onCategoryClick = {},
        onImageMenuAddClick = {},
        onSelectCategory = {},
        onAddMenu = {},
        onRemoveMenu = { _, _ -> },
        onUpdateMenu = { _, _, _, _, _ -> }
    )
}
