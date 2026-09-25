package com.zion830.threedollars.ui.home.ui.compose

import android.util.Log
import androidx.compose.animation.core.EaseIn
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineBreak
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
import base.compose.Gray90
import base.compose.AppTheme
import base.compose.Pink
import base.compose.Pink400
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.serverdriven.PreviewImageLayout
import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListMarkerModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreScreenModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.ui.home.ui.HomeSheetLayout
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiDefaults
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
private val HomeListCardHorizontalPadding = 20.dp
private val PreviewImageDefaultSize = 120.dp
private val PreviewImageSpacing = 6.dp
private val MapViewButtonHeight = 40.dp
private val MapViewButtonBottomInset = 20.dp
private val MapViewButtonHorizontalPadding = 12.dp
private val MapViewButtonIconSize = 16.dp
private val MapViewButtonIconTextGap = 4.dp
private const val MapViewButtonInteractiveProgress = 0.5f
internal val StorePreviewIconButtonSize = 32.dp
internal val StorePreviewIconButtonGap = 4.dp
private const val TitleBreakOpportunity = "\u200B"
private const val StorePreviewReviewMaxLines = 2
private const val StorePreviewReviewLineHeight = 18
private const val StorePreviewReviewVerticalPaddingValue = 11
private val StorePreviewReviewVerticalPadding = StorePreviewReviewVerticalPaddingValue.dp
private const val HOME_LIST_ADMOB_TAG = "HomeListAdMob"

/** 미리보기 응답 전 시트 높이. 한 번 그린 뒤에는 마지막으로 잰 높이를 유지한다 (iOS `StorePreviewLayout.defaultVisibleHeight`). */
private val StorePreviewDefaultHeight = 400.dp

