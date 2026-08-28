package com.zion830.threedollars.ui.storeDetail.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.SDImpressionLogModel
import com.threedollar.common.serverdriven.model.SDViewLogModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.zion830.threedollars.core.ui.serverdriven.SDTextRenderer
import kotlinx.coroutines.launch
import java.net.URI

@Composable
fun StoreDetailV2Content(
    screen: StoreDetailScreenModel,
    onAction: (StoreActionBarModel) -> Unit,
    onFavoriteToggle: (Boolean) -> Unit = {},
    favoriteOverride: Boolean? = null,
    onViewLog: (SDViewLogModel) -> Unit = {},
    onImpression: (String, SDImpressionLogModel) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val coroutineScope = rememberCoroutineScope()
    val preview = screen.sections.filterIsInstance<StoreDetailSectionModel.Preview>().firstOrNull()
    val previewIndex = screen.sections.indexOfFirst { it is StoreDetailSectionModel.Preview }
    var previewActionsVisible by remember(screen) { mutableStateOf(true) }
    val showStickyActions by remember(screen.sections, listState) {
        derivedStateOf {
            shouldShowStoreDetailStickyActions(
                hasActions = preview?.actionBars?.isNotEmpty() == true,
                previewIndex = previewIndex,
                firstVisibleItemIndex = listState.firstVisibleItemIndex,
                previewActionsVisible = previewActionsVisible,
            )
        }
    }

    LaunchedEffect(screen.viewLog) {
        onViewLog(screen.viewLog)
    }

    LaunchedEffect(screen.sections, listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.map { it.index }.toSet() }
            .collect { visibleIndices ->
                visibleIndices.forEach { index ->
                    val section = screen.sections.getOrNull(index)
                    if (section is StoreDetailSectionModel.RelatedStores) {
                        onImpression("RELATED_STORES:${section.impressionLog.objectId}", section.impressionLog)
                    }
                }
            }
    }

    Box(modifier = modifier.fillMaxSize().background(ColorWhite)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (showStickyActions) 84.dp else 24.dp),
        ) {
            itemsIndexed(
                items = screen.sections,
                key = { index, section -> "${section.type}-$index" },
                contentType = { _, section -> section.type },
            ) { _, section ->
                when (section) {
                    is StoreDetailSectionModel.Callout -> StoreDetailCalloutSection(section, onAction)
                    is StoreDetailSectionModel.Preview -> StoreDetailPreviewSection(
                        section = section,
                        onAction = onAction,
                        onFavoriteToggle = onFavoriteToggle,
                        favoriteOverride = favoriteOverride,
                        onActionRowVisibilityChanged = { previewActionsVisible = it },
                    )
                    is StoreDetailSectionModel.AdMob -> StoreDetailAdMobSection(section, onImpression)
                    is StoreDetailSectionModel.Tab -> StoreDetailTabSection(section) { actionBar ->
                        val targetType = actionBar.targetSectionTypeOrNull()
                        val targetIndex = targetType?.let(screen.sections::indexOfStoreDetailTarget) ?: -1
                        if (targetIndex >= 0) {
                            coroutineScope.launch { listState.animateScrollToItem(targetIndex) }
                        } else {
                            onAction(actionBar)
                        }
                    }
                    is StoreDetailSectionModel.Map -> StoreDetailMapSection(section, onAction)
                    is StoreDetailSectionModel.Edit -> StoreDetailEditSection(section, onAction)
                    is StoreDetailSectionModel.Coupon -> StoreDetailCouponSection(section, onAction)
                    is StoreDetailSectionModel.Visit -> StoreDetailVisitSection(section)
                    is StoreDetailSectionModel.Post -> StoreDetailPostSection(section, onAction)
                    is StoreDetailSectionModel.Image -> StoreDetailImageSection(section, onAction)
                    is StoreDetailSectionModel.AppearanceDay -> StoreDetailAppearanceDaySection(section, onAction)
                    is StoreDetailSectionModel.RelatedStores -> StoreDetailRelatedStoresSection(section, onAction)
                    is StoreDetailSectionModel.Cta -> StoreDetailCtaSection(section, onAction)
                    is StoreDetailSectionModel.Review -> StoreDetailReviewSection(section, onAction)
                    is StoreDetailSectionModel.InfoV1 -> StoreDetailInfoV1Section(section)
                    is StoreDetailSectionModel.InfoV2 -> StoreDetailInfoV2Section(section, onAction)
                }
                StoreDetailSectionDivider()
            }
        }

        if (showStickyActions && preview != null) {
            StoreDetailStickyActions(
                actions = preview.actionBars,
                onAction = onAction,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

internal fun shouldShowStoreDetailStickyActions(
    hasActions: Boolean,
    previewIndex: Int,
    firstVisibleItemIndex: Int,
    previewActionsVisible: Boolean,
): Boolean = hasActions && previewIndex >= 0 &&
    (!previewActionsVisible || firstVisibleItemIndex > previewIndex)

@Composable
private fun StoreDetailStickyActions(
    actions: List<StoreActionBarModel>,
    onAction: (StoreActionBarModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        actions.take(2).forEach { action ->
            StoreDetailActionButton(
                action = action,
                onClick = { onAction(action) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
internal fun StoreDetailSectionHeader(
    title: SDTextModel,
    subTitle: SDTextModel? = null,
    trailingButton: SDButtonModel? = null,
    onAction: ((StoreActionBarModel) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            SDTextRenderer(
                text = title,
                color = Gray100,
                fontSizeDp = 18,
                lineHeightDp = 24,
            )
            subTitle?.let { subtitle ->
                SDTextRenderer(text = subtitle, fontSizeDp = 13, lineHeightDp = 19)
            }
        }
        if (trailingButton != null && onAction != null) {
            StoreDetailTextButton(
                button = trailingButton,
                onClick = { onAction(StoreActionBarModel(type = "HEADER_TRAILING", button = trailingButton)) },
            )
        }
    }
}

@Composable
private fun StoreDetailSectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray10)
            .padding(top = 8.dp),
    )
}

internal fun syntheticActionBar(
    button: SDButtonModel,
    type: String,
) = StoreActionBarModel(type = type, button = button)

internal fun StoreActionBarModel.targetSectionTypeOrNull(): String? {
    val params = button.customAction?.extraParams.orEmpty()
    val extraTarget = sequenceOf("TARGET_SECTION_TYPE", "SECTION_TYPE")
        .mapNotNull { key -> params[key].stringValueOrNull() }
        .firstOrNull()
    if (!extraTarget.isNullOrBlank()) return extraTarget
    val link = button.link?.link.orEmpty()
    val queryTarget = Regex("(?:sectionType|targetSectionType)=([^&]+)", RegexOption.IGNORE_CASE)
        .find(link)
        ?.groupValues
        ?.getOrNull(1)
    if (!queryTarget.isNullOrBlank()) return queryTarget
    return when (runCatching { URI(link).fragment?.lowercase() }.getOrNull()) {
        "home" -> "PREVIEW"
        "info" -> "INFO"
        "images" -> "IMAGE"
        "reviews" -> "REVIEW"
        else -> null
    }
}

internal fun List<StoreDetailSectionModel>.indexOfStoreDetailTarget(target: String): Int =
    indexOfFirst { section ->
        section.type.equals(target, ignoreCase = true) ||
            (target.equals("INFO", ignoreCase = true) && section.type.startsWith("INFO_", ignoreCase = true))
    }

private fun SDClickLogValue?.stringValueOrNull(): String? = when (this) {
    is SDClickLogValue.StringValue -> value
    is SDClickLogValue.IntValue -> value.toString()
    is SDClickLogValue.LongValue -> value.toString()
    is SDClickLogValue.DoubleValue -> value.toString()
    is SDClickLogValue.BoolValue -> value.toString()
    SDClickLogValue.Null, null -> null
}
