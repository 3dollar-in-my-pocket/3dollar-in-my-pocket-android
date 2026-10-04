package com.zion830.threedollars.ui.write.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray20
import base.compose.Gray50
import base.compose.Gray60
import base.compose.Gray70
import base.compose.Gray95
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.Red
import coil3.compose.AsyncImage
import com.threedollar.common.R as CommonR
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

private const val MENU_NUMBER_MAX_LENGTH = 9

/**
 * 선택한 카테고리들을 탭으로 보여준다. [onFilterClick]이 있으면 오른쪽에 카테고리 편집 아이콘을 둔다.
 */
@Composable
internal fun MenuCategoryTabRow(
    categories: List<SelectCategoryModel>,
    selectedCategoryId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFilterClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorWhite),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
            ) {
                items(categories, key = { it.menuType.categoryId }) { category ->
                    MenuCategoryTab(
                        name = category.menuType.name,
                        isSelected = category.menuType.categoryId == selectedCategoryId,
                        onClick = { onSelect(category.menuType.categoryId) },
                    )
                }
            }
            if (onFilterClick != null) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_menu_category_filter),
                    contentDescription = stringResource(CommonR.string.add_store_food_category),
                    tint = Gray70,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(20.dp)
                        .clickable(onClick = onFilterClick),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Gray20),
        )
    }
}

@Composable
private fun MenuCategoryTab(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.W700 else FontWeight.W500,
            fontFamily = PretendardFontFamily,
            color = if (isSelected) Gray100 else Gray60,
            modifier = Modifier.padding(vertical = 12.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(if (isSelected) Gray100 else Color.Transparent),
        )
    }
}

/**
 * 카테고리 하나의 메뉴 입력 영역. 헤더 오른쪽 [+ 메뉴 추가]로 메뉴가 많아도 스크롤 없이 추가할 수 있다.
 * 메뉴 행마다 아이템을 나눠, 추가한 메뉴로 스크롤([rememberScrollToAddedMenu])할 수 있게 한다.
 */
internal fun LazyListScope.menuCategoryEditorItems(
    selectCategory: SelectCategoryModel,
    onAddMenu: () -> Unit,
    onRemoveMenu: (Int) -> Unit,
    onUpdateMenu: (index: Int, name: String, price: String, count: Int?) -> Unit,
) {
    val categoryId = selectCategory.menuType.categoryId
    val menus = selectCategory.menuDetail.orEmpty()
    item(key = "menu-header-$categoryId") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = selectCategory.menuType.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(CommonR.string.add_store_menu_with_name, selectCategory.menuType.name),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = Gray95,
                modifier = Modifier.weight(1f),
            )
            MenuAddPillButton(onClick = onAddMenu)
        }
    }
    menus.forEachIndexed { index, menu ->
        item(key = "menu-$categoryId-$index") {
            MenuInputRow(
                index = index,
                menu = menu,
                canRemove = menus.size > 1,
                onRemove = { onRemoveMenu(index) },
                onUpdateName = { name -> onUpdateMenu(index, name, menu.price.orEmpty(), menu.count) },
                onUpdatePrice = { price -> onUpdateMenu(index, menu.name.orEmpty(), price, menu.count) },
                onUpdateCount = { count -> onUpdateMenu(index, menu.name.orEmpty(), menu.price.orEmpty(), count) },
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp),
            )
        }
    }
    item(key = "menu-bottom-$categoryId") {
        Spacer(modifier = Modifier.height(20.dp))
    }
}

/**
 * [+ 메뉴 추가]로 늘어난 메뉴가 보이도록 목록 끝으로 스크롤한다 (TH-1438).
 *
 * 반환한 함수를 메뉴 추가 직전에 호출해 두면, 같은 카테고리의 메뉴 수가 늘어났을 때 한 번만 스크롤한다.
 * AI 인식 결과 반영이나 탭 전환처럼 메뉴 추가 버튼이 아닌 변화로는 스크롤하지 않는다.
 */
