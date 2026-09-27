package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray20
import base.compose.Gray30
import base.compose.Gray60
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.R as CommonR
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV1SectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV1SectionModel.RowType
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.component.SDSectionHeader
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreInfoV1SectionDefaults {
    val CardSpacing = 12.dp
    val CardShape = RoundedCornerShape(20.dp)
    val CardInset = 16.dp
    val RowSpacing = 8.dp
    val RowHeight = 24.dp
    val LabelMinWidth = 72.dp
    val LabelValueSpacing = 12.dp
    val CircleChipSize = 24.dp
    val CircleChipSpacing = 2.dp
    val OptionSpacing = 4.dp
    val BulletSize = 4.dp
    val MenuGroupSpacing = 12.dp
    val MenuItemSpacing = 8.dp
    val MenuItemIndent = 36.dp
    val LeaderSpacing = 8.dp
    val MoreButtonHeight = 50.dp
    const val COLLAPSED_MENU_ROW_COUNT = 6
}

/**
 * INFO_V1 섹션: 유저 제보 가게의 정보 카드(가게 형태·출몰 요일·결제 방식 등)와 메뉴 카드.
 * 메뉴 카드는 그룹 머리를 포함해 6줄까지만 보여주고, `메뉴 N개 더보기` 를 탭하면 펼친다.
 */
@Composable
fun SDStoreInfoV1Section(
    model: SDStoreInfoV1SectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(SDSectionDefaults.HeaderContentSpacing)
    ) {
        SDSectionHeader(model = model.header, onAction = onAction)
        Column(
            modifier = Modifier.padding(horizontal = SDSectionDefaults.HorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(SDStoreInfoV1SectionDefaults.CardSpacing)
        ) {
            model.informationCard?.let { InformationCard(it) }
            model.menuCard?.let { MenuCard(card = it, stateKey = model.sectionId) }
        }
    }
}

@Composable
private fun InformationCard(card: SDStoreInfoV1SectionModel.InformationCard) {
    val rows = card.rows.orEmpty().filter { it.type != null }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sdSurface(style = card.style, shape = SDStoreInfoV1SectionDefaults.CardShape)
            .padding(SDStoreInfoV1SectionDefaults.CardInset),
        verticalArrangement = Arrangement.spacedBy(SDStoreInfoV1SectionDefaults.RowSpacing)
    ) {
        rows.forEach { InformationRow(it) }
    }
}

@Composable
private fun InformationRow(row: SDStoreInfoV1SectionModel.Row) {
    val type = row.type ?: return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SDStoreInfoV1SectionDefaults.RowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.widthIn(min = SDStoreInfoV1SectionDefaults.LabelMinWidth)) {
            row.label?.let { SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W700, maxLines = 1) }
        }
        Spacer(modifier = Modifier.width(SDStoreInfoV1SectionDefaults.LabelValueSpacing))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            when (type) {
                RowType.TRAILING_TEXT -> row.value?.let {
                    SDText(
                        model = it,
                        fontSize = dpToSp(12),
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }
                RowType.CHIP_GROUP -> Row(
                    horizontalArrangement = Arrangement.spacedBy(SDStoreInfoV1SectionDefaults.CircleChipSpacing)
                ) {
                    row.chips.orEmpty().forEach { CircleChip(it) }
                }
                RowType.INLINE_OPTION -> Row(
                    horizontalArrangement = Arrangement.spacedBy(SDStoreInfoV1SectionDefaults.OptionSpacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.items.orEmpty().forEach { BulletText(it) }
                }
            }
        }
    }
}

