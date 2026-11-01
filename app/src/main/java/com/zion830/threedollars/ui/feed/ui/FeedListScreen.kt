package com.zion830.threedollars.ui.feed.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import base.compose.ColorWhite
import base.compose.Gray0
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray30
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

@Composable
internal fun FeedListScreen(
    state: FeedListUiState,
    onIntent: (FeedListUiIntent) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    FeedPagingTrigger(
        listState = listState,
        canLoadMore = state.canLoadMore,
        onLoadNextPage = { onIntent(FeedListUiIntent.OnLoadNextPage) },
    )
    var scrolledListRevision by rememberSaveable { mutableStateOf(state.listRevision) }
    LaunchedEffect(state.listRevision) {
        if (state.listRevision == scrolledListRevision) return@LaunchedEffect
        listState.scrollToItem(0)
        scrolledListRevision = state.listRevision
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(Gray0),
        contentPadding = PaddingValues(horizontal = HORIZONTAL_PADDING_DP.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = HEADER_ITEM_KEY) { FeedListHeader() }
        item(key = AD_ITEM_KEY) {
            FeedAdvertisementSlot(
                advertisement = state.advertisement,
                onClick = { onIntent(FeedListUiIntent.OnAdvertisementClick(it)) },
            )
        }
        when {
            state.showsSkeleton -> items(SKELETON_COUNT, key = { "skeleton:$it" }) { FeedCardSkeleton() }
            state.isFailed -> item(key = ERROR_ITEM_KEY) {
                FeedErrorContent(onRetry = { onIntent(FeedListUiIntent.OnRetry) })
            }
            state.isEmpty -> item(key = EMPTY_ITEM_KEY) { FeedEmptyContent() }
            else -> {
                items(state.feeds, key = { it.feedId }) { feed ->
                    FeedCard(feed = feed, onClick = { onIntent(FeedListUiIntent.OnFeedClick(feed)) })
                }
                if (state.isLoading) {
                    item(key = LOADING_ITEM_KEY) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            LottieFishLoading(modifier = Modifier.size(72.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedListHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = stringResource(CommonR.string.feed_list_header_title),
            color = Gray100,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = dpToSp(20),
            lineHeight = dpToSp(28),
        )
        Text(
            text = stringResource(CommonR.string.feed_list_header_description),
            color = Gray70,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = dpToSp(14),
            lineHeight = dpToSp(20),
        )
    }
}

@Composable
private fun FeedCardSkeleton() {
    val transition = rememberInfiniteTransition(label = "feed_skeleton")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(SKELETON_PULSE_MS), RepeatMode.Reverse),
        label = "feed_skeleton_alpha",
    )
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite, shape)
            .padding(16.dp)
            .alpha(alpha),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SkeletonBlock(width = 140.dp, height = 26.dp, shape = CircleShape)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonBlock(width = 56.dp, height = 56.dp, shape = RoundedCornerShape(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonBlock(width = 80.dp, height = 14.dp)
                SkeletonBlock(width = 160.dp, height = 20.dp)
            }
        }
        SkeletonBlock(height = 72.dp, shape = RoundedCornerShape(12.dp))
    }
}

@Composable
private fun SkeletonBlock(
    height: Dp,
    width: Dp? = null,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier = Modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .background(Gray10, shape),
    )
}

@Composable
private fun FeedErrorContent(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(CommonR.string.feed_list_error_title),
            color = Gray70,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = dpToSp(16),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(CommonR.string.feed_list_error_retry),
            modifier = Modifier
                .clip(CircleShape)
                .border(1.dp, Gray30, CircleShape)
                .background(ColorWhite)
                .clickable(onClick = onRetry)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Gray100,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = dpToSp(14),
        )
    }
}

@Composable
private fun FeedEmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
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

private const val HEADER_ITEM_KEY = "header"
private const val AD_ITEM_KEY = "advertisement"
private const val ERROR_ITEM_KEY = "error"
private const val EMPTY_ITEM_KEY = "empty"
private const val LOADING_ITEM_KEY = "loading"
private const val AD_SLOT_HEIGHT_DP = 200
private const val HORIZONTAL_PADDING_DP = 20
private const val SKELETON_COUNT = 3
private const val SKELETON_PULSE_MS = 600