@Composable
fun HomeBottomSheetContent(
    homeListSection: HomeListSectionModel,
    storeScreen: StoreScreenModel?,
    onCardClick: (HomeListCardModel.BasicCard) -> Unit,
    onLoadNextPage: () -> Unit,
    onStorePreviewClick: () -> Unit = {},
    fullListTopPx: Int,
    collapsedPeekHeight: Dp = HomeSheetLayout.COLLAPSED_PEEK_HEIGHT_DP.dp,
    onFullListBackgroundVisibleChange: (Boolean) -> Unit = {},
    onVisibleHeightChange: (Int) -> Unit = {},
    onMapViewClick: () -> Unit = {},
    storeDetailExpanded: Boolean = false,
    onStoreDetailExpandedChange: (Boolean) -> Unit = {},
    storeDetailContent: (@Composable (placeholderHeader: @Composable () -> Unit) -> Unit)? = null,
    storeDetailNavigationBar: (@Composable (Modifier) -> Unit)? = null,
    storePreview: (@Composable (showHeaderButtons: Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer()
        val containerHeightPx = with(density) { maxHeight.toPx().roundToInt() }
        val collapsedPeekHeightPx = with(density) { collapsedPeekHeight.toPx().roundToInt() }
        val dragSettleThresholdPx = with(density) { 24.dp.toPx() }
        val storeDetailFullOffsetPx = if (storeDetailNavigationBar != null) {
            with(density) { StoreDetailSduiDefaults.NavigationHeight.toPx() }
        } else {
            0f
        }
        var measuredStorePreviewHeightPx by remember { mutableStateOf<Int?>(null) }
        val storePreviewHeightPx = measuredStorePreviewHeightPx
            ?: with(density) { StorePreviewDefaultHeight.toPx().roundToInt() }
        val anchors = remember(containerHeightPx, fullListTopPx, collapsedPeekHeightPx) {
            HomeSheetStateCalculator.anchors(
                containerHeightPx = containerHeightPx,
                fullListTopPx = fullListTopPx,
                collapsedPeekHeightPx = collapsedPeekHeightPx,
            )
        }
        val storePreviewOffsetPx = remember(containerHeightPx, storePreviewHeightPx, anchors) {
            HomeSheetStateCalculator.previewTargetOffset(
                containerHeightPx = containerHeightPx,
                desiredVisibleHeightPx = storePreviewHeightPx,
                minimumVisibleHeightPx = 0,
                anchors = anchors,
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

        fun animateStorePreviewTo(expanded: Boolean) {
            stopSheetAnimation()
            val targetOffset = if (expanded) storeDetailFullOffsetPx else storePreviewOffsetPx
            val initialOffset = sheetOffsetPx
            animationJob = coroutineScope.launch {
                animate(
                    initialValue = initialOffset,
                    targetValue = targetOffset,
                    animationSpec = tween(durationMillis = StoreDetailSheetSpec.SNAP_DURATION_MS, easing = EaseIn),
                ) { animatedOffset, _ ->
                    sheetOffsetPx = animatedOffset
                }
                sheetOffsetPx = targetOffset
                onStoreDetailExpandedChange(expanded)
            }
        }

        fun settleStorePreview(velocityY: Float) {
            animateStorePreviewTo(
                expanded = StoreDetailSheetSpec.shouldExpand(
                    currentOffset = sheetOffsetPx,
                    tipOffset = storePreviewOffsetPx,
                    velocityY = velocityY,
                    fullOffset = storeDetailFullOffsetPx,
                )
            )
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
                    if (storeDetailExpanded) storeDetailFullOffsetPx else storePreviewOffsetPx
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
                if (storeDetailExpanded) return@LaunchedEffect
                animateSheetToOffset(
                    targetOffset = storePreviewOffsetPx,
                    settled = HomeSheetValue.Collapsed,
                )
            } else {
                animateSheetTo(HomeSheetStateCalculator.restoreAfterPreview(lastListSettledValue))
            }
        }

        LaunchedEffect(storeDetailExpanded) {
            if (!isSheetInitialized || storeScreen == null) return@LaunchedEffect
            val targetOffset = if (storeDetailExpanded) storeDetailFullOffsetPx else storePreviewOffsetPx
            if (abs(sheetOffsetPx - targetOffset) > 1f) animateStorePreviewTo(storeDetailExpanded)
        }

        DisposableEffect(Unit) {
            onDispose { animationJob?.cancel() }
        }

        val storePreviewNestedScrollConnection = remember(storePreviewOffsetPx, storeDetailFullOffsetPx, storeDetailExpanded) {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val dragY = available.y
                    val canMoveUp = dragY < 0f && sheetOffsetPx > storeDetailFullOffsetPx
                    if (!canMoveUp || source != NestedScrollSource.UserInput) return Offset.Zero
                    return moveStorePreviewSheet(dragY)
                }

                override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                    val dragY = available.y
                    val canMoveDown = dragY > 0f && sheetOffsetPx < storePreviewOffsetPx
                    if (!canMoveDown || source != NestedScrollSource.UserInput) return Offset.Zero
                    return moveStorePreviewSheet(dragY)
                }

                override suspend fun onPreFling(available: Velocity): Velocity = settleIfMoved(available.y)

                override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
                    settleIfMoved(available.y)

                private fun moveStorePreviewSheet(dragY: Float): Offset {
                    stopSheetAnimation()
                    val previousOffset = sheetOffsetPx
                    sheetOffsetPx = (sheetOffsetPx + dragY).coerceIn(storeDetailFullOffsetPx, storePreviewOffsetPx)
                    return Offset(x = 0f, y = sheetOffsetPx - previousOffset)
                }

                private fun settleIfMoved(velocityY: Float): Velocity {
                    val settledOffset = if (storeDetailExpanded) storeDetailFullOffsetPx else storePreviewOffsetPx
                    if (abs(sheetOffsetPx - settledOffset) <= 1f) return Velocity.Zero
                    settleStorePreview(velocityY)
                    return Velocity(x = 0f, y = velocityY)
                }
            }
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
        val isStoreDetailFull = storeScreen != null && sheetOffsetPx <= storeDetailFullOffsetPx + 1f
        val storeDetailNavigationAlpha = if (storeScreen != null) {
            StoreDetailSheetSpec.expandProgress(
                currentOffset = sheetOffsetPx,
                tipOffset = storePreviewOffsetPx,
                fullOffset = storeDetailFullOffsetPx,
            )
        } else {
            0f
        }
        val topCornerRadius = if (isFullListSettled || isStoreDetailFull) 0.dp else 16.dp
        val sheetVisibleHeightPx = HomeSheetStateCalculator.visibleHeight(
            containerHeightPx = containerHeightPx,
            currentOffset = sheetOffsetPx,
        ).roundToInt()
        val sheetHeight = with(density) { sheetVisibleHeightPx.toDp() }
        val mapViewButtonProgress = if (storeScreen == null) {
            HomeSheetStateCalculator.fullListProgress(currentOffset = sheetOffsetPx, anchors = anchors)
        } else {
            0f
        }

        LaunchedEffect(isFullListSettled) {
            onFullListBackgroundVisibleChange(isFullListSettled)
        }

        LaunchedEffect(sheetVisibleHeightPx) {
            onVisibleHeightChange(sheetVisibleHeightPx)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetHeight)
                .offset { IntOffset(x = 0, y = sheetOffsetPx.roundToInt()) }
                .clip(RoundedCornerShape(topStart = topCornerRadius, topEnd = topCornerRadius))
                .background(ColorWhite),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (storeScreen == null && !isFullListSettled) {
                    HomeBottomSheetHandle(
                        onHandleDrag = ::snapSheetBy,
                        onHandleDragEnd = { totalDragY -> settleSheet(totalDragY = totalDragY) },
                    )
                }
                if (storeScreen != null && storeDetailExpanded && storeDetailContent != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .nestedScroll(storePreviewNestedScrollConnection)
                    ) {
                        storeDetailContent {
                            storePreview?.invoke(false)
                        }
                    }
                } else if (storeScreen != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .nestedScroll(storePreviewNestedScrollConnection)
                            .verticalScroll(rememberScrollState())
                            .noRippleClickable(onClick = onStorePreviewClick),
                    ) {
                        if (storePreview != null) {
                            Box(modifier = Modifier.onSizeChanged { measuredStorePreviewHeightPx = it.height }) {
                                storePreview(true)
                            }
                        }
                    }
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
            if (mapViewButtonProgress > 0f) {
                MapViewButton(
                    onClick = {
                        onMapViewClick()
                        animateSheetTo(HomeSheetValue.Collapsed)
                    },
                    enabled = mapViewButtonProgress >= MapViewButtonInteractiveProgress,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = MapViewButtonBottomInset)
                        .alpha(mapViewButtonProgress),
                )
            }
        }
        if (storeDetailNavigationBar != null && storeDetailNavigationAlpha > 0f) {
            storeDetailNavigationBar(Modifier.alpha(storeDetailNavigationAlpha))
        }
    }
}

