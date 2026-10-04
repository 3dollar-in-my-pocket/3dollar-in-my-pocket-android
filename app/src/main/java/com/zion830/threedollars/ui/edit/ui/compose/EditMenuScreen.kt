package com.zion830.threedollars.ui.edit.ui.compose

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Pink
import base.compose.Pink200
import base.compose.PretendardFontFamily
import coil3.compose.AsyncImage
import com.threedollar.common.R as CommonR
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ScreenName
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.zion830.threedollars.ui.dialog.category.StoreCategory
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.write.ui.compose.menuCategoryEditorItems
import com.zion830.threedollars.ui.write.ui.compose.MenuCategoryTabRow
import com.zion830.threedollars.ui.write.ui.compose.MenuImageAddButton
import com.zion830.threedollars.ui.write.ui.compose.rememberScrollToAddedMenu
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun EditMenuScreen(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit,
    onImageMenuAddClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        LogManager.sendPageView(ScreenName.WRITE_DETAIL_MENU, "EditMenuScreen")
    }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val currentCategoryList = state.tempSelectCategoryList ?: state.selectCategoryList

    LaunchedEffect(currentCategoryList, state.selectedCategoryId) {
        if (currentCategoryList.isNotEmpty() && state.selectedCategoryId == null) {
            onIntent(EditStoreContract.Intent.SetSelectedCategoryId(currentCategoryList.first().menuType.categoryId))
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            EditCategoryBottomSheet(
                selectCategoryList = currentCategoryList,
                storeCategories = state.storeCategories,
                onConfirm = { categoryIds ->
                    onIntent(EditStoreContract.Intent.UpdateSelectedCategories(categoryIds))
                },
                onDismiss = {
                    scope.launch { bottomSheetState.hide() }
                }
            )
        }
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ColorWhite)
            ) {
                EditStoreTopBar(
                    title = stringResource(CommonR.string.edit_store_section_menu),
                    showBackButton = true,
                    onBackClick = {
                        onIntent(EditStoreContract.Intent.CancelMenuEdit)
                    },
                    onCloseClick = {
                        val hasUnconfirmedMenuChanges = state.tempSelectCategoryList
                            ?.let { it != state.selectCategoryList } == true
                        if (state.hasAnyChanges || hasUnconfirmedMenuChanges) {
                            onIntent(EditStoreContract.Intent.ShowExitConfirmDialog)
                        } else {
                            onIntent(EditStoreContract.Intent.ConfirmExit)
                        }
                    }
                )

                val selectedCategory = currentCategoryList.find {
                    it.menuType.categoryId == state.selectedCategoryId
                }
                val listState = rememberLazyListState()
                val markMenuAdding = rememberScrollToAddedMenu(listState = listState, selectCategory = selectedCategory)
                LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
                    item {
                        Text(
                            text = stringResource(CommonR.string.add_store_menu_detail_title),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = PretendardFontFamily,
                            color = Gray100,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 16.dp)
                        )
                        if (onImageMenuAddClick != null) {
                            MenuImageAddButton(
                                onClick = onImageMenuAddClick,
                                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                            )
                        }
                    }
                    stickyHeader {
                        MenuCategoryTabRow(
                            categories = currentCategoryList,
                            selectedCategoryId = state.selectedCategoryId,
                            onSelect = { categoryId ->
                                onIntent(EditStoreContract.Intent.SetSelectedCategoryId(categoryId))
                            },
                            onFilterClick = {
                                scope.launch { bottomSheetState.show() }
                            }
                        )
                    }

                    selectedCategory?.let { selectCategory ->
                        val categoryId = selectCategory.menuType.categoryId
                        menuCategoryEditorItems(
                            selectCategory = selectCategory,
                            onAddMenu = {
                                markMenuAdding()
                                onIntent(EditStoreContract.Intent.AddMenuToCategory(categoryId))
                            },
                            onRemoveMenu = { menuIndex ->
                                onIntent(EditStoreContract.Intent.RemoveMenuFromCategory(categoryId, menuIndex))
                            },
                            onUpdateMenu = { menuIndex, name, price, count ->
                                onIntent(
                                    EditStoreContract.Intent.UpdateMenuInCategory(
                                        categoryId,
                                        menuIndex,
                                        name,
                                        price,
                                        count
                                    )
                                )
                            },
                        )
                    }
                }

                MainButton(
                    text = stringResource(CommonR.string.edit_store_finish),
                    enabled = true,
                    onClick = {
                        onIntent(EditStoreContract.Intent.ConfirmMenuChanges)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorWhite.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Pink)
                }
            }

            if (state.showExitConfirmDialog) {
                ExitConfirmDialog(
                    onDismiss = { onIntent(EditStoreContract.Intent.HideExitConfirmDialog) },
                    onConfirm = { onIntent(EditStoreContract.Intent.ConfirmExit) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditCategoryBottomSheet(
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
                    EditCategorySelectChip(
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
private fun EditCategorySelectChip(
    category: CategoryModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(if (category.isSelected) Pink200 else Gray10)
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
            color = if (category.isSelected) Pink else Gray100
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditMenuScreenPreview() {
    EditMenuScreen(
        state = EditStoreContract.State(
            selectCategoryList = listOf(
                SelectCategoryModel(
                    menuType = CategoryModel(categoryId = "1", name = "붕어빵", imageUrl = ""),
                    menuDetail = listOf(
                        UserStoreMenuModel(
                            category = CategoryModel(categoryId = "1", name = "붕어빵"),
                            menuId = 1,
                            name = "슈크림 붕어빵",
                            price = "2000",
                            count = 1
                        )
                    )
                )
            ),
            selectedCategoryId = "1"
        ),
        onIntent = {},
        onImageMenuAddClick = {}
    )
}
