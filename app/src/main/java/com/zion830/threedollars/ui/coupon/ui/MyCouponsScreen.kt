package com.zion830.threedollars.ui.coupon.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray50
import base.compose.Gray80
import base.compose.Gray90
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.domain.store.model.IssuedCouponModel
import com.zion830.threedollars.core.ui.component.compose.LottieFishLoading
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.ui.coupon.model.CouponTab
import com.zion830.threedollars.ui.coupon.model.CouponTabState
import com.zion830.threedollars.ui.coupon.model.MyCouponsUiIntent
import com.zion830.threedollars.ui.coupon.model.MyCouponsUiState
import com.zion830.threedollars.ui.my.page.commponent.MyPageShopInfoView
import com.zion830.threedollars.ui.my.page.data.toMyPageShop
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@Composable
internal fun MyCouponsScreen(
    state: MyCouponsUiState,
    onIntent: (MyCouponsUiIntent) -> Unit,
) {
    val tabs = CouponTab.entries
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray100)
            .statusBarsPadding(),
    ) {
        MyCouponsTopBar(onBack = { onIntent(MyCouponsUiIntent.OnBackClick) })
        CouponTabBar(
            tabs = tabs,
            selectedIndex = pagerState.currentPage,
            onSelect = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val tab = tabs[page]
            CouponList(
                tabState = state.tab(tab),
                onLoadNextPage = { onIntent(MyCouponsUiIntent.OnLoadNextPage(tab)) },
                onStoreClick = { onIntent(MyCouponsUiIntent.OnStoreClick(it)) },
                onCouponClick = { onIntent(MyCouponsUiIntent.OnCouponClick(it)) },
            )
        }
    }

    state.sheetCoupon?.let { coupon ->
        CouponUseBottomSheet(
            coupon = coupon,
            isUsing = state.isUsing,
            onDismiss = { onIntent(MyCouponsUiIntent.OnSheetDismiss) },
            onUseClick = { onIntent(MyCouponsUiIntent.OnUseClick) },
        )
    }
    if (state.isConfirmVisible) {
        CouponUseConfirmDialog(
            onCancel = { onIntent(MyCouponsUiIntent.OnConfirmCancel) },
            onConfirm = { onIntent(MyCouponsUiIntent.OnConfirmUse) },
        )
    }
}

@Composable
private fun MyCouponsTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_arrow_left),
            contentDescription = null,
            colorFilter = ColorFilter.tint(ColorWhite),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(24.dp)
                .noRippleClickable(onClick = onBack),
        )
        Text(
            text = stringResource(CommonR.string.my_coupons_title),
            modifier = Modifier.align(Alignment.Center),
            color = ColorWhite,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = dpToSp(16),
        )
    }
}

@Composable
private fun CouponTabBar(
    tabs: List<CouponTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            tabs.forEachIndexed { index, tab ->
                val selected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable { onSelect(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(tab.titleRes),
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = if (selected) Pink else Gray50,
                        fontFamily = PretendardFontFamily,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = dpToSp(14),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(if (selected) Pink else Color.Transparent),
                    )
                }
            }
        }
        HorizontalDivider(thickness = 1.dp, color = Gray90)
    }
}

@Composable
private fun CouponList(
    tabState: CouponTabState,
    onLoadNextPage: () -> Unit,
    onStoreClick: (IssuedCouponModel) -> Unit,
    onCouponClick: (IssuedCouponModel) -> Unit,
) {
    val listState = rememberLazyListState()
    CouponPagingTrigger(listState = listState, canLoadMore = tabState.canLoadMore, onLoadNextPage = onLoadNextPage)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 28.dp),
        ) {
            itemsIndexed(tabState.coupons, key = { _, coupon -> coupon.issuedKey }) { index, coupon ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 28.dp),
                        thickness = 1.dp,
                        color = Gray80,
                    )
                }
                CouponCell(coupon = coupon, onStoreClick = onStoreClick, onCouponClick = onCouponClick)
            }
            if (tabState.isLoading && tabState.coupons.isNotEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        LottieFishLoading(modifier = Modifier.size(72.dp))
                    }
                }
            }
            item { Spacer(modifier = Modifier.navigationBarsPadding()) }
        }
        when {
            tabState.isEmpty -> CouponEmptyView(modifier = Modifier.align(Alignment.Center))
            tabState.isLoading && !tabState.hasLoaded -> LottieFishLoading(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(120.dp),
            )
        }
    }
}

@Composable
private fun CouponCell(
    coupon: IssuedCouponModel,
    onStoreClick: (IssuedCouponModel) -> Unit,
    onCouponClick: (IssuedCouponModel) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.noRippleClickable { onStoreClick(coupon) }) {
            MyPageShopInfoView(myPageShop = coupon.toMyPageShop())
        }
        CouponTicket(
            coupon = coupon,
            modifier = Modifier.noRippleClickable { onCouponClick(coupon) },
        )
    }
}

@Composable
private fun CouponEmptyView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.offset(y = (-40).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_empty_100),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(CommonR.string.my_coupons_empty),
            color = Gray30,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = dpToSp(14),
        )
    }
}

@Composable
private fun CouponPagingTrigger(
    listState: LazyListState,
    canLoadMore: Boolean,
    onLoadNextPage: () -> Unit,
) {
    LaunchedEffect(listState, canLoadMore) {
        snapshotFlow {
            val totalCount = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex to totalCount
        }
            .map { (lastVisibleIndex, totalCount) -> canLoadMore && totalCount > 0 && lastVisibleIndex >= totalCount - 2 }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadNextPage() }
    }
}
