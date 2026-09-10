package com.zion830.threedollars.ui.storeDetail.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray100
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.analytics.SDClickLogger
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDClickLogModel
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
    onViewLog: (SDViewLogModel) -> Unit = {},
    onImpression: (String, SDImpressionLogModel) -> Unit = { _, _ -> },
    onClickLog: (SDClickLogModel) -> Unit = { runCatching { SDClickLogger.send(it) } },
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val coroutineScope = rememberCoroutineScope()
    val adStates = rememberStoreDetailAdMobStates(screen.sections, onImpression, onClickLog)

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

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize().background(ColorWhite),
    ) {
        itemsIndexed(
            items = screen.sections,
            key = { index, section -> "${section.type}-$index" },
            contentType = { _, section -> section.type },
        ) { _, section ->
            when (section) {
                is StoreDetailSectionModel.Callout -> StoreDetailCalloutSection(section)
                is StoreDetailSectionModel.Margin -> Spacer(Modifier.fillMaxWidth().height(section.height.coerceAtLeast(0).dp))
                is StoreDetailSectionModel.Preview -> StoreDetailPreviewSection(section, onAction)
                is StoreDetailSectionModel.AdMob -> StoreDetailAdMobSection(
                    section = section,
                    adStates = adStates,
                )
                is StoreDetailSectionModel.Tab -> StoreDetailTabSection(
                    section,
                    isActionEnabled = { action ->
                        action.targetSectionTypeOrNull()?.let { screen.sections.indexOfStoreDetailTarget(it) >= 0 } ?: true
                    },
                ) { actionBar ->
                    handleStoreDetailTabClick(
                        actionBar = actionBar,
                        sections = screen.sections,
                        onScrollToSection = { index -> coroutineScope.launch { listState.animateScrollToItem(index) } },
                        onAction = onAction,
                        onClickLog = onClickLog,
                    )
                }
                is StoreDetailSectionModel.Map -> StoreDetailMapSection(section, onAction)
                is StoreDetailSectionModel.Edit -> StoreDetailEditSection(section, onAction)
                is StoreDetailSectionModel.Coupon -> StoreDetailCouponSection(section, onAction)
                is StoreDetailSectionModel.Visit -> StoreDetailVisitSection(section, onAction)
                is StoreDetailSectionModel.Post -> StoreDetailPostSection(section, onAction)
                is StoreDetailSectionModel.Image -> StoreDetailImageSection(section, onAction)
                is StoreDetailSectionModel.AppearanceDay -> StoreDetailAppearanceDaySection(section, onAction)
                is StoreDetailSectionModel.RelatedStores -> StoreDetailRelatedStoresSection(section, onAction)
                is StoreDetailSectionModel.Cta -> StoreDetailCtaSection(section, onAction)
                is StoreDetailSectionModel.Review -> StoreDetailReviewSection(section, onAction)
                is StoreDetailSectionModel.InfoV1 -> StoreDetailInfoV1Section(section, onAction)
                is StoreDetailSectionModel.InfoV2 -> StoreDetailInfoV2Section(section, onAction)
            }
        }
    }
}

internal fun handleStoreDetailTabClick(
    actionBar: StoreActionBarModel,
    sections: List<StoreDetailSectionModel>,
    onScrollToSection: (Int) -> Unit,
    onAction: (StoreActionBarModel) -> Unit,
    onClickLog: (SDClickLogModel) -> Unit,
) {
    val targetType = actionBar.targetSectionTypeOrNull()
    val targetIndex = targetType?.let(sections::indexOfStoreDetailTarget) ?: -1
    if (targetType != null && targetIndex < 0) return
    if (targetIndex >= 0) {
        (actionBar.clickLog ?: actionBar.button.clickLog)?.let(onClickLog)
        onScrollToSection(targetIndex)
    } else {
        onAction(actionBar)
    }
}

@Composable
internal fun StoreDetailSectionHeader(
    title: SDTextModel,
    subTitle: SDTextModel? = null,
    trailingButton: SDButtonModel? = null,
    onAction: ((StoreActionBarModel) -> Unit)? = null,
    modifier: Modifier = Modifier,
    alignActionWithTitle: Boolean = false,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val titleAlignment = if (alignActionWithTitle) Modifier.alignBy(FirstBaseline) else Modifier
        Column(modifier = Modifier.weight(1f).then(titleAlignment)) {
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
            Box(modifier = titleAlignment) {
                StoreDetailTextButton(
                    button = trailingButton,
                    onClick = { onAction(StoreActionBarModel(type = "HEADER_TRAILING", button = trailingButton)) },
                )
            }
        }
    }
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
        "post" -> "POST"
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
