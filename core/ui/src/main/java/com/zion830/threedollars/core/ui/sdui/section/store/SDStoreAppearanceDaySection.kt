package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray0
import base.compose.Gray30
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.section.SDStoreAppearanceDaySectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDHeader
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreAppearanceDaySectionDefaults {
    val RowSpacing = 12.dp
    val DividerHeight = 1.dp
    val LeadingWidth = 48.dp
    val ColumnSpacing = 12.dp
    val TextSpacing = 2.dp
}

/**
 * APPEARANCE_DAY 섹션: 요일별 영업 시간표. 카드 색은 첫 행의 스타일을 쓰고, 행 사이에 구분선을 둔다.
 */
@Composable
fun SDStoreAppearanceDaySection(
    model: SDStoreAppearanceDaySectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = model.items.orEmpty()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(horizontal = SDSectionDefaults.HorizontalPadding, vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(SDSectionDefaults.HeaderContentSpacing)
    ) {
        model.header?.let { SDHeader(model = it, onAction = onAction, modifier = Modifier.fillMaxWidth()) }
        if (items.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .sdSurface(
                        style = items.first().style,
                        shape = SDSectionDefaults.CardShape,
                        defaultBackground = Gray0
                    )
                    .padding(SDSectionDefaults.CardPadding),
                verticalArrangement = Arrangement.spacedBy(SDStoreAppearanceDaySectionDefaults.RowSpacing)
            ) {
                items.forEachIndexed { index, item ->
                    if (index > 0) AppearanceDayDivider()
                    AppearanceDayRow(item)
                }
            }
        }
    }
}

@Composable
private fun AppearanceDayRow(item: SDStoreAppearanceDaySectionModel.Item) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SDStoreAppearanceDaySectionDefaults.ColumnSpacing),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.width(SDStoreAppearanceDaySectionDefaults.LeadingWidth)) {
            item.leadingText?.let {
                SDText(
                    model = it,
                    fontSize = dpToSp(14),
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = dpToSp(20),
                    maxLines = 1
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(SDStoreAppearanceDaySectionDefaults.TextSpacing)
        ) {
            item.primaryText?.let {
                SDText(
                    model = it,
                    fontSize = dpToSp(14),
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = dpToSp(20),
                    textAlign = TextAlign.End
                )
            }
            item.secondaryText?.let {
                SDText(
                    model = it,
                    fontSize = dpToSp(12),
                    fontWeight = FontWeight.Medium,
                    lineHeight = dpToSp(18),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun AppearanceDayDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SDStoreAppearanceDaySectionDefaults.DividerHeight)
            .background(Gray30)
    )
}
