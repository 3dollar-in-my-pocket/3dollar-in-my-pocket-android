package com.zion830.threedollars.ui.storeDetail.sdui.ui

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray100
import base.compose.Gray20
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDRelatedStoresSectionModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.threedollar.common.sdui.model.section.SDStoreTabSectionModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.component.SDActionBarRow
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreSectionSlots
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreSkeletonSection
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreTabSectionDefaults
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailScrollSpec
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiState
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreSectionFragment
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

object StoreDetailSduiDefaults {
    val NavigationHeight = 56.dp
    val NavigationHorizontalPadding = 16.dp
    val BackIconSize = 24.dp
    val TrailingIconSize = 28.dp
    val TrailingIconSpacing = 12.dp
    const val BOTTOM_BAR_ANIMATION_MS = 300

    /** 활동 유도 모달이 하단 칩 바를 가리지 않도록 띄우는 높이. */
    val DisplayItemBottomInset = 68.dp
}

/**
 * 가게 상세 본문(상단 네비 + 섹션 목록 + 하단 칩 바). 홈 시트의 full 상태와 전체 화면 상세가 같이 쓴다.
 *
 * @param onBack `chevron_left` 탭. 시트에서는 tip 으로 접고, 전체 화면에서는 뒤로 간다.
 * @param onClose `x` 탭. 시트에서는 시트를 닫고, 전체 화면에서는 화면을 닫는다.
 * @param placeholderHeader 응답 전 스켈레톤 위에 보여줄 헤더(홈 미리보기 데이터로 만든다).
 * @param inSheet 홈 시트 안이면 true. 상단 네비는 시트 밖 고정 위치에서 홈이 fade 로 그리고([StoreDetailNavigationBar]),
 * 하단 칩 바는 GNB 위에 놓이므로 시스템 내비게이션 바 여백을 더하지 않는다.
 * @param isDisplayed 홈 시트는 tip 상태에서도 상세를 미리 그려 둔다. 실제로 보일 때만 섹션 노출 로그를 보낸다.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreDetailSduiContent(
    state: StoreDetailSduiUiState,
    listState: LazyListState,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onFavoriteClick: () -> Unit,
    onAction: (SDActionEvent) -> Unit,
    onImageClick: (List<SDImageModel>, Int) -> Unit,
    onImpression: (key: String, log: SDLogModel?) -> Unit,
    modifier: Modifier = Modifier,
    slots: SDStoreSectionSlots = SDStoreSectionSlots(),
    placeholderHeader: (@Composable () -> Unit)? = null,
    inSheet: Boolean = false,
    isDisplayed: Boolean = true,
) {
    val density = LocalDensity.current
    val sections = state.sections
    val previewIndex = remember(sections) { sections.previewIndex() }
    val preview = previewIndex?.let { sections[it] as? SDStorePreviewSectionModel }
    val tabTargets = remember(sections) { sections.tabTargets() }
    val tabHeightPx = with(density) { SDStoreTabSectionDefaults.Height.toPx() }

    var listTop by remember { mutableFloatStateOf(0f) }
    var actionBarBottom by remember { mutableStateOf<Float?>(null) }
    var bottomBarHeightPx by remember { mutableFloatStateOf(0f) }

    val selectedTab by remember(tabTargets) {
        derivedStateOf {
            val anchor = listState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.offset + it.size > tabHeightPx && sections.getOrNull(it.index) !is SDStoreTabSectionModel }
                ?.index ?: listState.firstVisibleItemIndex
            StoreDetailScrollSpec.selectedTab(tabTargets, anchor, isAtBottom = !listState.canScrollForward && listState.canScrollBackward)
        }
    }
    val isBottomBarVisible by remember(previewIndex, preview) {
        derivedStateOf {
            StoreDetailScrollSpec.isBottomBarVisible(
                hasActionBars = !preview?.actionBars.isNullOrEmpty(),
                previewIndex = previewIndex,
                firstVisibleIndex = listState.firstVisibleItemIndex,
                actionBarBottom = actionBarBottom,
                viewportTop = listTop,
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorWhite)
    ) {
        if (!inSheet) {
            StoreDetailNavigationBar(
                state = state,
                listState = listState,
                onBack = onBack,
                onFavoriteClick = onFavoriteClick,
                onClose = onClose,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onGloballyPositioned { listTop = it.boundsInWindow().top }
        ) {
            if (!state.hasContent) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    placeholderHeader?.invoke()
                    SDStoreSkeletonSection()
                }
            } else {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    sections.forEachIndexed { index, section ->
                        val key = "$index-${section.type}"
                        if (section is SDStoreTabSectionModel) {
                            stickyHeader(key = key, contentType = section.type) {
                                SDStoreSection(section = section, onAction = onAction, selectedTabIndex = selectedTab)
                            }
                        } else {
                            item(key = key, contentType = section.type) {
                                SectionImpressionEffect(key = key, section = section, isDisplayed = isDisplayed, onImpression = onImpression)
                                SDStoreSection(
                                    section = section,
                                    onAction = onAction,
                                    onImageClick = onImageClick,
                                    slots = slots,
                                    previewActionBarsModifier = Modifier.onGloballyPositioned {
                                        actionBarBottom = it.boundsInWindow().bottom
                                    },
                                )
                            }
                        }
                    }
                    item(key = "bottom-bar-space") {
                        Spacer(modifier = Modifier.height(with(density) { bottomBarHeightPx.toDp() }))
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = fadeIn(tween(StoreDetailSduiDefaults.BOTTOM_BAR_ANIMATION_MS, easing = EaseIn)),
                exit = fadeOut(tween(StoreDetailSduiDefaults.BOTTOM_BAR_ANIMATION_MS, easing = EaseIn)),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                StoreDetailBottomChipBar(
                    actionBars = preview?.actionBars.orEmpty(),
                    onAction = onAction,
                    applyNavigationBarInset = !inSheet,
                    modifier = Modifier.onSizeChanged { bottomBarHeightPx = it.height.toFloat() }
                )
            }
        }
    }
}

@Composable
private fun SectionImpressionEffect(
    key: String,
    section: SDSectionModel,
    isDisplayed: Boolean,
    onImpression: (String, SDLogModel?) -> Unit,
) {
    val log = when (section) {
        is SDRelatedStoresSectionModel -> section.impressionLog
        is SDStoreAdmobSectionModel -> section.cards?.firstOrNull()?.impressionLog
        else -> null
    } ?: return
    LaunchedEffect(key, isDisplayed) { if (isDisplayed) onImpression(key, log) }
}

/**
 * 상세 상단 네비(`<`, 가게명, 저장, 닫기). 가게명은 PREVIEW 섹션이 스크롤로 가려질수록 진해진다.
 * 전체 화면은 [StoreDetailSduiContent] 가, 홈 시트는 시트 밖 고정 위치에서 홈이 직접 그린다.
 */
