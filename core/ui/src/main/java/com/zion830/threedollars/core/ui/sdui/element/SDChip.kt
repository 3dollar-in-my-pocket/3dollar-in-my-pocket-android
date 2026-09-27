package com.zion830.threedollars.core.ui.sdui.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageAlignment

private const val DEFAULT_CONTENT_SPACING = 2f

/**
 * 이미지 + 텍스트 + 보조 텍스트 한 줄. 이미지 위치(`imageAlignment`)와 간격(`contentSpacing`)은 서버 값을 따른다.
 * 배경이 필요한 곳은 호출부가 [modifier]로 입힌다.
 */
@Composable
fun SDChip(
    model: SDChipModel,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = dpToSp(12),
    lineHeight: TextUnit = dpToSp(18),
    maxLines: Int = 1
) {
    val spacing = (model.contentSpacing ?: DEFAULT_CONTENT_SPACING).dp
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val image = @Composable { model.image?.let { SDImage(it) } }
        if (model.imageAlignment != SDImageAlignment.END) image()
        model.text?.let {
            SDText(model = it, fontSize = fontSize, lineHeight = lineHeight, maxLines = maxLines)
        }
        model.additionalText?.let {
            SDText(model = it, fontSize = fontSize, lineHeight = lineHeight, maxLines = maxLines)
        }
        if (model.imageAlignment == SDImageAlignment.END) image()
    }
}
