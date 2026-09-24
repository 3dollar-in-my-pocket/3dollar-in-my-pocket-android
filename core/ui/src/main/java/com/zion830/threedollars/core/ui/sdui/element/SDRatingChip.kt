package com.zion830.threedollars.core.ui.sdui.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.threedollar.common.sdui.model.element.SDRatingChipModel

/**
 * 별점. 채운 별·빈 별 이미지를 서버가 개수만큼 내려준다.
 */
@Composable
fun SDRatingChip(
    model: SDRatingChipModel,
    modifier: Modifier = Modifier,
    spacing: Dp = 2.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        model.images.orEmpty().forEach { SDImage(it) }
    }
}
