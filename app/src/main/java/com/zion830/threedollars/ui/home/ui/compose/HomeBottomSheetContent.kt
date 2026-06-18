package com.zion830.threedollars.ui.home.ui.compose

import android.util.Log
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import base.compose.ColorWhite
import base.compose.Gray0
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray20
import base.compose.Gray30
import base.compose.Gray50
import base.compose.Gray60
import base.compose.Gray70
import base.compose.Gray80
import base.compose.Green
import base.compose.AppTheme
import base.compose.Pink
import base.compose.Pink400
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreScreenModel
import com.threedollar.common.serverdriven.model.StoreSectionAdditionalInfosModel
import com.threedollar.common.serverdriven.model.StoreSectionModel
import com.zion830.threedollars.ui.home.ui.HomeSheetLayout
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

private val MetadataSeparatorColor = Color(0xFFB7B7B7)
private val ImagePlaceholderColor = Color(0xFFD9D9D9)
private val StorePreviewReviewButtonBackground = Color(0xFFFFECEE)
private val StorePreviewBaseVerticalPadding = 32.dp
private val StorePreviewTitleMetadataHeight = 72.dp
private val StorePreviewActionRowHeight = 36.dp
private val StorePreviewHeaderActionGap = 16.dp
private val StorePreviewRootGap = 12.dp
private val StorePreviewImageHeight = 120.dp
private val StorePreviewActionIconSize = 14.dp
private val StorePreviewActionTrailingIconSize = 10.dp
private const val StorePreviewReviewMaxLines = 2
private const val StorePreviewReviewLineHeight = 18
private const val StorePreviewReviewVerticalPaddingValue = 11
private const val StorePreviewVisitActionWeight = 1.2f
private const val StorePreviewDefaultActionWeight = 1f
private val StorePreviewReviewVerticalPadding = StorePreviewReviewVerticalPaddingValue.dp
private val StorePreviewReviewEstimatedMaxHeight =
    (StorePreviewReviewLineHeight * StorePreviewReviewMaxLines + StorePreviewReviewVerticalPaddingValue * 2).dp
private val StorePreviewMediaGap = 8.dp
private val HomeFeedButtonBottomGap = 16.dp
private val HomeFeedButtonShape = RoundedCornerShape(16.dp)
private const val HomeFeedButtonEmoji = "\uD83C\uDF40 "
private const val HOME_LIST_ADMOB_TAG = "HomeListAdMob"

