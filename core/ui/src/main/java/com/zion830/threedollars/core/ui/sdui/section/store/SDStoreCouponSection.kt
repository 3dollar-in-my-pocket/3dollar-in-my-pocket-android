package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray90
import base.compose.Pink100
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.section.SDStoreCouponSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDHeader
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDButtonDefaults
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreCouponSectionDefaults {
    val CardSpacing = 12.dp
    val CardMinHeight = 80.dp
    val CardCornerRadius = 8.dp
    val NotchRadius = 12.dp
    val BadgeHeight = 26.dp
    val BadgeOverlap = 18.dp
    val BadgeStartPadding = 12.dp
    val BadgePadding = PaddingValues(horizontal = 8.dp)
    val TextStartPadding = 18.dp
    val TextVerticalPadding = 12.dp
    val TextSpacing = 4.dp
    val DividerSpacing = 16.dp
    val DividerVerticalPadding = 12.dp
    val DividerWidth = 1.dp
    val TrailingEndPadding = 25.dp
    val DividerColor = Color(0xFFBC4BD6)
}

/**
 * COUPON 섹션: 머리(내 쿠폰함 가기)와 쿠폰 카드 목록.
 * 카드 오른쪽 영역을 탭하면 `trailingButton` 의 쿠폰 발급·사용 액션을 올린다.
 */
@Composable
fun SDStoreCouponSection(
    model: SDStoreCouponSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(horizontal = SDSectionDefaults.HorizontalPadding, vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(SDSectionDefaults.HeaderContentSpacing)
    ) {
        model.header?.let { SDHeader(model = it, onAction = onAction, modifier = Modifier.fillMaxWidth()) }
        val cards = model.cards.orEmpty()
        if (cards.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(SDStoreCouponSectionDefaults.CardSpacing)) {
                cards.forEach { card -> CouponCard(card = card, onAction = onAction) }
            }
        }
    }
}

@Composable
private fun CouponCard(
    card: SDStoreCouponSectionModel.Card,
    onAction: (SDActionEvent) -> Unit
) {
    val badge = card.badge?.takeIf { it.text != null }
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(top = if (badge != null) SDStoreCouponSectionDefaults.BadgeOverlap else 0.dp)
                .fillMaxWidth()
                .heightIn(min = SDStoreCouponSectionDefaults.CardMinHeight)
                .height(IntrinsicSize.Min)
                .sdSurface(style = card.style, shape = CouponTicketShape, defaultBackground = Pink100),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CouponTexts(card = card, modifier = Modifier.weight(1f))
            CouponDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = SDStoreCouponSectionDefaults.DividerVerticalPadding)
            )
            card.trailingButton?.let { button ->
                SDButton(
                    model = button.copy(style = null),
                    onAction = onAction,
                    fallbackLog = card.clickLog,
                    shape = RectangleShape,
                    contentPadding = PaddingValues(
                        start = SDStoreCouponSectionDefaults.DividerSpacing,
                        end = SDStoreCouponSectionDefaults.TrailingEndPadding
                    ),
                    fontSize = dpToSp(12),
                    modifier = Modifier.fillMaxHeight()
                )
            } ?: Box(modifier = Modifier.width(SDStoreCouponSectionDefaults.TrailingEndPadding))
        }
        badge?.let {
            CouponBadge(
                badge = it,
                modifier = Modifier.padding(start = SDStoreCouponSectionDefaults.BadgeStartPadding)
            )
        }
    }
}

@Composable
private fun CouponTexts(card: SDStoreCouponSectionModel.Card, modifier: Modifier) {
    Column(
        modifier = modifier.padding(
            start = SDStoreCouponSectionDefaults.TextStartPadding,
            end = SDStoreCouponSectionDefaults.DividerSpacing,
            top = SDStoreCouponSectionDefaults.TextVerticalPadding,
            bottom = SDStoreCouponSectionDefaults.TextVerticalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(SDStoreCouponSectionDefaults.TextSpacing)
    ) {
        card.title?.let {
            SDText(
                model = it,
                fontSize = dpToSp(16),
                fontWeight = FontWeight.Bold,
                lineHeight = dpToSp(24),
                maxLines = 2
            )
        }
        card.subTitle?.let {
            SDText(
                model = it,
                fontSize = dpToSp(14),
                fontWeight = FontWeight.Normal,
                lineHeight = dpToSp(20),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CouponDivider(modifier: Modifier) {
    Canvas(modifier = modifier.width(SDStoreCouponSectionDefaults.DividerWidth)) {
        drawLine(
            color = SDStoreCouponSectionDefaults.DividerColor,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = size.width,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.dp.toPx(), 3.dp.toPx()))
        )
    }
}

@Composable
private fun CouponBadge(badge: SDChipModel, modifier: Modifier) {
    SDChip(
        model = badge,
        fontSize = dpToSp(12),
        modifier = modifier
            .height(SDStoreCouponSectionDefaults.BadgeHeight)
            .sdSurface(style = badge.style, shape = SDButtonDefaults.PillShape, defaultBackground = Gray90)
            .padding(SDStoreCouponSectionDefaults.BadgePadding)
    )
}

/**
 * 오른쪽 가운데가 반원으로 파인 쿠폰 모양.
 */
private object CouponTicketShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val corner = with(density) { SDStoreCouponSectionDefaults.CardCornerRadius.toPx() }
        val notch = with(density) { SDStoreCouponSectionDefaults.NotchRadius.toPx() }
        val card = Path().apply {
            addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(corner)))
        }
        val cutout = Path().apply {
            addOval(
                Rect(
                    center = Offset(size.width, size.height / 2),
                    radius = notch
                )
            )
        }
        return Outline.Generic(Path.combine(PathOperation.Difference, card, cutout))
    }
}
