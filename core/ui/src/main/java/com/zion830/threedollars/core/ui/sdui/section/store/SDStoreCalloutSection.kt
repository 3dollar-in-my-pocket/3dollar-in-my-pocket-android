package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray90
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.section.SDStoreCalloutSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDImage
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreCalloutSectionDefaults {
    val TopPadding = 12.dp
    val CardShape = RoundedCornerShape(12.dp)
    val CardPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    val CardMinHeight = 41.dp
    val IconSize = 21.dp
    val IconSpacing = 5.dp
    val ContentSpacing = 4.dp
}

/**
 * CALLOUT 섹션: 공식 인증·직영점 같은 안내 카드.
 * 서버는 `image`+`text` 형태를 주지만 `title`·`subTitle`·`footerLeftButton` 형태도 같은 카드에 그린다.
 * 카드 색은 `content.style`, 없으면 섹션 스타일을 쓴다.
 */
@Composable
fun SDStoreCalloutSection(
    model: SDStoreCalloutSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val content = model.content ?: return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(
                start = SDSectionDefaults.HorizontalPadding,
                end = SDSectionDefaults.HorizontalPadding,
                top = SDStoreCalloutSectionDefaults.TopPadding
            )
    ) {
        CalloutCard(content = content, fallbackStyle = model.style, onAction = onAction)
    }
}

@Composable
private fun CalloutCard(
    content: SDStoreCalloutSectionModel.Content,
    fallbackStyle: SDSurfaceStyleModel?,
    onAction: (SDActionEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = SDStoreCalloutSectionDefaults.CardMinHeight)
            .sdSurface(
                style = content.style ?: fallbackStyle,
                shape = SDStoreCalloutSectionDefaults.CardShape,
                defaultBackground = Gray90
            )
            .padding(SDStoreCalloutSectionDefaults.CardPadding),
        verticalArrangement = Arrangement.spacedBy(SDStoreCalloutSectionDefaults.ContentSpacing, Alignment.CenterVertically)
    ) {
        val title = content.text ?: content.title
        if (content.image != null || title != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SDStoreCalloutSectionDefaults.IconSpacing)
            ) {
                content.image?.let {
                    SDImage(
                        model = it,
                        contentScale = ContentScale.Fit,
                        sizeFromStyle = false,
                        modifier = Modifier.size(SDStoreCalloutSectionDefaults.IconSize)
                    )
                }
                title?.let { SDText(model = it, fontSize = dpToSp(14), fontWeight = FontWeight.Normal) }
            }
        }
        content.subTitle?.let { SDText(model = it, fontSize = dpToSp(13), fontWeight = FontWeight.Normal) }
        content.footerLeftButton?.let { button ->
            SDButton(model = button, onAction = onAction)
        }
    }
}