@Composable
fun HomeBottomSheetContent(
    homeListSection: HomeListSectionModel,
    storeScreen: StoreScreenModel?,
    onCardClick: (HomeListCardModel.BasicCard) -> Unit,
    onLoadNextPage: () -> Unit,
    onClosePreview: () -> Unit,
    onActionClick: (StoreActionBarModel) -> Unit,
    onFavoriteClick: (Boolean) -> Unit = { _ -> },
    onStorePreviewClick: () -> Unit = {},
    onAddPhotoClick: (() -> Unit)? = null,
    onFeedClick: () -> Unit = {},
    fullListTopPx: Int,
    collapsedPeekHeight: Dp = HomeSheetLayout.COLLAPSED_PEEK_HEIGHT_DP.dp,
    onFullListBackgroundVisibleChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val containerHeightPx = with(density) { maxHeight.toPx().roundToInt() }
        val collapsedPeekHeightPx = with(density) { collapsedPeekHeight.toPx().roundToInt() }
        val dragSettleThresholdPx = with(density) { 24.dp.toPx() }
        val storePreviewSection = storeScreen?.previewSectionOrNull()
        val storePreviewHeightPx = with(density) {
            (storePreviewSection?.previewSheetHeight() ?: collapsedPeekHeight).toPx().roundToInt()
        }
        val storePreviewOffsetPx = remember(containerHeightPx, storePreviewHeightPx) {
            HomeSheetStateCalculator.previewOffset(
                containerHeightPx = containerHeightPx,
                desiredVisibleHeightPx = storePreviewHeightPx,
                minimumVisibleHeightPx = 0,
            )
        }
        val anchors = remember(containerHeightPx, fullListTopPx, collapsedPeekHeightPx) {
            HomeSheetStateCalculator.anchors(
                containerHeightPx = containerHeightPx,
                fullListTopPx = fullListTopPx,
                collapsedPeekHeightPx = collapsedPeekHeightPx,
            )
        }
        val coroutineScope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        val isListAtTop = remember(listState) {
            derivedStateOf {
                listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
            }
        }
        var sheetOffsetPx by remember { mutableFloatStateOf(anchors.collapsedOffset) }
        var settledValue by remember { mutableStateOf(HomeSheetValue.Collapsed) }
        var lastListSettledValue by remember { mutableStateOf(HomeSheetValue.Collapsed) }
        var isSheetInitialized by remember { mutableStateOf(false) }
        var animationJob by remember { mutableStateOf<Job?>(null) }

        fun stopSheetAnimation() {
            animationJob?.cancel()
            animationJob = null
        }

        fun snapSheetBy(deltaY: Float) {
            stopSheetAnimation()
            sheetOffsetPx = anchors.clamp(sheetOffsetPx + deltaY)
        }

        fun animateSheetToOffset(targetOffset: Float, settled: HomeSheetValue) {
            stopSheetAnimation()
            settledValue = settled
            val initialOffset = sheetOffsetPx
            animationJob = coroutineScope.launch {
                animate(
                    initialValue = initialOffset,
                    targetValue = targetOffset,
                    animationSpec = tween(durationMillis = 220),
                ) { animatedOffset, _ ->
                    sheetOffsetPx = animatedOffset
                }
                sheetOffsetPx = targetOffset
            }
        }

        fun animateSheetTo(value: HomeSheetValue) {
            if (storeScreen == null) {
                lastListSettledValue = value
            }
            animateSheetToOffset(targetOffset = anchors.offsetOf(value), settled = value)
        }

        fun settleSheet(velocityY: Float = 0f, totalDragY: Float = 0f) {
            val value = when {
                totalDragY <= -dragSettleThresholdPx -> HomeSheetValue.FullList
                totalDragY >= dragSettleThresholdPx -> HomeSheetValue.Collapsed
                else -> HomeSheetStateCalculator.settleValue(
                    currentOffset = sheetOffsetPx,
                    anchors = anchors,
                    velocityY = velocityY,
                )
            }
            animateSheetTo(value)
        }

        LaunchedEffect(anchors) {
            sheetOffsetPx = if (isSheetInitialized) {
                if (storeScreen != null) {
                    storePreviewOffsetPx
                } else {
                    val restoredValue = HomeSheetStateCalculator.restoreAfterPreview(lastListSettledValue)
                    settledValue = restoredValue
                    anchors.offsetOf(restoredValue)
                }
            } else {
                isSheetInitialized = true
                anchors.collapsedOffset
            }
        }

        LaunchedEffect(storeScreen, anchors, storePreviewOffsetPx) {
            if (!isSheetInitialized) return@LaunchedEffect
            if (storeScreen != null) {
                animateSheetToOffset(
                    targetOffset = anchors.clamp(storePreviewOffsetPx),
                    settled = HomeSheetValue.Collapsed,
                )
            } else {
                animateSheetTo(HomeSheetStateCalculator.restoreAfterPreview(lastListSettledValue))
            }
        }

        DisposableEffect(Unit) {
            onDispose { animationJob?.cancel() }
        }

        val listNestedScrollConnection = remember(anchors, storeScreen) {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (storeScreen != null) return Offset.Zero

                    val dragY = available.y
                    val shouldPullSheetDown = dragY > 0f &&
                        isListAtTop.value &&
                        sheetOffsetPx < anchors.collapsedOffset
                    val shouldPushSheetUp = dragY < 0f && sheetOffsetPx > anchors.fullListOffset

                    if (!shouldPullSheetDown && !shouldPushSheetUp) return Offset.Zero

                    val previousOffset = sheetOffsetPx
                    snapSheetBy(dragY)
                    return Offset(x = 0f, y = sheetOffsetPx - previousOffset)
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (storeScreen != null) return Offset.Zero

                    val dragY = available.y
                    val shouldPullSheetDown = dragY > 0f &&
                        isListAtTop.value &&
                        sheetOffsetPx < anchors.collapsedOffset

                    if (!shouldPullSheetDown) return Offset.Zero

                    val previousOffset = sheetOffsetPx
                    snapSheetBy(dragY)
                    return Offset(x = 0f, y = sheetOffsetPx - previousOffset)
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    if (storeScreen != null) return Velocity.Zero
                    return settleIfBetweenAnchors(available.y)
                }

                override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                    if (storeScreen != null) return Velocity.Zero
                    return settleIfBetweenAnchors(available.y)
                }

                private fun settleIfBetweenAnchors(velocityY: Float): Velocity {
                    val isBetweenAnchors = sheetOffsetPx > anchors.fullListOffset &&
                        sheetOffsetPx < anchors.collapsedOffset
                    if (!isBetweenAnchors) return Velocity.Zero

                    settleSheet(velocityY = velocityY)
                    return Velocity(x = 0f, y = velocityY)
                }
            }
        }

        val isFullListSettled = storeScreen == null &&
            settledValue == HomeSheetValue.FullList &&
            abs(sheetOffsetPx - anchors.fullListOffset) <= 1f
        val topCornerRadius = if (isFullListSettled) 0.dp else 16.dp
        val sheetHeight = with(density) {
            HomeSheetStateCalculator.visibleHeight(
                containerHeightPx = containerHeightPx,
                currentOffset = sheetOffsetPx,
            ).toDp()
        }

        LaunchedEffect(isFullListSettled) {
            onFullListBackgroundVisibleChange(isFullListSettled)
        }

        if (storeScreen == null && !isFullListSettled) {
            HomeFeedButton(
                onClick = onFeedClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = sheetHeight + HomeFeedButtonBottomGap),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetHeight)
                .offset { IntOffset(x = 0, y = sheetOffsetPx.roundToInt()) }
                .clip(RoundedCornerShape(topStart = topCornerRadius, topEnd = topCornerRadius))
                .background(ColorWhite),
        ) {
            if (storeScreen == null && !isFullListSettled) {
                HomeBottomSheetHandle(
                    onHandleDrag = ::snapSheetBy,
                    onHandleDragEnd = { totalDragY -> settleSheet(totalDragY = totalDragY) },
                )
            }
            if (storeScreen != null) {
                StorePreviewContent(
                    storeScreen = storeScreen,
                    onClosePreview = onClosePreview,
                    onActionClick = onActionClick,
                    onFavoriteClick = onFavoriteClick,
                    onPreviewClick = onStorePreviewClick,
                    onAddPhotoClick = onAddPhotoClick,
                    modifier = Modifier.weight(1f),
                )
            } else {
                HomeListContent(
                    homeListSection = homeListSection,
                    listState = listState,
                    onCardClick = onCardClick,
                    onLoadNextPage = onLoadNextPage,
                    modifier = Modifier
                        .weight(1f)
                        .nestedScroll(listNestedScrollConnection),
                )
            }
        }
    }
}

