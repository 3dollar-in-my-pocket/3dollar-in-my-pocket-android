package com.zion830.threedollars.ui.feed.ui

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
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray0
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray70
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.section.SDAdMobSlotHeight
import com.threedollar.common.sdui.model.section.home.SDHomeNewsItemModel
import com.zion830.threedollars.BuildConfig
import com.zion830.threedollars.core.ui.component.compose.LottieFishLoading
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.ui.ads.AdMobUnitId
import com.zion830.threedollars.ui.ads.SduiAdMobSlot
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
    FeedVisibleCardTracker(listState = listState, onCardVisible = { onIntent(FeedListUiIntent.OnCardVisible(it)) })
    var scrolledListRevision by rememberSaveable { mutableStateOf(state.listRevision) }
    LaunchedEffect(state.listRevision) {
        if (state.listRevision == scrolledListRevision) return@LaunchedEffect
        listState.scrollToItem(0)
        scrolledListRevision = state.listRevision
    }
    var failedAdCardIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(Gray0),
        contentPadding = PaddingValues(horizontal = HORIZONTAL_PADDING_DP.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        state.header?.let { header -> item(key = HEADER_ITEM_KEY) { FeedListHeader(header) } }
        when {
            state.showsSkeleton -> items(SKELETON_COUNT, key = { "skeleton:$it" }) { FeedCardSkeleton() }
            state.isFailed -> item(key = ERROR_ITEM_KEY) {
                FeedErrorContent(onRetry = { onIntent(FeedListUiIntent.OnRetry) })
            }
            state.isEmpty -> item(key = EMPTY_ITEM_KEY) { FeedEmptyContent() }
            else -> {
                feedItems(
                    items = state.items.filterNot { it is SDHomeNewsItemModel.AdMob && it.cardId in failedAdCardIds },
                    onIntent = onIntent,
                    onAdLoadFailed = { failedAdCardIds = failedAdCardIds + it },
                )
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

private fun LazyListScope.feedItems(
    items: List<SDHomeNewsItemModel>,
    onIntent: (FeedListUiIntent) -> Unit,
    onAdLoadFailed: (String) -> Unit,
) {
    items(
        items = items,
        key = { FeedListItemKey.of(it.cardId) },
        contentType = { if (it is SDHomeNewsItemModel.AdMob) "ADMOB_CARD" else "CONTENT_CARD" },
    ) { item ->
        when (item) {
            is SDHomeNewsItemModel.ContentCard -> FeedCard(
                card = item,
                onClick = { onIntent(FeedListUiIntent.OnContentCardClick(item.cardId)) },
                onImageClick = { blockIndex, imageIndex ->
                    onIntent(FeedListUiIntent.OnImageClick(item.cardId, blockIndex, imageIndex))
                },
            )
            is SDHomeNewsItemModel.AdMob -> FeedAdMobCard(
                card = item.card,
                onClick = { onIntent(FeedListUiIntent.OnAdMobClick(item.cardId)) },
                onLoadFailed = { onAdLoadFailed(item.cardId) },
            )
        }
    }
}

@Composable
private fun FeedListHeader(header: SDHeaderModel) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        header.title?.let {
            SDText(
                model = it,
                color = it.fontColor.toColor(fallback = Gray100),
                fontWeight = FontWeight.Bold,
                fontSize = dpToSp(20),
                lineHeight = dpToSp(28),
            )
        }
        header.subTitle?.let {
            SDText(
                model = it,
                color = it.fontColor.toColor(fallback = Gray70),
                fontWeight = FontWeight.Normal,
                fontSize = dpToSp(14),
                lineHeight = dpToSp(20),
            )
        }
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
 * 서버 애드몹 카드. 서버 높이·광고 단위로 그리고(debug 는 테스트 단위), 끝내 못 불러오면 [onLoadFailed] 로 자리를 접는다.
 */
@Composable
private fun FeedAdMobCard(
    card: SDAdMobCardModel,
    onClick: () -> Unit,
    onLoadFailed: () -> Unit,
) {
    val context = LocalContext.current
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnLoadFailed by rememberUpdatedState(onLoadFailed)
    val fallbackAdUnitId = context.getString(CommonR.string.admob_feed_list_banner)
    SduiAdMobSlot(
        adUnitId = AdMobUnitId.resolve(serverUnitId = card.adUnitId, fallbackUnitId = fallbackAdUnitId, isDebug = BuildConfig.DEBUG),
        fallbackAdUnitId = fallbackAdUnitId,
        heightDp = SDAdMobSlotHeight.resolve(card.height),
        adListener = remember {
            object : AdListener() {
                override fun onAdClicked() = currentOnClick()
                override fun onAdFailedToLoad(error: LoadAdError) = currentOnLoadFailed()
            }
        },
    )
}

/** 화면에 들어온 카드의 cardId 를 알린다. 같은 카드가 계속 보이는 동안에는 다시 알리지 않는다. */
@Composable
private fun FeedVisibleCardTracker(listState: LazyListState, onCardVisible: (String) -> Unit) {
    val currentOnCardVisible by rememberUpdatedState(onCardVisible)
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val viewportEnd = layoutInfo.viewportEndOffset
            layoutInfo.visibleItemsInfo
                .filter { it.offset < viewportEnd && it.offset + it.size > layoutInfo.viewportStartOffset }
                .mapNotNull { FeedListItemKey.cardIdOrNull(it.key) }
                .toSet()
        }
            .distinctUntilChanged()
            .collect { cardIds -> cardIds.forEach(currentOnCardVisible) }
    }
}

private object FeedListItemKey {
    private const val CARD_PREFIX = "card:"

    fun of(cardId: String): String = CARD_PREFIX + cardId

    fun cardIdOrNull(key: Any): String? = (key as? String)?.takeIf { it.startsWith(CARD_PREFIX) }?.removePrefix(CARD_PREFIX)
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
private const val ERROR_ITEM_KEY = "error"
private const val EMPTY_ITEM_KEY = "empty"
private const val LOADING_ITEM_KEY = "loading"
private const val HORIZONTAL_PADDING_DP = 20
private const val SKELETON_COUNT = 3
private const val SKELETON_PULSE_MS = 600