@Composable
fun StoreDetailNavigationBar(
    state: StoreDetailSduiUiState,
    listState: LazyListState,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val previewIndex = remember(state.sections) { state.sections.previewIndex() }
    val titleAlpha by remember(previewIndex) {
        derivedStateOf {
            StoreDetailScrollSpec.titleAlpha(
                firstVisibleIndex = listState.firstVisibleItemIndex,
                firstVisibleOffsetDp = with(density) { listState.firstVisibleItemScrollOffset.toDp().value },
                previewIndex = previewIndex,
            )
        }
    }
    val title = state.storeName
    val isFavorite = state.isFavorite
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(StoreDetailSduiDefaults.NavigationHeight)
            .background(ColorWhite)
            .padding(horizontal = StoreDetailSduiDefaults.NavigationHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_arrow_left),
            contentDescription = null,
            modifier = Modifier
                .size(StoreDetailSduiDefaults.BackIconSize)
                .noRippleClickable(onClick = onBack)
        )
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .alpha(titleAlpha),
            color = Gray100,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = dpToSp(20),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(StoreDetailSduiDefaults.TrailingIconSpacing)) {
            NavigationIcon(
                iconRes = if (isFavorite) DesignSystemR.drawable.ic_store_preview_bookmark_solid else DesignSystemR.drawable.ic_store_preview_bookmark_line,
                tint = if (isFavorite) Pink else Gray100,
                onClick = onFavoriteClick
            )
            NavigationIcon(iconRes = DesignSystemR.drawable.ic_store_preview_close, tint = Gray100, onClick = onClose)
        }
    }
}

@Composable
private fun NavigationIcon(iconRes: Int, tint: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(StoreDetailSduiDefaults.TrailingIconSize)
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(iconRes), contentDescription = null, colorFilter = ColorFilter.tint(tint))
    }
}

/**
 * PREVIEW 액션 버튼 줄이 스크롤로 사라졌을 때 하단에 고정되는 칩 바.
 */
@Composable
private fun StoreDetailBottomChipBar(
    actionBars: List<SDActionBarModel>,
    onAction: (SDActionEvent) -> Unit,
    applyNavigationBarInset: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .then(if (applyNavigationBarInset) Modifier.navigationBarsPadding() else Modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Gray20)
        )
        SDActionBarRow(
            actionBars = actionBars,
            onAction = onAction,
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
        )
    }
}

/**
 * [index] 섹션이 고정 탭 바로 아래에 오도록 스크롤한다.
 */
suspend fun LazyListState.scrollToSection(index: Int, sections: List<SDSectionModel>, tabHeightPx: Int) {
    val tabIndex = sections.indexOfFirst { it is SDStoreTabSectionModel }
    val offset = if (tabIndex in 0 until index) -tabHeightPx else 0
    scrollToItem(index = index, scrollOffset = offset)
}

private fun List<SDSectionModel>.previewIndex(): Int? =
    indexOfFirst { it is SDStorePreviewSectionModel }.takeIf { it >= 0 }

private fun List<SDSectionModel>.tabTargets(): List<Int?> {
    val tabSection = filterIsInstance<SDStoreTabSectionModel>().firstOrNull() ?: return emptyList()
    return tabSection.tabs.orEmpty().map { tab ->
        val fragment = StoreSectionFragment.parseLink(tab.button?.link?.link)?.fragment
        StoreSectionFragment.resolveIndex(this, fragment)
    }
}