@Composable
private fun HomeFeedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = HomeFeedButtonShape,
                ambientColor = Green.copy(alpha = 0.4f),
                spotColor = Green.copy(alpha = 0.4f),
            )
            .clip(HomeFeedButtonShape)
            .background(ColorWhite)
            .border(BorderStroke(1.dp, Green), HomeFeedButtonShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = HomeFeedButtonEmoji + stringResource(CommonR.string.home_feed_button),
            color = Green,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = dpToSp(14),
            lineHeight = dpToSp(20),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(name = "Home feed floating button")
@Composable
private fun HomeFeedButtonPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            HomeFeedButton(onClick = {})
        }
    }
}

@Preview(name = "Home collapsed sheet height", widthDp = 360, heightDp = 812)
@Composable
private fun HomeCollapsedSheetHeightPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray10),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(HomeSheetLayout.COLLAPSED_PEEK_HEIGHT_DP.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(ColorWhite),
            ) {
                HomeBottomSheetHandle(
                    onHandleDrag = {},
                    onHandleDragEnd = {},
                )
            }
        }
    }
}

@Composable
private fun HomeBottomSheetHandle(
    onHandleDrag: (Float) -> Unit,
    onHandleDragEnd: (Float) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .pointerInput(onHandleDrag, onHandleDragEnd) {
                var totalDragY = 0f
                detectVerticalDragGestures(
                    onDragStart = { totalDragY = 0f },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        totalDragY += dragAmount
                        onHandleDrag(dragAmount)
                    },
                    onDragEnd = { onHandleDragEnd(totalDragY) },
                    onDragCancel = { onHandleDragEnd(totalDragY) },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Gray20),
        )
    }
}

@Composable
private fun HomeListContent(
    homeListSection: HomeListSectionModel,
    listState: LazyListState,
    onCardClick: (HomeListCardModel.BasicCard) -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cards = homeListSection.cards

    LaunchedEffect(listState, homeListSection.cursor?.nextCursor, cards.size) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .map { lastVisibleIndex -> lastVisibleIndex >= cards.lastIndex - 1 }
            .distinctUntilChanged()
            .filter { shouldLoad -> shouldLoad && homeListSection.cursor?.hasMore == true }
            .collect { onLoadNextPage() }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
    ) {
        items(
            items = cards,
            key = { it.cardId },
        ) { card ->
            when (card) {
                is HomeListCardModel.BasicCard -> HomeListBasicCard(
                    card = card,
                    onClick = { onCardClick(card) },
                )
                is HomeListCardModel.EmptyCard -> HomeListEmptyCard(card = card)
                is HomeListCardModel.AdMobCard -> HomeListAdMobCard()
            }
        }
    }
}

