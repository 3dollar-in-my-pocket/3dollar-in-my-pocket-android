package com.zion830.threedollars.ui.dialog.category.composable

import android.content.Context
import android.content.Intent
import androidx.annotation.IntRange
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import base.compose.ColorBlack
import base.compose.ColorWhite
import base.compose.Gray70
import base.compose.Gray90
import base.compose.Pink
import base.compose.Pink100
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.threedollar.common.compose.utils.toColor
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.core.ui.component.compose.components.FlowWithLifecycleEffect
import com.zion830.threedollars.core.ui.component.compose.components.VerticalSpacer
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.ui.dialog.category.SelectCategoryEffect
import com.zion830.threedollars.ui.dialog.category.SelectCategoryIntent
import com.zion830.threedollars.ui.dialog.category.SelectCategoryState
import com.zion830.threedollars.ui.dialog.category.SelectCategoryViewModel
import com.zion830.threedollars.ui.dialog.category.StoreCategory
import com.zion830.threedollars.ui.dialog.category.StoreCategoryItem
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

private const val CATEGORY_GRID_SIZE = 4

@Composable
fun CategoryListScreen(
    viewModel: SelectCategoryViewModel,
    homeViewModel: HomeViewModel,
    onSelected: (StoreCategoryItem.Food?) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    var showError by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.dispatch(SelectCategoryIntent.OnInit)
    }

    FlowWithLifecycleEffect(viewModel.effect) {
        when (it) {
            is SelectCategoryEffect.InitError -> {
                showError = true
            }

            is SelectCategoryEffect.ChangeFoodCategory -> {
                onSelected.invoke(it.item.takeIf { homeUiState.selectedCategory != it })
            }

            is SelectCategoryEffect.HandleAd -> {
                context.routeAd(it.data)
                onDismiss.invoke()
            }
        }
    }

    when (val state = state) {
        is SelectCategoryState.Success -> {
            CategoryList(
                state = state,
                selected = homeUiState.selectedCategory,
                onDispatch = viewModel::dispatch
            )
        }

        is SelectCategoryState.Idle -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
    }

    if (showError) {
        AlertDialog(
            onDismissRequest = {
                showError = false
            },
            title = {
                Text(
                    text = stringResource(CommonR.string.error_unknown_title),
                    color = Gray90,
                    fontSize = 16.sp
                )

            },
            text = {
                Text(
                    text = stringResource(CommonR.string.error_unknown_message),
                    color = Gray90,
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = ColorWhite
                    ),
                    onClick = {
                        viewModel.dispatch(SelectCategoryIntent.OnInit)
                        showError = false
                    }
                ) {
                    Text(
                        text = stringResource(CommonR.string.retry),
                        color = Gray90,
                        fontSize = 16.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = ColorWhite
                    ),
                    onClick = {
                        showError = false
                    }
                ) {
                    Text(
                        text = stringResource(CommonR.string.cancel),
                        color = Gray90,
                        fontSize = 16.sp
                    )
                }
            }
        )
    }
}

