package com.zion830.threedollars.core.ui.sdui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * 가게 상세 섹션들이 공유하는 레이아웃 값. 서버가 주지 않는 여백·모서리만 여기 둔다.
 */
object SDSectionDefaults {
    val HorizontalPadding = 20.dp
    val VerticalPadding = 16.dp
    val HeaderContentSpacing = 12.dp
    val CardShape = RoundedCornerShape(20.dp)
    val CardPadding = 16.dp
    val SmallCardShape = RoundedCornerShape(12.dp)
    val ImageShape = RoundedCornerShape(10.dp)
}