@Composable
private fun HomeListAdMobCard() {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .height(HomeListAdMobConfig.containerHeight)
            .background(ColorWhite),
        factory = { context ->
            AdView(context).apply {
                setAdSize(HomeListAdMobConfig.adSize)
                adUnitId = context.getString(CommonR.string.admob_list_banner)
                setBackgroundColor(android.graphics.Color.WHITE)
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d(HOME_LIST_ADMOB_TAG, "Home list AdMob loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.d(HOME_LIST_ADMOB_TAG, "Home list AdMob failed: ${error.code} ${error.message}")
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}

@Composable
private fun HomeListBasicCard(
    card: HomeListCardModel.BasicCard,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HomeHeader(
                header = card.header,
                titleSize = 16,
                titleWeight = FontWeight.Bold,
                titleColor = card.header.title?.fontColor.textColorOnWhite(fallback = Gray100),
                badgeDefaultSize = 14.dp,
            )
            MetadataRows(metadata = card.metadata)
            HomeImages(images = card.images, listMode = true)
            BodiesRow(bodies = card.bodies)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Gray10),
        )
    }
}

@Composable
private fun HomeListEmptyCard(card: HomeListCardModel.EmptyCard) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val title = card.header?.title?.displayText()?.takeIf { it.isNotBlank() } ?: "조건에 맞는 가게가 없어요"
        Text(
            text = title,
            color = card.header?.title?.fontColor.toColor(fallback = Gray100),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = dpToSp(16),
            lineHeight = dpToSp(24),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        card.bodies.firstOrNull()?.let { body ->
            Text(
                text = body.displayText(),
                color = body.fontColor.toColor(fallback = Gray70),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = dpToSp(13),
                lineHeight = dpToSp(19),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun StorePreviewContent(
    storeScreen: StoreScreenModel,
    onClosePreview: () -> Unit,
    onActionClick: (StoreActionBarModel) -> Unit,
    onFavoriteClick: (Boolean) -> Unit,
    onPreviewClick: () -> Unit,
    onAddPhotoClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val preview = storeScreen.previewSectionOrNull()
    if (preview == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "",
                color = Gray70,
                fontFamily = PretendardFontFamily,
                fontSize = dpToSp(14),
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(StorePreviewRootGap),
    ) {
        item {
            StorePreviewHeaderSection(
                preview = preview,
                onClosePreview = onClosePreview,
                onActionClick = onActionClick,
                onFavoriteClick = onFavoriteClick,
                onPreviewClick = onPreviewClick,
            )
        }
        if (preview.images.isNotEmpty() || preview.bodies.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.clickable(onClick = onPreviewClick),
                    verticalArrangement = Arrangement.spacedBy(StorePreviewMediaGap),
                ) {
                    HomeImages(images = preview.images, listMode = false, onAddPhotoClick = onAddPhotoClick)
                    BodiesRow(bodies = preview.bodies)
                }
            }
        }
    }
}

@Composable
private fun StorePreviewHeaderSection(
    preview: StoreSectionModel.Preview,
    onClosePreview: () -> Unit,
    onActionClick: (StoreActionBarModel) -> Unit,
    onFavoriteClick: (Boolean) -> Unit,
    onPreviewClick: () -> Unit,
) {
    val rowActions = preview.rowActionBars()
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(StorePreviewHeaderActionGap),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onPreviewClick),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                StorePreviewTitle(header = preview.header)
                MetadataRows(metadata = preview.metadata, verticalGap = 0.dp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (preview.additionalInfos.isStoreType()) {
                    StorePreviewIconButton(
                        iconRes = if (preview.additionalInfos.isSubscriber) {
                            DesignSystemR.drawable.ic_store_preview_bookmark_solid
                        } else {
                            DesignSystemR.drawable.ic_store_preview_bookmark_line
                        },
                        tint = if (preview.additionalInfos.isSubscriber) Pink else Gray100,
                        onClick = { onFavoriteClick(preview.additionalInfos.isSubscriber) },
                    )
                }
                StorePreviewIconButton(
                    iconRes = DesignSystemR.drawable.ic_store_preview_close,
                    tint = Gray100,
                    onClick = onClosePreview,
                )
            }
        }
        if (rowActions.isNotEmpty()) {
            StorePreviewActionBarRow(
                actionBars = rowActions,
                onActionClick = onActionClick,
            )
        }
    }
}