@Composable
private fun MapViewButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(MapViewButtonHeight / 2)
    Row(
        modifier = modifier
            .height(MapViewButtonHeight)
            .shadow(elevation = 2.dp, shape = shape)
            .clip(shape)
            .background(Gray90)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = MapViewButtonHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MapViewButtonIconTextGap),
    ) {
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_map),
            contentDescription = null,
            tint = ColorWhite,
            modifier = Modifier.size(MapViewButtonIconSize),
        )
        Text(
            text = stringResource(CommonR.string.map_view),
            color = ColorWhite,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = dpToSp(12),
            maxLines = 1,
        )
    }
}

@Preview(name = "Map view button")
@Composable
private fun MapViewButtonPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MapViewButton(onClick = {})
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
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = HomeListCardHorizontalPadding),
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
            }
            HomeImages(images = card.images, listMode = true, horizontalPadding = HomeListCardHorizontalPadding)
            BodiesRow(bodies = card.bodies, horizontalPadding = HomeListCardHorizontalPadding)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Gray10),
        )
    }
}

@Preview(name = "Home list card long title", widthDp = 360)
@Composable
private fun HomeListBasicCardLongTitlePreview() {
    AppTheme {
        HomeListBasicCard(
            card = previewHomeListBasicCardWithLongTitle(),
            onClick = {},
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
private fun TitleWithBadge(
    title: SDTextModel?,
    badge: SDImageModel?,
    titleSize: Int,
    titleWeight: FontWeight,
    titleColor: Color,
    maxLines: Int,
    badgeDefaultSize: Dp,
    modifier: Modifier = Modifier,
    lineBreak: LineBreak? = null,
    fillTitleWidth: Boolean = false,
) {
    val rawTitleText = title.displayText()
    val titleText = if (lineBreak != null) {
        rawTitleText.withTitleBreakOpportunities()
    } else {
        rawTitleText
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = titleText,
            color = titleColor,
            fontFamily = PretendardFontFamily,
            fontWeight = title?.fontWeight.toServerDrivenFontWeight(titleWeight),
            fontSize = dpToSp(titleSize),
            lineHeight = dpToSp(titleSize + 8),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            style = if (lineBreak != null) {
                TextStyle(lineBreak = lineBreak)
            } else {
                TextStyle.Default
            },
            modifier = Modifier.weight(1f, fill = fillTitleWidth),
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
internal fun StorePreviewIconButton(
    iconRes: Int,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(StorePreviewIconButtonSize)
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
        lineBreak = LineBreak.Heading,
    )
}

@Composable
private fun MetadataRows(
    metadata: HomeListCardMetadataModel,
    verticalGap: Dp = 4.dp,
) {
    Column(verticalArrangement = Arrangement.spacedBy(verticalGap)) {
        MetadataRow(chips = metadata.primary, defaultTextColor = Gray60, shrinkChipIndex = 0)
        MetadataRow(
            chips = metadata.secondary,
            defaultTextColor = Gray60,
            firstChipColor = Gray80,
            shrinkChipIndex = metadata.secondary.lastIndex,
        )
    }
}

/**
 * @param shrinkChipIndex 줄이 넘칠 때 말줄임되는 칩. 카테고리 줄은 첫 칩(카테고리 목록), 영업 상태 줄은 끝 칩이
 * 줄어야 "영업 중"·"영업 종료" 가 "…" 로 가려지지 않는다.
 */
@Composable
private fun MetadataRow(
    chips: List<SDChipModel>,
    defaultTextColor: Color,
    firstChipColor: Color? = null,
    shrinkChipIndex: Int = 0,
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
                modifier = if (index == shrinkChipIndex && chips.size > 1) Modifier.weight(1f, fill = false) else Modifier,
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
    onImageClick: ((index: Int) -> Unit)? = null,
    horizontalPadding: Dp = 0.dp,
) {
    if (images.isEmpty()) return
    val cornerRadius = if (listMode) 8.dp else 10.dp
    val rowHeight = PreviewImageLayout.rowHeight(images, PreviewImageDefaultSize.value).dp

    BoxWithConstraints {
        val availableWidth = (maxWidth - horizontalPadding * 2).value
        LazyRow(
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(PreviewImageSpacing),
        ) {
            itemsIndexed(images, key = { index, image -> "${image.url}-$index" }) { index, image ->
                ServerImage(
                    image = image,
                    modifier = Modifier
                        .size(
                            width = PreviewImageLayout.itemWidth(
                                style = image.style,
                                count = images.size,
                                availableWidth = availableWidth,
                                spacing = PreviewImageSpacing.value,
                                defaultWidth = PreviewImageDefaultSize.value,
                            ).dp,
                            height = PreviewImageLayout.itemHeight(
                                style = image.style,
                                defaultHeight = PreviewImageDefaultSize.value,
                            ).dp,
                        )
                        .clip(RoundedCornerShape(cornerRadius))
                        .then(if (onImageClick != null) Modifier.clickable { onImageClick(index) } else Modifier),
                    contentScale = ContentScale.Crop,
                )
            }
            if (onAddPhotoClick != null) {
                item(key = "store-preview-add-photo") {
                    AddPhotoTile(tileSize = rowHeight, onClick = onAddPhotoClick)
                }
            }
        }
    }
}

@Composable
internal fun AddPhotoTile(tileSize: Dp = PreviewImageDefaultSize, onClick: () -> Unit) {
    val strokeWidth = 1.dp
    val dashWidth = 6.dp
    val dashGap = 4.dp
    Box(
        modifier = Modifier
            .size(tileSize)
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
private fun BodiesRow(bodies: List<SDTextModel>, horizontalPadding: Dp = 0.dp) {
    if (bodies.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
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

private fun String.withTitleBreakOpportunities(): String {
    if (length <= 1) return this

    return buildString {
        var index = 0
        while (index < this@withTitleBreakOpportunities.length) {
            val codePoint = this@withTitleBreakOpportunities.codePointAt(index)
            val nextIndex = index + Character.charCount(codePoint)
            append(this@withTitleBreakOpportunities, index, nextIndex)
            if (
                nextIndex < this@withTitleBreakOpportunities.length &&
                !Character.isWhitespace(codePoint) &&
                !Character.isWhitespace(this@withTitleBreakOpportunities.codePointAt(nextIndex))
            ) {
                append(TitleBreakOpportunity)
            }
            index = nextIndex
        }
    }
}

private fun previewHomeListBasicCardWithLongTitle(): HomeListCardModel.BasicCard {
    return HomeListCardModel.BasicCard(
        type = "BASIC_CARD",
        cardId = "S:100186",
        header = HomeListCardHeaderModel(
            title = SDTextModel(
                text = "ㅂㅈㅂㅈㄷㅂㅈㅁㅁㄴㅋㅌㅂㅈㅂㅈㄷㅂㅈㅁㅁㄴㅋㅌ",
                isHtml = false,
                fontColor = "#0F0F0F",
            ),
        ),
        metadata = HomeListCardMetadataModel(
            primary = listOf(
                SDChipModel(text = SDTextModel(text = "떡볶이, 계란빵, 땅콩빵", isHtml = false)),
                SDChipModel(text = SDTextModel(text = "5.0 (1)", isHtml = false)),
            ),
            secondary = listOf(
                SDChipModel(text = SDTextModel(text = "0m", isHtml = false)),
                SDChipModel(text = SDTextModel(text = "최근 방문 0명", isHtml = false)),
            ),
        ),
        marker = HomeListMarkerModel(
            focused = SDChipModel(text = SDTextModel(text = "", isHtml = false)),
            unfocused = SDChipModel(text = SDTextModel(text = "", isHtml = false)),
            location = SDLocationModel(latitude = 37.1, longitude = 127.2),
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
