package com.zion830.threedollars.ui.edit.ui.compose

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray50
import base.compose.Gray70
import base.compose.Gray90
import base.compose.Pink
import base.compose.Pink200
import base.compose.PretendardFontFamily
import base.compose.Red
import coil3.compose.AsyncImage
import com.threedollar.common.R as CommonR
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun EditMenuScreen(
    state: EditStoreContract.State,
    onIntent: (EditStoreContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    LaunchedEffect(state.selectCategoryList, state.selectedCategoryId) {
        if (state.selectCategoryList.isNotEmpty() && state.selectedCategoryId == null) {
            onIntent(EditStoreContract.Intent.SetSelectedCategoryId(state.selectCategoryList.first().menuType.categoryId))
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            EditCategoryBottomSheet(
                selectCategoryList = state.selectCategoryList,
                availableSnackCategories = state.availableSnackCategories,
                availableMealCategories = state.availableMealCategories,
                onCategoryChange = { category ->
                    onIntent(EditStoreContract.Intent.ChangeSelectCategory(category))
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
                        onIntent(EditStoreContract.Intent.NavigateBack)
                    },
                    onCloseClick = {
                        if (state.hasAnyChanges) {
                            onIntent(EditStoreContract.Intent.ShowExitConfirmDialog)
                        } else {
                            onIntent(EditStoreContract.Intent.ConfirmExit)
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 20.dp)
                ) {
                    Text(
                        text = stringResource(CommonR.string.add_store_menu_detail_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W600,
                        fontFamily = PretendardFontFamily,
                        color = Gray100,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(CommonR.string.add_store_food_category),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400,
                            fontFamily = PretendardFontFamily,
                            color = Gray100
                        )
                    }

                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.selectCategoryList.forEach { category ->
                            EditCategoryChip(
                                category = category.menuType,
                                isSelected = state.selectedCategoryId == category.menuType.categoryId,
                                onClick = {
                                    onIntent(EditStoreContract.Intent.SetSelectedCategoryId(category.menuType.categoryId))
                                }
                            )
                        }

                        EditCategoryEditButton(
                            onClick = {
                                scope.launch { bottomSheetState.show() }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .background(Gray10)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        val selectedCategory = state.selectCategoryList.find {
                            it.menuType.categoryId == state.selectedCategoryId
                        }

                        selectedCategory?.let { selectCategory ->
                            item {
                                EditMenuCategorySection(
                                    selectCategory = selectCategory,
                                    onAddMenu = {
                                        onIntent(EditStoreContract.Intent.AddMenuToCategory(selectCategory.menuType.categoryId))
                                    },
                                    onRemoveMenu = { menuIndex ->
                                        onIntent(EditStoreContract.Intent.RemoveMenuFromCategory(selectCategory.menuType.categoryId, menuIndex))
                                    },
                                    onUpdateMenu = { menuIndex, name, price, count ->
                                        onIntent(
                                            EditStoreContract.Intent.UpdateMenuInCategory(
                                                selectCategory.menuType.categoryId,
                                                menuIndex,
                                                name,
                                                price,
                                                count
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                MainButton(
                    text = stringResource(CommonR.string.edit_store_finish),
                    enabled = true,
                    onClick = {
                        onIntent(EditStoreContract.Intent.NavigateBack)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
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

@Composable
private fun EditMenuCategorySection(
    selectCategory: SelectCategoryModel,
    onAddMenu: () -> Unit,
    onRemoveMenu: (Int) -> Unit,
    onUpdateMenu: (Int, String, String, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = selectCategory.menuType.imageUrl,
                contentDescription = selectCategory.menuType.name,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = stringResource(CommonR.string.add_store_menu_with_name, selectCategory.menuType.name),
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        selectCategory.menuDetail?.forEachIndexed { index, menu ->
            EditMenuInputRow(
                index = index,
                menu = menu,
                onRemove = { onRemoveMenu(index) },
                onUpdateName = { name -> onUpdateMenu(index, name, menu.price ?: "", menu.count) },
                onUpdatePrice = { price -> onUpdateMenu(index, menu.name ?: "", price, menu.count) },
                onUpdateCount = { count -> onUpdateMenu(index, menu.name ?: "", menu.price ?: "", count) },
                canRemove = (selectCategory.menuDetail?.size ?: 0) > 1
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        TextButton(
            onClick = onAddMenu,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(color = Gray90, shape = RoundedCornerShape(10.dp))
        ) {
            Text(
                text = stringResource(CommonR.string.add_store_add_menu),
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = ColorWhite
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun EditMenuInputRow(
    index: Int,
    menu: UserStoreMenuModel,
    onRemove: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdatePrice: (String) -> Unit,
    onUpdateCount: (Int?) -> Unit,
    canRemove: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(CommonR.string.add_store_menu_format, index + 1),
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100
            )

            if (canRemove) {
                Text(
                    text = stringResource(CommonR.string.delete),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = PretendardFontFamily,
                    color = Red,
                    modifier = Modifier.clickable { onRemove() }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = menu.name ?: "",
            onValueChange = onUpdateName,
            placeholder = {
                Text(
                    text = stringResource(CommonR.string.add_store_menu_name_placeholder),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = PretendardFontFamily,
                    color = Gray50
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = ColorWhite,
                focusedBorderColor = Pink,
                unfocusedBorderColor = Color.Transparent,
                textColor = Gray100
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle.Default.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                fontFamily = PretendardFontFamily
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var countText by remember(menu.count) {
                mutableStateOf(menu.count?.toString() ?: "")
            }
            var priceText by remember(menu.price) {
                mutableStateOf(menu.price?.takeIf { it != "-" } ?: "")
            }

            OutlinedTextField(
                value = countText,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } && newValue.length <= 9) {
                        countText = newValue
                        onUpdateCount(newValue.toIntOrNull())
                    }
                },
                placeholder = {
                    Text(
                        text = stringResource(CommonR.string.add_store_count_placeholder),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = PretendardFontFamily,
                        color = Gray50
                    )
                },
                trailingIcon = {
                    Text(
                        text = stringResource(CommonR.string.unit_count),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = PretendardFontFamily,
                        color = Gray70,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = ColorWhite,
                    focusedBorderColor = Pink,
                    unfocusedBorderColor = Color.Transparent,
                    textColor = Gray100
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = priceText,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } && newValue.length <= 9) {
                        priceText = newValue
                        onUpdatePrice(newValue)
                    }
                },
                placeholder = {
                    Text(
                        text = stringResource(CommonR.string.add_store_price_placeholder),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = PretendardFontFamily,
                        color = Gray50
                    )
                },
                trailingIcon = {
                    Text(
                        text = stringResource(CommonR.string.unit_currency_won),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = PretendardFontFamily,
                        color = Gray70,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = ColorWhite,
                    focusedBorderColor = Pink,
                    unfocusedBorderColor = Color.Transparent,
                    textColor = Gray100
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EditCategoryChip(
    category: CategoryModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(if (isSelected) Pink200 else Gray10)
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
            color = if (isSelected) Pink else Gray100
        )
    }
}

@Composable
private fun EditCategoryEditButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(Gray10)
            .border(1.dp, Gray30, RoundedCornerShape(100.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(CommonR.string.add_store_edit_category),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            color = Gray70
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditCategoryBottomSheet(
    selectCategoryList: List<SelectCategoryModel>,
    availableSnackCategories: List<CategoryModel>,
    availableMealCategories: List<CategoryModel>,
    onCategoryChange: (CategoryModel) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCategoryIds = selectCategoryList.map { it.menuType.categoryId }

    val snackCategories = availableSnackCategories.map { category ->
        category.copy(isSelected = selectedCategoryIds.contains(category.categoryId))
    }
    val mealCategories = availableMealCategories.map { category ->
        category.copy(isSelected = selectedCategoryIds.contains(category.categoryId))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(CommonR.string.add_store_select_category_count, selectCategoryList.size),
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray100
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(CommonR.string.category_snack),
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
            snackCategories.forEach { category ->
                EditCategorySelectChip(
                    category = category,
                    onClick = {
                        val updatedCategory = category.copy(isSelected = !category.isSelected)
                        onCategoryChange(updatedCategory)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(CommonR.string.category_meal),
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
            mealCategories.forEach { category ->
                EditCategorySelectChip(
                    category = category,
                    onClick = {
                        val updatedCategory = category.copy(isSelected = !category.isSelected)
                        onCategoryChange(updatedCategory)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (selectCategoryList.isEmpty()) Gray30 else Pink)
                .clickable(enabled = selectCategoryList.isNotEmpty()) {
                    if (selectCategoryList.isNotEmpty()) {
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
        onIntent = {}
    )
}
