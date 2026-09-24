package com.zion830.threedollars.core.ui.sdui.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable

fun SDSurfaceStyleModel?.backgroundColorOr(default: Color): Color = this?.backgroundColor.toColor(fallback = default)

/**
 * 서버 스타일의 배경색·테두리를 [shape]에 맞춰 입힌다. 스타일이 없으면 [defaultBackground]를 쓴다.
 */
fun Modifier.sdSurface(
    style: SDSurfaceStyleModel?,
    shape: Shape = RectangleShape,
    defaultBackground: Color = Color.Transparent
): Modifier {
    val border = style?.border
    val borderWidth = border?.width ?: 0f
    val bordered = if (border != null && borderWidth > 0f) {
        Modifier.border(width = borderWidth.dp, color = border.color.toColor(fallback = Color.Transparent), shape = shape)
    } else {
        Modifier
    }
    return this
        .clip(shape)
        .background(style.backgroundColorOr(defaultBackground), shape)
        .then(bordered)
}

/**
 * 링크·커스텀 액션이 있을 때만 탭을 받는다.
 */
fun Modifier.sdClickable(
    event: SDActionEvent?,
    onAction: (SDActionEvent) -> Unit
): Modifier = if (event != null && (event.link != null || event.customAction != null)) {
    noRippleClickable { onAction(event) }
} else {
    this
}
