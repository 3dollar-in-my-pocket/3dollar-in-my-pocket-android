package com.zion830.threedollars.core.ui.sdui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.Gray0
import base.compose.Gray10
import base.compose.dpToSp
import com.threedollar.common.sdui.model.component.SDAdCardModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.element.SDImage
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDAdCardDefaults {
    val ListHorizontalPadding = 20.dp
    val ListVerticalPadding = 16.dp
    val ListImageHeight = 120.dp
    val ListImageShape = RoundedCornerShape(8.dp)
    val CurationImageShape = RoundedCornerShape(16.dp)
    val BannerShape = RoundedCornerShape(16.dp)
    val BannerPadding = 12.dp
    val BannerImageSize = 64.dp
    val BannerImageShape = RoundedCornerShape(12.dp)
    val BannerTrailingIconSize = 24.dp
    val LabelShape = RoundedCornerShape(4.dp)
    val LabelHorizontalPadding = 4.dp
    val LabelVerticalPadding = 2.dp
    val ContentSpacing = 4.dp
    val ImageSpacing = 12.dp
    val DividerWidth = 1.dp
}

@Composable
fun SDAdListCard(
    model: SDAdCardModel,
    onClick: (SDAdCardModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable { onClick(model) }
            .drawBehind {
                val stroke = SDAdCardDefaults.DividerWidth.toPx()
                val y = size.height - stroke / 2
                drawLine(Gray10, Offset(0f, y), Offset(size.width, y), strokeWidth = stroke)
            }
            .padding(
                horizontal = SDAdCardDefaults.ListHorizontalPadding,
                vertical = SDAdCardDefaults.ListVerticalPadding,
            ),
    ) {
        val labels = model.labels.orEmpty()
        if (model.title != null || labels.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SDAdCardDefaults.ContentSpacing),
            ) {
                model.title?.let { AdTitle(it, fontSizeDp = 16, lineHeightDp = 24, modifier = Modifier.weight(1f, fill = false)) }
                SDAdLabels(labels)
            }
        }
        model.subTitle?.let { AdSubTitle(it) }
        model.image?.let { image ->
            Spacer(modifier = Modifier.height(SDAdCardDefaults.ImageSpacing))
            SDImage(
                model = image,
                sizeFromStyle = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SDAdCardDefaults.ListImageHeight)
                    .clip(SDAdCardDefaults.ListImageShape),
            )
        }
    }
}

@Composable
fun SDAdCurationCard(
    model: SDAdCardModel,
    imageSize: Dp,
    onClick: (SDAdCardModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(imageSize)
            .noRippleClickable { onClick(model) },
        verticalArrangement = Arrangement.spacedBy(SDAdCardDefaults.ContentSpacing),
    ) {
        model.image?.let { image ->
            Box(
                modifier = Modifier
                    .size(imageSize)
                    .border(width = SDAdCardDefaults.DividerWidth, color = Gray10, shape = SDAdCardDefaults.CurationImageShape)
                    .clip(SDAdCardDefaults.CurationImageShape)
                    .background(Gray0),
            ) {
                SDImage(model = image, sizeFromStyle = false, modifier = Modifier.size(imageSize))
            }
        }
        model.title?.let { AdTitle(it, fontSizeDp = 14, lineHeightDp = 20) }
        model.subTitle?.let { AdSubTitle(it) }
        SDAdLabels(model.labels.orEmpty())
    }
}

@Composable
fun SDAdBannerCard(
    model: SDAdCardModel,
    onClick: (SDAdCardModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(SDAdCardDefaults.BannerShape)
            .background(Gray0)
            .border(width = SDAdCardDefaults.DividerWidth, color = Gray10, shape = SDAdCardDefaults.BannerShape)
            .noRippleClickable { onClick(model) }
            .padding(SDAdCardDefaults.BannerPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SDAdCardDefaults.ImageSpacing),
    ) {
        model.image?.let { image ->
            SDImage(
                model = image,
                sizeFromStyle = false,
                modifier = Modifier
                    .size(SDAdCardDefaults.BannerImageSize)
                    .clip(SDAdCardDefaults.BannerImageShape),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SDAdCardDefaults.ContentSpacing),
        ) {
            SDAdLabels(model.labels.orEmpty())
            model.title?.let { AdTitle(it, fontSizeDp = 16, lineHeightDp = 24) }
            model.subTitle?.let { AdSubTitle(it) }
        }
        model.trailingIcon?.let { icon ->
            SDImage(
                model = icon,
                sizeFromStyle = false,
                modifier = Modifier.size(SDAdCardDefaults.BannerTrailingIconSize),
            )
        }
    }
}

@Composable
fun SDAdLabels(labels: List<SDChipModel>, modifier: Modifier = Modifier) {
    if (labels.isEmpty()) return
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SDAdCardDefaults.ContentSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        labels.forEach { label ->
            SDChip(
                model = label,
                fontSize = dpToSp(10),
                lineHeight = dpToSp(14),
                modifier = Modifier
                    .sdSurface(style = label.style, shape = SDAdCardDefaults.LabelShape)
                    .padding(
                        horizontal = SDAdCardDefaults.LabelHorizontalPadding,
                        vertical = SDAdCardDefaults.LabelVerticalPadding,
                    ),
            )
        }
    }
}

@Composable
private fun AdTitle(
    model: SDTextModel,
    fontSizeDp: Int,
    lineHeightDp: Int,
    modifier: Modifier = Modifier,
) {
    SDText(
        model = model,
        modifier = modifier,
        fontSize = dpToSp(fontSizeDp),
        lineHeight = dpToSp(lineHeightDp),
        fontWeight = FontWeight.Bold,
        maxLines = 1,
    )
}

@Composable
private fun AdSubTitle(model: SDTextModel) {
    SDText(
        model = model,
        fontSize = dpToSp(12),
        lineHeight = dpToSp(18),
        fontWeight = FontWeight.Medium,
        maxLines = 1,
    )
}
