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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
import coil.compose.AsyncImage
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.zion830.threedollars.ui.write.viewModel.AddStoreContract

@Composable
fun MenuDetailScreen(
    state: AddStoreContract.State,
    onIntent: (AddStoreContract.Intent) -> Unit,
    onShowCategoryEditSheet: () -> Unit,
    modifier: Modifier = Modifier,
    isCompletionMode: Boolean = false
) {
    LaunchedEffect(state.selectCategoryList, state.selectedCategoryId) {
        if (state.selectCategoryList.isNotEmpty() && state.selectedCategoryId == null) {
            onIntent(AddStoreContract.Intent.SetSelectedCategoryId(state.selectCategoryList.first().menuType.categoryId))
        }
    }

    MenuDetailScreenContent(
        selectCategoryList = state.selectCategoryList,
        selectedCategoryId = state.selectedCategoryId,
        onCategoryClick = onShowCategoryEditSheet,
        onSelectCategory = { categoryId -> onIntent(AddStoreContract.Intent.SetSelectedCategoryId(categoryId)) },
        onAddMenu = { categoryId -> onIntent(AddStoreContract.Intent.AddMenuToCategory(categoryId)) },
        onRemoveMenu = { categoryId, menuIndex ->
            onIntent(AddStoreContract.Intent.RemoveMenuFromCategory(categoryId, menuIndex))
        },
        onUpdateMenu = { categoryId, menuIndex, name, price, count ->
            onIntent(AddStoreContract.Intent.UpdateMenuInCategory(categoryId, menuIndex, name, price, count))
        },
        modifier = modifier,
        isCompletionMode = isCompletionMode
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MenuDetailScreenContent(
    selectCategoryList: List<SelectCategoryModel>,
    selectedCategoryId: String?,
    onCategoryClick: () -> Unit,
    onSelectCategory: (String) -> Unit,
    onAddMenu: (String) -> Unit,
    onRemoveMenu: (String, Int) -> Unit,
    onUpdateMenu: (String, Int, String, String, Int?) -> Unit,
    modifier: Modifier = Modifier,
    isCompletionMode: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append("메뉴 상세 정보 추가 ")
                withStyle(style = SpanStyle(color = Gray50, fontSize = 16.sp)) {
                    append("선택")
                }
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray100,
            modifier = Modifier.padding(horizontal = 20.dp),
            textAlign = TextAlign.Center
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
                text = "음식 카테고리",
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
            selectCategoryList.forEach { category ->
                CategoryChip(
                    category = category.menuType,
                    isSelected = selectedCategoryId == category.menuType.categoryId,
                    onClick = { onSelectCategory(category.menuType.categoryId) }
                )
            }

            CategoryEditChip(
                onClick = onCategoryClick
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
            val selectedCategory = selectCategoryList.find {
                it.menuType.categoryId == selectedCategoryId
            }

            selectedCategory?.let { selectCategory ->
                item {
                    MenuCategorySection(
                        selectCategory = selectCategory,
                        onAddMenu = { onAddMenu(selectCategory.menuType.categoryId) },
                        onRemoveMenu = { menuIndex ->
                            onRemoveMenu(selectCategory.menuType.categoryId, menuIndex)
                        },
                        onUpdateMenu = { menuIndex, name, price, count ->
                            onUpdateMenu(
                                selectCategory.menuType.categoryId,
                                menuIndex,
                                name,
                                price,
                                count
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun MenuCategorySection(
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
                text = "${selectCategory.menuType.name} 메뉴",
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100
            )

            Text(
                text = "삭제",
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                fontFamily = PretendardFontFamily,
                color = Red,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable { }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        selectCategory.menuDetail?.forEachIndexed { index, menu ->
            MenuInputRow(
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
                text = "메뉴 추가",
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = ColorWhite
            )
        }
    }
}

@Composable
private fun MenuInputRow(
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
                text = "메뉴${index + 1}",
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100
            )

            if (canRemove) {
                Text(
                    text = "삭제",
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
                    text = "슈크림 붕어빵",
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
                fontFamily = PretendardFontFamily,
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
                    if (newValue.all { it.isDigit() }) {
                        countText = newValue
                        onUpdateCount(newValue.toIntOrNull())
                    }
                },
                placeholder = {
                    Text(
                        text = "1",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = PretendardFontFamily,
                        color = Gray50
                    )
                },
                trailingIcon = {
                    Text(
                        text = "개",
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
                    if (newValue.all { it.isDigit() }) {
                        priceText = newValue
                        onUpdatePrice(newValue)
                    }
                },
                placeholder = {
                    Text(
                        text = "5,000",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W400,
                        fontFamily = PretendardFontFamily,
                        color = Gray50
                    )
                },
                trailingIcon = {
                    Text(
                        text = "원",
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryEditBottomSheet(
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
            text = "추가할 카테고리를 선택해 주세요 (${selectCategoryList.size}/10)",
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray100
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "간식",
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
                CategoryChip(
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
            text = "식사",
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
                CategoryChip(
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
                text = "수정 완료",
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

@Composable
private fun CategoryEditChip(
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
            text = "카테고리 수정",
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            color = Gray70
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

@Preview(showBackground = true)
@Composable
private fun MenuInputRowPreview() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
    ) {
        MenuInputRow(
            index = 0,
            menu = UserStoreMenuModel(
                category = CategoryModel(categoryId = "1", name = "붕어빵"),
                menuId = 1,
                name = "슈크림 붕어빵",
                price = "2000",
                count = 1
            ),
            onRemove = {},
            onUpdateName = {},
            onUpdatePrice = {},
            onUpdateCount = {},
            canRemove = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MenuInputRow(
            index = 1,
            menu = UserStoreMenuModel(
                category = CategoryModel(categoryId = "1", name = "붕어빵"),
                menuId = 0,
                name = "",
                price = ""
            ),
            onRemove = {},
            onUpdateName = {},
            onUpdatePrice = {},
            onUpdateCount = {},
            canRemove = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuCategorySectionPreview() {
    MenuCategorySection(
        selectCategory = SelectCategoryModel(
            menuType = CategoryModel(
                categoryId = "1",
                name = "붕어빵",
                imageUrl = ""
            ),
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
        onAddMenu = {},
        onRemoveMenu = {},
        onUpdateMenu = { _, _, _, _ -> }
    )
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
        onSelectCategory = {},
        onAddMenu = {},
        onRemoveMenu = { _, _ -> },
        onUpdateMenu = { _, _, _, _, _ -> }
    )
}
