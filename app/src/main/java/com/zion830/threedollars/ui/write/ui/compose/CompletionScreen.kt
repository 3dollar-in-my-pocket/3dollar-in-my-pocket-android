package com.zion830.threedollars.ui.write.ui.compose

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import base.compose.Gray10
import com.threedollar.common.R as CommonR
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray60
import base.compose.Green
import base.compose.Green200
import base.compose.Pink
import base.compose.Pink200
import base.compose.PretendardFontFamily
import coil3.compose.AsyncImage
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.zion830.threedollars.ui.write.viewModel.AddStoreContract
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@Composable
fun CompletionScreen(
    state: AddStoreContract.State,
    onIntent: (AddStoreContract.Intent) -> Unit,
    onComplete: () -> Unit,
    onNavigateToMenuDetail: () -> Unit,
    onNavigateToStoreDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompletionScreenContent(
        address = state.createdStoreInfo?.address?.fullAddress ?: state.address,
        categories = state.selectCategoryList,
        isMenuDetailCompleted = state.isMenuDetailCompleted,
        isStoreDetailCompleted = state.isStoreDetailCompleted,
        onMenuDetailClick = onNavigateToMenuDetail,
        onStoreDetailClick = onNavigateToStoreDetail,
        onComplete = onComplete,
        modifier = modifier
    )
}

@Composable
private fun CompletionScreenContent(
    address: String,
    categories: List<SelectCategoryModel>,
    isMenuDetailCompleted: Boolean,
    isStoreDetailCompleted: Boolean,
    onMenuDetailClick: () -> Unit,
    onStoreDetailClick: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(88.dp))

            Image(painterResource(DesignSystemR.drawable.ic_completion_clap), contentDescription = "", Modifier.size(40.dp))

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(CommonR.string.add_store_completion_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = Gray100,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            StoreSummaryCard(
                address = address,
                categories = categories
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(CommonR.string.add_store_tell_us_more),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = Gray100,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            DetailOptionButton(
                iconResId = DesignSystemR.drawable.ic_completion_menu,
                title = stringResource(CommonR.string.add_store_add_menu_detail),
                subtitle = stringResource(CommonR.string.add_store_menu_detail_subtitle),
                isCompleted = isMenuDetailCompleted,
                onClick = onMenuDetailClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailOptionButton(
                iconResId = DesignSystemR.drawable.ic_completion_megaphone,
                title = stringResource(CommonR.string.add_store_add_store_detail),
                subtitle = stringResource(CommonR.string.add_store_detail_subtitle),
                isCompleted = isStoreDetailCompleted,
                onClick = onStoreDetailClick
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Pink)
                .clickable { onComplete() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(CommonR.string.complete),
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                fontFamily = PretendardFontFamily,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoreSummaryCard(
    address: String,
    categories: List<SelectCategoryModel>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Gray30, RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val noAddressText = stringResource(CommonR.string.add_store_no_address)
        Text(
            text = address.ifEmpty { noAddressText },
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            fontFamily = PretendardFontFamily,
            color = Gray100,
            textAlign = TextAlign.Center
        )

        if (categories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
                content = {
                    categories.forEach { category ->
                        CategoryTag(
                            imageUrl = category.menuType.imageUrl,
                            name = category.menuType.name
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun CategoryTag(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Pink200)
            .padding(horizontal = 4.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
        }
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily,
            color = Pink
        )
    }
}

@Composable
private fun DetailOptionButton(
    iconResId: Int,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.9f),
                spotColor = Color.Black.copy(alpha = 0.9f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isCompleted) Green200 else Gray10)
                    .padding(4.dp)
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = 5.dp, y = (-3).dp)
                        .clip(CircleShape)
                        .background(Green)
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center,

                    ) {
                    Icon(
                        painter = painterResource(id = DesignSystemR.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = PretendardFontFamily,
                color = Gray100
            )
            Spacer(modifier = Modifier.height(4.dp))
            val completedText = stringResource(CommonR.string.add_store_write_complete)
            Text(
                text = if (isCompleted) completedText else subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.W500,
                fontFamily = PretendardFontFamily,
                color = if (isCompleted) Green else Gray60
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompletionScreenContentPreview() {
    CompletionScreenContent(
        address = "봉화역 2번 출구 삼거리 근처 붕어빵 집",
        categories = emptyList(),
        isMenuDetailCompleted = false,
        isStoreDetailCompleted = false,
        onMenuDetailClick = {},
        onStoreDetailClick = {},
        onComplete = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun CompletionScreenContentCompletedPreview() {
    CompletionScreenContent(
        address = "봉화역 2번 출구 삼거리 근처 붕어빵 집",
        categories = emptyList(),
        isMenuDetailCompleted = true,
        isStoreDetailCompleted = true,
        onMenuDetailClick = {},
        onStoreDetailClick = {},
        onComplete = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun StoreSummaryCardPreview() {
    StoreSummaryCard(
        address = "봉화역 2번 출구 삼거리 근처 붕어빵 집",
        categories = listOf(
            SelectCategoryModel(
                menuType = CategoryModel(categoryId = "1", name = "붕어빵", imageUrl = ""),
                menuDetail = null
            ),
            SelectCategoryModel(
                menuType = CategoryModel(categoryId = "2", name = "문어빵", imageUrl = ""),
                menuDetail = null
            ),
            SelectCategoryModel(
                menuType = CategoryModel(categoryId = "3", name = "와플", imageUrl = ""),
                menuDetail = null
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailOptionButtonPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DetailOptionButton(
            iconResId = DesignSystemR.drawable.ic_completion_menu,
            title = "메뉴 상세 정보 추가하기",
            subtitle = "메뉴명 • 메뉴 가격",
            isCompleted = false,
            onClick = {}
        )
        DetailOptionButton(
            iconResId = DesignSystemR.drawable.ic_completion_megaphone,
            title = "가게 세부 정보 추가하기",
            subtitle = "가게 형태 • 결제 방식 • 출몰 요일 • 출몰 시간대",
            isCompleted = true,
            onClick = {}
        )
    }
}