@Composable
private fun StorePreviewTitle(header: HomeListCardHeaderModel) {
    TitleWithBadge(
        title = header.title,
        badge = header.badge,
        titleSize = 20,
        titleWeight = FontWeight.SemiBold,
        titleColor = header.title?.fontColor.textColorOnWhite(fallback = Gray100),
        maxLines = 1,
        badgeDefaultSize = 16.dp,
    )
}

@Composable
private fun TitleWithBadge(
    title: SDTextModel?,
    badge: SDImageModel?,
    titleSize: Int,
    titleWeight: FontWeight,
    titleColor: Color,
    maxLines: Int,
    badgeDefaultSize: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title.displayText(),
            color = titleColor,
            fontFamily = PretendardFontFamily,
            fontWeight = title?.fontWeight.toServerDrivenFontWeight(titleWeight),
            fontSize = dpToSp(titleSize),
            lineHeight = dpToSp(titleSize + 8),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        badge?.let { image ->
            ServerImage(
                image = image,
                modifier = Modifier.size(
                    width = (image.style?.width ?: badgeDefaultSize.value.toDouble()).dp,
                    height = (image.style?.height ?: badgeDefaultSize.value.toDouble()).dp,
                ),
                contentScale = ContentScale.Fit,
                drawPlaceholderBackground = false,
            )
        }
    }
}

@Composable
private fun StorePreviewIconButton(
    iconRes: Int,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Gray10)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun StorePreviewActionBarRow(
    actionBars: List<StoreActionBarModel>,
    onActionClick: (StoreActionBarModel) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        actionBars.forEach { actionBar ->
            StorePreviewActionButton(
                actionBar = actionBar,
                modifier = Modifier.weight(actionBar.previewActionWeight()),
                onClick = { onActionClick(actionBar) },
            )
        }
    }
}

@Preview(name = "Store preview actions compact", widthDp = 360)
@Composable
private fun StorePreviewActionBarRowCompactPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            StorePreviewActionBarRow(
                actionBars = previewStoreActionBars(),
                onActionClick = {},
            )
        }
    }
}

