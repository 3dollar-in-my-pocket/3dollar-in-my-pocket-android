package com.zion830.threedollars.core.ui.sdui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import base.compose.Gray0
import base.compose.Gray10
import base.compose.dpToSp
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.zion830.threedollars.core.ui.component.compose.components.VerticalSpacer
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.component.SDChipRow
import com.zion830.threedollars.core.ui.sdui.element.SDImage
import com.zion830.threedollars.core.ui.sdui.element.SDText

object SDImagePreviewCardDefaults {
    val DefaultSize = 128.dp
    val Shape = RoundedCornerShape(16.dp)
}

@Composable
fun SDImagePreviewCardLayout(
    modifier: Modifier = Modifier,
    imageSize: Dp = SDImagePreviewCardDefaults.DefaultSize,
    image: @Composable BoxScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .border(width = 1.dp, color = Gray10, shape = SDImagePreviewCardDefaults.Shape)
                .clip(SDImagePreviewCardDefaults.Shape)
                .size(imageSize)
                .background(Gray0),
            contentAlignment = Alignment.Center,
            content = image
        )

        VerticalSpacer(8)

        content()
    }
}

@Composable
fun SDImagePreviewCard(
    model: ImagePreviewCardModel,
    modifier: Modifier = Modifier,
    imageSize: Dp = SDImagePreviewCardDefaults.DefaultSize,
    imageContentScale: ContentScale = ContentScale.Crop,
    onPressed: (ImagePreviewCardModel) -> Unit
) {
    SDImagePreviewCardLayout(
        modifier = modifier
            .wrapContentSize()
            .background(model.style?.backgroundColor.toColor())
            .noRippleClickable { onPressed.invoke(model) },
        imageSize = imageSize,
        image = {
            model.image?.let {
                SDImage(
                    model = it,
                    modifier = Modifier.size(
                        width = it.style?.width?.dp ?: imageSize,
                        height = it.style?.height?.dp ?: imageSize,
                    ),
                    contentScale = imageContentScale
                )
            }
        }
    ) {
        model.title?.let {
            SDText(
                model = it,
                maxLines = 1,
                fontSize = dpToSp(14),
                lineHeight = dpToSp(20),
                fontWeight = FontWeight.SemiBold
            )
        }
        model.metricLabel?.let {
            SDChipRow(it)
        }
        model.contextLabel?.let {
            SDChipRow(it)
        }
    }
}
