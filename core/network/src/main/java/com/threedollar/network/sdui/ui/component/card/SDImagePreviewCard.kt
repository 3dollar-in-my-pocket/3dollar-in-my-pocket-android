package com.threedollar.network.sdui.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import base.compose.Gray0
import base.compose.Gray10
import base.compose.dpToSp
import com.threedollar.common.compose.utils.toColor
import com.threedollar.network.sdui.model.component.ImagePreviewCardModel
import com.threedollar.network.sdui.model.element.SDImageModel
import com.threedollar.network.sdui.model.element.SDTextModel
import com.threedollar.network.sdui.ui.component.SDChipRow
import com.threedollar.network.sdui.ui.element.SDImage
import com.threedollar.network.sdui.ui.element.SDText
import com.zion830.threedollars.core.ui.component.compose.components.VerticalSpacer
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable

object SDImagePreviewCardDefaults {
    val DefaultSize = 128.dp
    val Shape = RoundedCornerShape(16.dp)
}

@Composable
internal fun SDImagePreviewCard(
    model: ImagePreviewCardModel,
    onPressed: (ImagePreviewCardModel) -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(model.style?.backgroundColor.toColor())
            .noRippleClickable { onPressed.invoke(model) },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .border(width = 1.dp, color = Gray10, shape = SDImagePreviewCardDefaults.Shape)
                .clip(SDImagePreviewCardDefaults.Shape)
                .size(SDImagePreviewCardDefaults.DefaultSize)
                .background(Gray0),
            contentAlignment = Alignment.Center
        ) {
            model.image?.let {
                SDImage(
                    model = it,
                    modifier = Modifier.size(
                        width = it.style?.width?.dp ?: SDImagePreviewCardDefaults.DefaultSize,
                        height = it.style?.height?.dp ?: SDImagePreviewCardDefaults.DefaultSize,
                    ),
                    contentScale = ContentScale.Crop
                )
            }
        }

        VerticalSpacer(8)

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

@Preview(showBackground = true)
@Composable
private fun PreviewSDCard() {
    SDImagePreviewCard(
        model = ImagePreviewCardModel(
            cardId = "preview_card_1",
            image = SDImageModel(
                url = "https://via.placeholder.com/200x120",
                style = SDImageModel.Style(width = 200f, height = 120f)
            ),
            title = SDTextModel(
                text = "카드 타이틀",
                isHtml = false,
                fontColor = "#000000"
            ),
            metricLabel = null,
            contextLabel = null,
            link = null,
            style = null,
            refs = null
        )
    ) {

    }
}