@Composable
private fun StorePreviewActionButton(
    actionBar: StoreActionBarModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val button = actionBar.button
    val isVisit = actionBar.isVisitAction()
    val isReview = actionBar.isReviewAction()
    val isNeutral = actionBar.isShareAction() || actionBar.isNavigationAction()
    val shape = RoundedCornerShape(18.dp)
    val background = when {
        isNeutral -> ColorWhite
        else -> button.style?.backgroundColor.toColor(
            fallback = when {
                isVisit -> Pink
                isReview -> StorePreviewReviewButtonBackground
                else -> ColorWhite
            },
        )
    }
    val textColor = button.text.fontColor.toColor(
        fallback = when {
            isVisit -> ColorWhite
            isReview -> Pink400
            else -> Gray70
        },
    )
    val border = button.style?.border
    val borderModifier = when {
        border != null -> Modifier.border(
            BorderStroke((border.width ?: 1.0).dp, border.color.toColor(fallback = Gray20)),
            shape,
        )
        !isVisit && !isReview -> Modifier.border(BorderStroke(1.dp, Gray20), shape)
        else -> Modifier
    }
    val horizontalPadding = when {
        isVisit -> PaddingValues(start = 8.dp, end = 6.dp)
        isReview -> PaddingValues(horizontal = 8.dp)
        else -> PaddingValues(horizontal = 6.dp)
    }

    Row(
        modifier = modifier
            .height(StorePreviewActionRowHeight)
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
    ) {
        if (button.imageAlignment != "END") {
            StorePreviewActionImage(actionBar = actionBar, tint = textColor)
        }
        Text(
            text = button.text.displayText(),
            color = textColor,
            fontFamily = PretendardFontFamily,
            fontWeight = if (isVisit || isReview) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = dpToSp(13),
            lineHeight = dpToSp(19),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (button.imageAlignment == "END" || isVisit) {
            StorePreviewActionImage(actionBar = actionBar, tint = textColor, trailing = true)
        }
    }
}

@Composable
private fun StorePreviewActionImage(
    actionBar: StoreActionBarModel,
    tint: Color,
    trailing: Boolean = false,
) {
    val localIconRes = when {
        actionBar.isShareAction() && !trailing -> DesignSystemR.drawable.ic_store_preview_share
        actionBar.isNavigationAction() && !trailing -> DesignSystemR.drawable.ic_store_preview_location
        else -> null
    }
    if (localIconRes != null) {
        Icon(
            painter = painterResource(id = localIconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(StorePreviewActionIconSize),
        )
        return
    }

    val image = actionBar.button.image
    if (image != null) {
        ServerImage(
            image = image,
            modifier = Modifier.size(
                width = (image.style?.width ?: if (trailing) 10.0 else 14.0).dp,
                height = (image.style?.height ?: if (trailing) 10.0 else 14.0).dp,
            ),
            contentScale = ContentScale.Fit,
            drawPlaceholderBackground = false,
        )
        return
    }
    val iconRes = when {
        actionBar.isVisitAction() && trailing -> DesignSystemR.drawable.ic_arrow_right
        else -> null
    } ?: return
    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(if (trailing) StorePreviewActionTrailingIconSize else StorePreviewActionIconSize),
    )
}

@Composable
private fun HomeHeader(
    header: HomeListCardHeaderModel,
    titleSize: Int,
    titleWeight: FontWeight,
    titleColor: Color? = null,
    badgeDefaultSize: Dp = 16.dp,
) {
    TitleWithBadge(
        title = header.title,
        badge = header.badge,
        titleSize = titleSize,
        titleWeight = titleWeight,
        titleColor = titleColor ?: header.title?.fontColor.toColor(fallback = Gray100),
        maxLines = 2,
        badgeDefaultSize = badgeDefaultSize,
    )
}

@Composable
private fun MetadataRows(
    metadata: HomeListCardMetadataModel,
    verticalGap: Dp = 4.dp,
) {
    Column(verticalArrangement = Arrangement.spacedBy(verticalGap)) {
        MetadataRow(chips = metadata.primary, defaultTextColor = Gray60)
        MetadataRow(
            chips = metadata.secondary,
            defaultTextColor = Gray60,
            firstChipColor = Gray80,
        )
    }
}

@Composable
private fun MetadataRow(
    chips: List<SDChipModel>,
    defaultTextColor: Color,
    firstChipColor: Color? = null,
) {
    if (chips.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        chips.forEachIndexed { index, chip ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .size(2.dp)
                        .clip(CircleShape)
                        .background(MetadataSeparatorColor),
                )
            }
            MetadataChip(
                chip = chip,
                textColor = if (index == 0 && firstChipColor != null) {
                    firstChipColor
                } else {
                    chip.text.fontColor.toColor(fallback = defaultTextColor)
                },
                fontWeight = chip.text.fontWeight.toServerDrivenFontWeight(FontWeight.Normal),
                modifier = if (index == 0 && chips.size > 1) Modifier.weight(1f, fill = false) else Modifier,
            )
        }
    }
}

