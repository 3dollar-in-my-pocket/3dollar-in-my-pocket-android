package com.zion830.threedollars.core.ui.sdui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import base.compose.Gray50
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.element.SDImage

@Composable
fun SDChipRow(
    chips: List<SDChipModel>,
    modifier: Modifier = Modifier.wrapContentSize(),
    space: Dp = 4.dp,
    fontSize: TextUnit = dpToSp(12),
    divider: @Composable (() -> Unit) = { SDChipRowDefaultDivider() }
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space)
    ) {
        chips.forEachIndexed { index, chip ->
            SDChip(model = chip, fontSize = fontSize)
            if (index < chips.lastIndex) divider()
        }
    }
}

/**
 * 서버가 구분자 이미지를 주면 그 이미지를, 없으면 기본 세로선을 쓴다.
 */
@Composable
fun SDChipRowSeparator(separator: SDImageModel?) {
    if (separator != null) SDImage(separator) else SDChipRowDefaultDivider()
}

@Composable
internal fun SDChipRowDefaultDivider() {
    Box(
        modifier = Modifier
            .height(8.dp)
            .width(1.dp)
            .background(Gray50)
    )
}