@Composable
private fun CategoryBannerAd(
    ad: AdvertisementModelV2,
    onPressed: (AdvertisementModelV2) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(ad.background.color.toColor(), RoundedCornerShape(12.dp))
            .noRippleClickable {
                onPressed.invoke(ad)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, bottom = 16.dp)
                .weight(1f)
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = ad.title.content,
                fontSize = 14.sp,
                color = ad.title.fontColor.toColor(),
                fontWeight = FontWeight.W700,
                letterSpacing = 0.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = ad.subTitle.content,
                fontSize = 12.sp,
                color = ad.subTitle.fontColor.toColor(),
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        AsyncImage(
            modifier = Modifier.size(84.dp),
            contentDescription = "광고 배너 이미지",
            model = ad.image.url,
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun CategoryList(
    state: SelectCategoryState.Success,
    selected: StoreCategoryItem?,
    onDispatch: (SelectCategoryIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        state.bannerAd?.let {
            CategoryBannerAd(it) { pressed ->
                onDispatch.invoke(SelectCategoryIntent.OnCategoryAdBannerClick(pressed))
            }
            VerticalSpacer(4)
        }
        state.categories.forEach {
            CategoryListItem(it, selected) { pressed ->
                onDispatch.invoke(SelectCategoryIntent.OnCategoryClick(pressed))
            }
        }
    }
}

@Composable
private fun CategoryListItem(
    item: StoreCategory,
    selected: StoreCategoryItem?,
    onSelected: (StoreCategoryItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = item.classification.name,
            fontSize = dpToSp(16),
            color = ColorBlack,
            fontWeight = FontWeight.W700,
            lineHeight = dpToSp(24),
            letterSpacing = 0.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        VerticalSpacer(12)

        CategoryGridLayout(
            size = CATEGORY_GRID_SIZE,
            horizontalSpacing = 12.dp,
            verticalSpacing = 12.dp
        ) {
            item.items.forEach {
                when (it) {
                    is StoreCategoryItem.Food -> {
                        CategoryFoodGridItem(
                            item = it,
                            selected = it == selected,
                            onSelected = onSelected
                        )
                    }

                    is StoreCategoryItem.Ad -> {
                        CategoryGridAdItem(
                            item = it,
                            onSelected = onSelected
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun CategoryFoodGridItem(
    item: StoreCategoryItem.Food,
    selected: Boolean,
    onSelected: (StoreCategoryItem) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onSelected.invoke(item) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .then(
                        other = if (selected) {
                            Modifier
                                .border(
                                    width = 1.dp,
                                    color = Pink,
                                    shape = CircleShape
                                )
                                .background(
                                    color = Pink100,
                                    shape = CircleShape
                                )
                        } else {
                            Modifier
                        }
                    )
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )

            Text(
                text = item.name,
                fontSize = dpToSp(12),
                color = if (selected) {
                    Pink
                } else {
                    Gray70
                },
                fontWeight = if (selected) {
                    FontWeight.W700
                } else {
                    FontWeight.W500
                },
                lineHeight = dpToSp(18),
                letterSpacing = 0.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = LocalTextStyle.current.copy(
                    lineBreak = LineBreak.Heading
                )
            )
        }
        if (item.isNew) {
            Image(
                painter = painterResource(DesignSystemR.drawable.ic_new_badge),
                contentDescription = item.name,
                modifier = Modifier
                    .width(32.dp)
                    .height(14.dp)
                    .align(Alignment.TopStart)
            )
        }
    }
}

@Composable
private fun CategoryGridAdItem(
    item: StoreCategoryItem.Ad,
    onSelected: (StoreCategoryItem.Ad) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onSelected.invoke(item) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = item.data.image.url,
                contentDescription = item.data.title.content,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )

            Text(
                text = item.data.title.content,
                fontSize = dpToSp(12),
                color = Gray70,
                fontWeight = FontWeight.W500,
                lineHeight = dpToSp(18),
                letterSpacing = 0.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                style = LocalTextStyle.current.copy(
                    lineBreak = LineBreak.Heading
                )
            )
        }
    }
}

/**
 * LazyGrid는 wrap_contnet를 미지원 하므로 커스텀 레이아웃 필요함
 */
@Composable
private fun CategoryGridLayout(
    @IntRange(from = 1) size: Int,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->

        val totalItems = measurables.size
        val rowCount = (totalItems + size - 1) / size

        val hSpacingPx = horizontalSpacing.roundToPx()
        val vSpacingPx = verticalSpacing.roundToPx()

        // Horizontal spacing affects width!
        val totalHorizontalSpacing = hSpacingPx * (size - 1)
        val itemWidth = (constraints.maxWidth - totalHorizontalSpacing) / size

        // Measure children
        val placeables = measurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    minWidth = itemWidth,
                    maxWidth = itemWidth
                )
            )
        }

        // Row heights + spacing 반영
        val rowHeights = IntArray(rowCount) { row ->
            val from = row * size
            val to = minOf(from + size, totalItems)
            placeables.subList(from, to).maxOf { it.height }
        }

        val totalVerticalSpacing = vSpacingPx * (rowCount - 1)
        val totalHeight = rowHeights.sum() + totalVerticalSpacing

        layout(constraints.maxWidth, totalHeight) {
            var y = 0
            var index = 0

            repeat(rowCount) { row ->
                var x = 0
                val rowHeight = rowHeights[row]

                repeat(size) {
                    if (index >= totalItems) return@repeat
                    val placeable = placeables[index]
                    placeable.placeRelative(x, y)
                    x += itemWidth + hSpacingPx
                    index++
                }

                y += rowHeight + vSpacingPx
            }
        }
    }
}

private fun Context.routeAd(data: AdvertisementModelV2) {
    when (data.link.type) {
        "APP_SCHEME" -> {
            startActivity(
                Intent(this, DynamicLinkActivity::class.java).apply {
                    putExtra("link", data.link.url)
                },
            )
        }

        else -> {
            startActivity(Intent(Intent.ACTION_VIEW, data.link.url.toUri()))
        }
    }
}