@Composable
private fun CircleChip(chip: SDChipModel) {
    Box(
        modifier = Modifier
            .size(SDStoreInfoV1SectionDefaults.CircleChipSize)
            .sdSurface(style = chip.style, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        chip.text?.let {
            SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W500, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}

/**
 * 선택 여부는 서버가 글자색으로 주므로 불릿 색만 글자색에 맞춘다.
 */
@Composable
private fun BulletText(item: SDStoreInfoV1SectionModel.SelectableText) {
    val text = item.text ?: return
    val bulletColor = text.fontColor.toColor(fallback = colorResource(DesignSystemR.color.color_B7B7B7))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(SDStoreInfoV1SectionDefaults.OptionSpacing))
        Box(
            modifier = Modifier
                .size(SDStoreInfoV1SectionDefaults.BulletSize)
                .background(color = bulletColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(SDStoreInfoV1SectionDefaults.OptionSpacing))
        SDText(model = text, fontSize = dpToSp(12), fontWeight = FontWeight.W500, maxLines = 1)
    }
}

private data class VisibleMenuGroup(
    val header: SDChipModel?,
    val items: List<SDStoreInfoV1SectionModel.MenuItem>,
)

/**
 * 그룹 머리와 메뉴를 한 줄씩 세어 [rowLimit] 줄까지만 남긴다. 머리 뒤에 메뉴가 한 줄도 못 오면 그 그룹은 뺀다.
 */
private fun List<SDStoreInfoV1SectionModel.MenuGroup>.limitRows(rowLimit: Int): List<VisibleMenuGroup> {
    var remaining = rowLimit
    val visible = mutableListOf<VisibleMenuGroup>()
    for (group in this) {
        if (remaining < 2) break
        remaining -= 1
        val items = group.items.orEmpty().take(remaining)
        remaining -= items.size
        visible += VisibleMenuGroup(header = group.header, items = items)
    }
    return visible
}

@Composable
private fun MenuCard(card: SDStoreInfoV1SectionModel.MenuCard, stateKey: String?) {
    var expanded by rememberSaveable(stateKey) { mutableStateOf(false) }
    val groups = card.groups.orEmpty()
    val totalRowCount = groups.sumOf { 1 + it.items.orEmpty().size }
    val totalItemCount = groups.sumOf { it.items.orEmpty().size }
    val collapsed = !expanded && totalRowCount > SDStoreInfoV1SectionDefaults.COLLAPSED_MENU_ROW_COUNT
    val visibleGroups = groups.limitRows(if (collapsed) SDStoreInfoV1SectionDefaults.COLLAPSED_MENU_ROW_COUNT else totalRowCount)
    val hiddenItemCount = totalItemCount - visibleGroups.sumOf { it.items.size }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sdSurface(style = card.style, shape = SDStoreInfoV1SectionDefaults.CardShape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SDStoreInfoV1SectionDefaults.CardInset),
            verticalArrangement = Arrangement.spacedBy(SDStoreInfoV1SectionDefaults.MenuGroupSpacing)
        ) {
            visibleGroups.forEach { group ->
                Column(verticalArrangement = Arrangement.spacedBy(SDStoreInfoV1SectionDefaults.MenuItemSpacing)) {
                    group.header?.let { SDChip(model = it, fontSize = dpToSp(14), lineHeight = dpToSp(20)) }
                    group.items.forEach { MenuItemRow(it) }
                }
            }
        }
        if (collapsed) {
            SDStoreMenuMoreButton(hiddenItemCount = hiddenItemCount, onClick = { expanded = true })
        }
    }
}

/**
 * 메뉴명 · 점선 · 가격 한 줄. 가격을 먼저 재고 남은 너비를 메뉴명과 점선이 나눠 쓴다.
 */
@Composable
private fun MenuItemRow(item: SDStoreInfoV1SectionModel.MenuItem) {
    Layout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = SDStoreInfoV1SectionDefaults.MenuItemIndent),
        content = {
            Box { item.primaryText?.let { SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W500, maxLines = 1) } }
            DashedLeader()
            Box { item.secondaryText?.let { SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W500, maxLines = 1) } }
        }
    ) { measurables, constraints ->
        val spacing = SDStoreInfoV1SectionDefaults.LeaderSpacing.roundToPx()
        val width = constraints.maxWidth
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val secondary = measurables[2].measure(loose)
        val primary = measurables[0].measure(loose.copy(maxWidth = (width - secondary.width - spacing * 2).coerceAtLeast(0)))
        val lineWidth = (width - primary.width - secondary.width - spacing * 2).coerceAtLeast(0)
        val line = measurables[1].measure(Constraints.fixed(lineWidth, 1.dp.roundToPx()))
        val height = maxOf(primary.height, secondary.height)
        layout(width, height) {
            primary.placeRelative(0, (height - primary.height) / 2)
            line.placeRelative(primary.width + spacing, (height - line.height) / 2)
            secondary.placeRelative(width - secondary.width, (height - secondary.height) / 2)
        }
    }
}

@Composable
private fun DashedLeader() {
    Canvas(modifier = Modifier) {
        drawLine(
            color = Gray30,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 2.dp.toPx()))
        )
    }
}

/**
 * 접힌 메뉴 카드 아래의 구분선과 `메뉴 N개 더보기` 버튼. INFO_V1·INFO_V2 메뉴 카드가 같이 쓴다.
 */
@Composable
internal fun SDStoreMenuMoreButton(hiddenItemCount: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SDStoreInfoV1SectionDefaults.CardInset)
            .height(1.dp)
            .background(Gray20)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SDStoreInfoV1SectionDefaults.MoreButtonHeight)
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(CommonR.string.store_detail_menu_more, hiddenItemCount),
            color = Gray60,
            fontSize = dpToSp(12),
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily
        )
    }
}