@Composable
private fun MetadataChip(
    chip: SDChipModel,
    textColor: Color,
    fontWeight: FontWeight,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        chip.image?.let { image ->
            AsyncImage(
                model = image.url,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(
                    width = (image.style?.width ?: 12.0).dp,
                    height = (image.style?.height ?: 12.0).dp,
                ),
            )
        }
        Text(
            text = chip.text.displayText(),
            color = textColor,
            fontFamily = PretendardFontFamily,
            fontWeight = fontWeight,
            fontSize = dpToSp(14),
            lineHeight = dpToSp(20),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        chip.additionalText?.let { additionalText ->
            Text(
                text = additionalText.displayText(),
                color = textColor,
                fontFamily = PretendardFontFamily,
                fontWeight = additionalText.fontWeight.toServerDrivenFontWeight(FontWeight.Normal),
                fontSize = dpToSp(14),
                lineHeight = dpToSp(20),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun HomeImages(
    images: List<SDImageModel>,
    listMode: Boolean,
    onAddPhotoClick: (() -> Unit)? = null,
) {
    if (images.isEmpty()) return
    val cornerRadius = if (listMode) 8.dp else 10.dp
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        itemsIndexed(images, key = { index, image -> "${image.url}-$index" }) { _, image ->
            ServerImage(
                image = image,
                modifier = Modifier
                    .size(StorePreviewImageHeight)
                    .clip(RoundedCornerShape(cornerRadius)),
                contentScale = ContentScale.Crop,
            )
        }
        if (onAddPhotoClick != null) {
            item(key = "store-preview-add-photo") {
                AddPhotoTile(onClick = onAddPhotoClick)
            }
        }
    }
}

@Composable
private fun AddPhotoTile(onClick: () -> Unit) {
    val strokeWidth = 1.dp
    val dashWidth = 6.dp
    val dashGap = 4.dp
    Box(
        modifier = Modifier
            .size(StorePreviewImageHeight)
            .clip(RoundedCornerShape(10.dp))
            .background(Gray0)
            .drawBehind {
                val strokePx = strokeWidth.toPx()
                drawRoundRect(
                    color = Gray30,
                    topLeft = Offset(strokePx / 2f, strokePx / 2f),
                    size = Size(size.width - strokePx, size.height - strokePx),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                    style = Stroke(
                        width = strokePx,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(dashWidth.toPx(), dashGap.toPx()),
                        ),
                    ),
                )
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_plus),
                contentDescription = stringResource(CommonR.string.store_preview_photo_add),
                tint = Gray50,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = stringResource(CommonR.string.store_preview_photo_add),
                color = Gray50,
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = dpToSp(14),
                lineHeight = dpToSp(20),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(name = "Store preview photo add tile")
@Composable
private fun AddPhotoTilePreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AddPhotoTile(onClick = {})
        }
    }
}

@Composable
private fun BodiesRow(bodies: List<SDTextModel>) {
    if (bodies.isEmpty()) return
    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        itemsIndexed(bodies, key = { index, body -> "${body.text}-$index" }) { _, body ->
            Box(
                modifier = Modifier
                    .then(if (bodies.size == 1) Modifier.fillParentMaxWidth() else Modifier.width(300.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Gray10)
                    .padding(horizontal = 12.dp, vertical = StorePreviewReviewVerticalPadding),
            ) {
                Text(
                    text = body.displayText(),
                    color = body.fontColor.toColor(fallback = Gray70),
                    fontFamily = PretendardFontFamily,
                    fontWeight = body.fontWeight.toServerDrivenFontWeight(FontWeight.Medium),
                    fontSize = dpToSp(12),
                    lineHeight = dpToSp(StorePreviewReviewLineHeight),
                    maxLines = StorePreviewReviewMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun StoreActionButton(
    actionBar: StoreActionBarModel,
    onClick: () -> Unit,
) {
    val button = actionBar.button
    val isPrimary = button.link?.link?.contains("/visit") == true
    val background = button.style?.backgroundColor.toColor(fallback = if (isPrimary) Pink else ColorWhite)
    val border = button.style?.border
    val borderModifier = if (border != null) {
        Modifier.border(
            BorderStroke((border.width ?: 1.0).dp, border.color.toColor(fallback = Gray20)),
            RoundedCornerShape(12.dp),
        )
    } else if (!isPrimary) {
        Modifier.border(BorderStroke(1.dp, Gray20), RoundedCornerShape(12.dp))
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        val content: @Composable () -> Unit = {
            button.image?.let { image ->
                ServerImage(
                    image = image,
                    modifier = Modifier.size(
                        width = (image.style?.width ?: 18.0).dp,
                        height = (image.style?.height ?: 18.0).dp,
                    ),
                    contentScale = ContentScale.Fit,
                    drawPlaceholderBackground = false,
                )
            }
            Text(
                text = button.text.displayText(),
                color = button.text.fontColor.toColor(fallback = if (isPrimary) ColorWhite else Pink400),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = dpToSp(14),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (button.imageAlignment == "END") {
            Text(
                text = button.text.displayText(),
                color = button.text.fontColor.toColor(fallback = if (isPrimary) ColorWhite else Pink400),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = dpToSp(14),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            button.image?.let { image ->
                ServerImage(
                    image = image,
                    modifier = Modifier.size(
                        width = (image.style?.width ?: 18.0).dp,
                        height = (image.style?.height ?: 18.0).dp,
                    ),
                    contentScale = ContentScale.Fit,
                    drawPlaceholderBackground = false,
                )
            }
        } else {
            content()
        }
    }
}

private fun StoreScreenModel.previewSectionOrNull(): StoreSectionModel.Preview? {
    return sections.filterIsInstance<StoreSectionModel.Preview>().firstOrNull()
}

private fun StoreSectionModel.Preview.previewSheetHeight(): Dp {
    val headerHeight = StorePreviewTitleMetadataHeight + if (rowActionBars().isNotEmpty()) {
        StorePreviewHeaderActionGap + StorePreviewActionRowHeight
    } else {
        0.dp
    }
    val mediaHeight = when {
        images.isNotEmpty() && bodies.isNotEmpty() -> StorePreviewImageHeight + StorePreviewMediaGap + StorePreviewReviewEstimatedMaxHeight
        images.isNotEmpty() -> StorePreviewImageHeight
        bodies.isNotEmpty() -> StorePreviewReviewEstimatedMaxHeight
        else -> 0.dp
    }
    val mediaBlockHeight = if (mediaHeight > 0.dp) StorePreviewRootGap + mediaHeight else 0.dp
    return StorePreviewBaseVerticalPadding + headerHeight + mediaBlockHeight
}

private fun StoreSectionModel.Preview.rowActionBars(): List<StoreActionBarModel> {
    return actionBars.filterNot { it.isFavoriteToggleAction() || it.isCloseAction() }
}

private fun StoreSectionAdditionalInfosModel.isStoreType(): Boolean {
    return type.equals("STORE", ignoreCase = true)
}

private fun StoreActionBarModel.isFavoriteToggleAction(): Boolean {
    return isFavoriteAction() || isUnfavoriteAction()
}

private fun StoreActionBarModel.isFavoriteAction(): Boolean {
    val actionType = button.customAction?.actionType.orEmpty()
    val label = button.text.displayText()
    return actionType.equals("STORE_PREVIEW_SECTION_FAVORITE", ignoreCase = true) ||
        type.contains("FAVORITE", ignoreCase = true) ||
        label.contains("저장") ||
        label.contains("즐겨찾기")
}

private fun StoreActionBarModel.isUnfavoriteAction(): Boolean {
    val actionType = button.customAction?.actionType.orEmpty()
    return actionType.equals("STORE_PREVIEW_SECTION_UNFAVORITE", ignoreCase = true) ||
        type.contains("UNFAVORITE", ignoreCase = true) ||
        type.contains("UN_FAVORITE", ignoreCase = true)
}

private fun StoreActionBarModel.isCloseAction(): Boolean {
    val actionType = button.customAction?.actionType.orEmpty()
    return actionType.equals("STORE_PREVIEW_SECTION_CLOSE", ignoreCase = true) ||
        type.contains("CLOSE", ignoreCase = true) ||
        button.text.displayText().contains("닫기")
}

private fun StoreActionBarModel.isVisitAction(): Boolean {
    return button.link?.link?.contains("/visit") == true ||
        button.text.displayText().contains("방문")
}

private fun StoreActionBarModel.previewActionWeight(): Float {
    return if (isVisitAction()) StorePreviewVisitActionWeight else StorePreviewDefaultActionWeight
}

private fun StoreActionBarModel.isReviewAction(): Boolean {
    return button.text.displayText().contains("리뷰")
}

private fun StoreActionBarModel.isShareAction(): Boolean {
    return button.customAction?.actionType.equals("STORE_PREVIEW_SECTION_SHARE", ignoreCase = true) ||
        button.text.displayText().contains("공유")
}

private fun StoreActionBarModel.isNavigationAction(): Boolean {
    return button.customAction?.actionType.equals("STORE_PREVIEW_SECTION_NAVIGATION", ignoreCase = true) ||
        button.text.displayText().contains("길안내")
}

private fun previewStoreActionBars(): List<StoreActionBarModel> {
    return listOf(
        StoreActionBarModel(
            type = "VISIT",
            button = SDButtonModel(
                text = SDTextModel(text = "방문 인증", isHtml = false),
                imageAlignment = "END",
                link = SDLinkModel(type = "APP", link = "/visit"),
            ),
        ),
        StoreActionBarModel(
            type = "REVIEW",
            button = SDButtonModel(
                text = SDTextModel(text = "리뷰 작성", isHtml = false),
            ),
        ),
        StoreActionBarModel(
            type = "SHARE",
            button = SDButtonModel(
                text = SDTextModel(text = "공유", isHtml = false),
                customAction = SDCustomActionModel(actionType = "STORE_PREVIEW_SECTION_SHARE"),
            ),
        ),
        StoreActionBarModel(
            type = "NAVIGATION",
            button = SDButtonModel(
                text = SDTextModel(text = "길안내", isHtml = false),
                customAction = SDCustomActionModel(actionType = "STORE_PREVIEW_SECTION_NAVIGATION"),
            ),
        ),
    )
}

@Composable
private fun ServerImage(
    image: SDImageModel,
    modifier: Modifier,
    contentScale: ContentScale,
    drawPlaceholderBackground: Boolean = true,
) {
    AsyncImage(
        model = image.url,
        contentDescription = null,
        contentScale = contentScale,
        modifier = if (drawPlaceholderBackground) {
            modifier.background(ImagePlaceholderColor)
        } else {
            modifier
        },
    )
}

private fun String?.textColorOnWhite(fallback: Color): Color {
    val normalized = this?.trim()?.uppercase()
    return if (normalized == "#FFFFFF" || normalized == "#FFFFFFFF" || normalized == "WHITE") {
        fallback
    } else {
        this.toColor(fallback = fallback)
    }
}