@Composable
internal fun rememberScrollToAddedMenu(listState: LazyListState, selectCategory: SelectCategoryModel?): () -> Unit {
    val categoryId = selectCategory?.menuType?.categoryId
    val menuCount = selectCategory?.menuDetail?.size ?: 0
    var pendingAdd by remember { mutableStateOf<Pair<String, Int>?>(null) }
    LaunchedEffect(categoryId, menuCount) {
        val (pendingCategoryId, countBeforeAdd) = pendingAdd ?: return@LaunchedEffect
        pendingAdd = null
        if (categoryId == pendingCategoryId && menuCount > countBeforeAdd) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }
    return { if (categoryId != null) pendingAdd = categoryId to menuCount }
}

@Composable
private fun MenuAddPillButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Gray70)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_menu_add_small),
            contentDescription = null,
            tint = Gray10,
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(CommonR.string.add_store_add_menu),
            fontSize = 12.sp,
            fontWeight = FontWeight.W700,
            fontFamily = PretendardFontFamily,
            color = Gray10,
        )
    }
}

@Composable
private fun MenuInputRow(
    index: Int,
    menu: UserStoreMenuModel,
    canRemove: Boolean,
    onRemove: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdatePrice: (String) -> Unit,
    onUpdateCount: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(CommonR.string.add_store_menu_format, index + 1),
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100,
            )
            if (canRemove) {
                Text(
                    text = stringResource(CommonR.string.delete),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = PretendardFontFamily,
                    color = Red,
                    modifier = Modifier.clickable(onClick = onRemove),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        MenuTextField(
            value = menu.name.orEmpty(),
            onValueChange = onUpdateName,
            placeholder = stringResource(CommonR.string.add_store_menu_name_placeholder),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            var countText by remember(menu.count) { mutableStateOf(menu.count?.toString().orEmpty()) }
            var priceText by remember(menu.price) { mutableStateOf(menu.price?.takeIf { it != "-" }.orEmpty()) }

            MenuTextField(
                value = countText,
                onValueChange = { newValue ->
                    if (newValue.isMenuNumber()) {
                        countText = newValue
                        onUpdateCount(newValue.toIntOrNull())
                    }
                },
                placeholder = stringResource(CommonR.string.add_store_count_placeholder),
                unit = stringResource(CommonR.string.unit_count),
                keyboardType = KeyboardType.Number,
                modifier = Modifier.width(90.dp),
            )
            MenuTextField(
                value = priceText,
                onValueChange = { newValue ->
                    if (newValue.isMenuNumber()) {
                        priceText = newValue
                        onUpdatePrice(newValue)
                    }
                },
                placeholder = stringResource(CommonR.string.add_store_price_placeholder),
                unit = stringResource(CommonR.string.unit_currency_won),
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun String.isMenuNumber(): Boolean = all { it.isDigit() } && length <= MENU_NUMBER_MAX_LENGTH

@Composable
private fun MenuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val textStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
        fontFamily = PretendardFontFamily,
        color = Gray100,
    )
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(Pink),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        interactionSource = interactionSource,
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Gray10)
            .border(BorderStroke(1.dp, if (isFocused) Pink else Color.Transparent), RoundedCornerShape(8.dp)),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(text = placeholder, style = textStyle.copy(color = Gray50))
                    }
                    innerTextField()
                }
                if (unit != null) {
                    Text(text = unit, style = textStyle.copy(color = Gray70))
                }
            }
        },
    )
}

/**
 * 메뉴 상세 정보 추가 화면 상단의 [이미지로 메뉴 추가] 버튼.
 */
@Composable
internal fun MenuImageAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ColorWhite)
            .border(BorderStroke(1.dp, Gray20), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(CommonR.string.menu_extraction_add_by_image),
            fontSize = 14.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray70,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_menu_extraction_add_image),
            contentDescription = null,
            tint = Gray70,
            modifier = Modifier.size(18.dp),
        )
    }
}
