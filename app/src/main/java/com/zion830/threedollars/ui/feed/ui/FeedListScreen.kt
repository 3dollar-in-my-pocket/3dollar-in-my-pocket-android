package com.zion830.threedollars.ui.feed.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import base.compose.Gray0
import base.compose.Gray100
import base.compose.Gray70
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.threedollar.domain.community.data.AdvertisementModelV2
import com.zion830.threedollars.core.ui.component.compose.LottieFishLoading
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.ui.feed.model.FeedListUiIntent
import com.zion830.threedollars.ui.feed.model.FeedListUiState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeedListScreen(
    state: FeedListUiState,
    onIntent: (FeedListUiIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    FeedPagingTrigger(
        listState = listState,
        canLoadMore = state.canLoadMore,
        onLoadNextPage = { onIntent(FeedListUiIntent.OnLoadNextPage) },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray0)
            .statusBarsPadding(),
    ) {
        FeedListTopBar(onClose = { onIntent(FeedListUiIntent.OnCloseClick) })
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(FeedListUiIntent.OnRefresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item(key = AD_ITEM_KEY) {
                    FeedAdvertisementSlot(
                        advertisement = state.advertisement,
                        onClick = { onIntent(FeedListUiIntent.OnAdvertisementClick(it)) },
                    )
                }
                items(state.feeds, key = { it.feedId }) { feed ->
                    FeedCard(feed = feed, onClick = { onIntent(FeedListUiIntent.OnFeedClick(feed)) })
                }
                if (state.isLoading && state.feeds.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            LottieFishLoading(modifier = Modifier.size(72.dp))
                        }
                    }
                }
                item { Spacer(modifier = Modifier.navigationBarsPadding()) }
            }
            if (state.isEmpty) {
                FeedEmptyContent(modifier = Modifier.align(Alignment.Center))
            }
            if (state.isLoading && !state.hasLoaded) {
                LottieFishLoading(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(120.dp),
                )
            }
        }
    }
}

@Composable
private fun FeedListTopBar(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Text(
            text = stringResource(CommonR.string.feed_list_title),
            modifier = Modifier.align(Alignment.Center),
            color = Gray100,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = dpToSp(16),
        )
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_close_gray100_24),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(24.dp)
                .noRippleClickable(onClick = onClose),
        )
    }
}

@Composable
private fun FeedEmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.offset(y = (-40).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_list_view_empty),
            contentDescription = null,
            modifier = Modifier.size(112.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(CommonR.string.feed_list_empty_title),
            color = Gray70,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = dpToSp(16),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(CommonR.string.feed_list_empty_description),
            color = Gray70,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = dpToSp(12),
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * 목록 최상단 광고 슬롯(높이 200). 서버 광고가 있으면 그 이미지를, 없으면 AdMob 배너를 보여준다.
 * AdMob 은 광고 WebView 가 포커스를 가져가 목록이 튀지 않도록 하위 포커스를 막는다.
 */
@Composable
private fun FeedAdvertisementSlot(
    advertisement: AdvertisementModelV2?,
    onClick: (AdvertisementModelV2) -> Unit,
) {
    if (advertisement != null) {
        AsyncImage(
            model = advertisement.image.url,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(AD_SLOT_HEIGHT_DP.dp)
                .noRippleClickable { onClick(advertisement) },
        )
        return
    }
    FeedAdMobBanner()
}

@Composable
private fun FeedAdMobBanner() {
    var isFailed by remember { mutableStateOf(false) }
    if (isFailed) return
    val screenWidthDp = LocalConfiguration.current.screenWidthDp - HORIZONTAL_PADDING_DP * 2
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AD_SLOT_HEIGHT_DP.dp),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                FrameLayout(context).apply {
                    descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                    addView(
                        AdView(context).apply {
                            setAdSize(AdSize.getInlineAdaptiveBannerAdSize(screenWidthDp, AD_SLOT_HEIGHT_DP))
                            adUnitId = context.getString(CommonR.string.admob_feed_list_banner)
                            adListener = object : AdListener() {
                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    isFailed = true
                                }
                            }
                            loadAd(AdRequest.Builder().build())
                        },
                    )
                }
            },
            onRelease = { container -> (container.getChildAt(0) as? AdView)?.destroy() },
        )
    }
}

@Composable
private fun FeedPagingTrigger(
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
            .map { (lastVisibleIndex, totalCount) ->
                canLoadMore && totalCount > 0 && lastVisibleIndex >= totalCount - 3
            }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadNextPage() }
    }
}

private const val AD_ITEM_KEY = "advertisement"
private const val AD_SLOT_HEIGHT_DP = 200
private const val HORIZONTAL_PADDING_DP = 16
