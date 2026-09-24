package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray0
import base.compose.Gray100
import base.compose.Gray50
import base.compose.Gray60
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.R as CommonR
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.section.SDStoreVisitSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDHeader
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.element.SDButtonDefaults
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreVisitSectionDefaults {
    val SummaryTopSpacing = 12.dp
    val SummarySpacing = 8.dp
    val SummaryChipHeight = 48.dp
    val SummaryChipPadding = PaddingValues(horizontal = 16.dp)
    val HistoryTopSpacing = 8.dp
    val HistoryShape = RoundedCornerShape(20.dp)
    val HistoryPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    val HistoryItemSpacing = 4.dp
    val MoreTextStartPadding = 12.dp
    val EmptyBannerHeight = 112.dp
    val EmptyBannerPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp)
    val EmptyBannerTextSpacing = 4.dp
}

/**
 * VISIT 섹션: 방문 성공·실패 요약 칩, 방문 인증 내역 박스.
 * 내역이 비어 있으면 서버 값 대신 방문 인증을 권하는 안내 배너를 그린다.
 */
@Composable
fun SDStoreVisitSection(
    model: SDStoreVisitSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val historyItems = model.history?.items.orEmpty()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(horizontal = SDSectionDefaults.HorizontalPadding, vertical = SDSectionDefaults.VerticalPadding)
    ) {
        model.header?.let { SDHeader(model = it, onAction = onAction, modifier = Modifier.fillMaxWidth()) }
        model.summary?.chips?.takeIf { it.isNotEmpty() }?.let { chips ->
            VisitSummary(
                chips = chips,
                modifier = Modifier.padding(top = SDStoreVisitSectionDefaults.SummaryTopSpacing)
            )
        }
        val historyModifier = Modifier.padding(top = SDStoreVisitSectionDefaults.HistoryTopSpacing)
        if (historyItems.isEmpty()) {
            VisitEmptyBanner(modifier = historyModifier)
        } else {
            VisitHistory(history = requireNotNull(model.history), modifier = historyModifier)
        }
    }
}

@Composable
private fun VisitSummary(chips: List<SDChipModel>, modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SDStoreVisitSectionDefaults.SummarySpacing)
    ) {
        chips.forEach { chip ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(SDStoreVisitSectionDefaults.SummaryChipHeight)
                    .sdSurface(style = chip.style, shape = SDButtonDefaults.PillShape)
                    .padding(SDStoreVisitSectionDefaults.SummaryChipPadding),
                contentAlignment = Alignment.CenterStart
            ) {
                SDChip(model = chip, fontSize = dpToSp(14), lineHeight = dpToSp(20))
            }
        }
    }
}

@Composable
private fun VisitHistory(history: SDStoreVisitSectionModel.History, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = history.style, shape = SDStoreVisitSectionDefaults.HistoryShape, defaultBackground = Gray0)
            .padding(SDStoreVisitSectionDefaults.HistoryPadding),
        verticalArrangement = Arrangement.spacedBy(SDStoreVisitSectionDefaults.HistoryItemSpacing)
    ) {
        history.items.orEmpty().forEach { item -> SDChip(model = item, fontSize = dpToSp(12)) }
        history.moreText?.let {
            SDText(
                model = it,
                color = it.fontColor.toColor(fallback = Gray50),
                fontSize = dpToSp(12),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier.padding(start = SDStoreVisitSectionDefaults.MoreTextStartPadding)
            )
        }
    }
}

@Composable
private fun VisitEmptyBanner(modifier: Modifier) {
    val title = stringResource(CommonR.string.store_detail_visit_empty_title)
    val highlight = stringResource(CommonR.string.store_detail_visit_empty_title_highlight)
    val annotatedTitle = remember(title, highlight) { highlightedTitle(title, highlight) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(SDStoreVisitSectionDefaults.EmptyBannerHeight)
            .background(Gray100, SDStoreVisitSectionDefaults.HistoryShape)
            .padding(SDStoreVisitSectionDefaults.EmptyBannerPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SDStoreVisitSectionDefaults.EmptyBannerTextSpacing)
    ) {
        Text(
            text = annotatedTitle,
            color = Gray0,
            fontSize = dpToSp(16),
            fontWeight = FontWeight.Bold,
            fontFamily = PretendardFontFamily,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(CommonR.string.store_detail_visit_empty_description),
            color = Gray60,
            fontSize = dpToSp(12),
            fontWeight = FontWeight.Medium,
            fontFamily = PretendardFontFamily,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun highlightedTitle(title: String, highlight: String): AnnotatedString = buildAnnotatedString {
    append(title)
    val start = title.indexOf(highlight)
    if (highlight.isNotEmpty() && start >= 0) {
        addStyle(SpanStyle(color = Pink), start, start + highlight.length)
    }
}
