package com.zion830.threedollars.ui.write.ui.compose

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray50
import base.compose.Pink
import base.compose.Pink200
import base.compose.PretendardFontFamily
import base.compose.Red
import com.threedollar.domain.home.data.store.CategoryModel
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.LegacySharedPrefUtils

data class CategorySection(
    val title: String,
    val categories: List<CategoryModel>
)

@Composable
fun MenuCategoryScreen(
    viewModel: AddStoreViewModel,
    modifier: Modifier = Modifier
) {
    val selectCategoryList by viewModel.selectCategoryList.collectAsState()
    val selectedCategoryIds = selectCategoryList.map { it.menuType.categoryId }

    val snackCategories = LegacySharedPrefUtils.getCategories().map { category ->
        category.copy(isSelected = selectedCategoryIds.contains(category.categoryId))
    }
    val mealCategories = LegacySharedPrefUtils.getTruckCategories().map { category ->
        category.copy(isSelected = selectedCategoryIds.contains(category.categoryId))
    }

    val categorySections = listOf(
        CategorySection(title = "간식", categories = snackCategories),
        CategorySection(title = "식사", categories = mealCategories)
    )

    MenuCategoryContent(
        categorySections = categorySections,
        onCategoryClick = { category ->
            val updatedCategory = category.copy(isSelected = !category.isSelected)
            viewModel.changeSelectCategory(updatedCategory)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MenuCategoryContent(
    categorySections: List<CategorySection>,
    onCategoryClick: (CategoryModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCount = categorySections.sumOf { section ->
        section.categories.count { it.isSelected }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "음식 카테고리 선택 ",
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 24.sp,
                color = Gray100
            )
            Text(
                text = buildAnnotatedString {
                    if (selectedCount == 10) {
                        withStyle(style = SpanStyle(color = Red)) {
                            append("($selectedCount/10)")
                        }
                    } else {
                        append("(")
                        val countColor = if (selectedCount == 0) Gray50 else Pink
                        withStyle(style = SpanStyle(color = countColor)) {
                            append("$selectedCount")
                        }
                        append("/10)")
                    }
                },
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                color = Gray100
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        categorySections.forEachIndexed { index, section ->
            Text(
                text = section.title,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                color = Gray100
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                section.categories.forEach { category ->
                    CategoryChip(
                        text = category.name,
                        isSelected = category.isSelected,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }

            if (index < categorySections.size - 1) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val boxColor = if (isSelected) Pink200 else Gray10
    val textColor = if (isSelected) Pink else Gray100

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(boxColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryChipPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CategoryChip(text = "🥖 붕어빵", isSelected = true, onClick = {})
        CategoryChip(text = "🍖 문어빵", isSelected = false, onClick = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MenuCategoryContentPreview() {
    val categorySections = listOf(
        CategorySection(
            title = "간식",
            categories = listOf(
                CategoryModel(categoryId = "1", name = "🥖 붕어빵", isSelected = true),
                CategoryModel(categoryId = "2", name = "🍖 문어빵", isSelected = true),
                CategoryModel(categoryId = "3", name = "🥚 계란빵"),
                CategoryModel(categoryId = "4", name = "🍢 꼬치"),
                CategoryModel(categoryId = "5", name = "🍡 호떡"),
                CategoryModel(categoryId = "6", name = "🧇 와플"),
                CategoryModel(categoryId = "7", name = "🍥 어묵")
            )
        ),
        CategorySection(
            title = "식사",
            categories = listOf(
                CategoryModel(categoryId = "8", name = "🍚 한식"),
                CategoryModel(categoryId = "9", name = "🍝 양식"),
                CategoryModel(categoryId = "10", name = "🍣 일식"),
                CategoryModel(categoryId = "11", name = "🥟 중식")
            )
        )
    )

    MenuCategoryContent(
        categorySections = categorySections,
        onCategoryClick = {}
    )
}
