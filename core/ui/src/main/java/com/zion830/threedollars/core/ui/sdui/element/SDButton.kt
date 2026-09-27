package com.zion830.threedollars.core.ui.sdui.element

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import base.compose.dpToSp
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDButtonModel
import com.threedollar.common.sdui.model.element.SDImageAlignment
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDToggleActionModel
import com.threedollar.common.sdui.model.element.toActionEvent
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDButtonDefaults {
    val Shape = RoundedCornerShape(8.dp)
    val PillShape = RoundedCornerShape(percent = 50)
    val NoPadding = PaddingValues(0.dp)
    val ImageSpacing = 4.dp
    val CenteredLineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    )
}

/**
 * 서버 버튼. 모양·여백은 쓰는 자리마다 다르므로 호출부가 정하고, 색·글자·아이콘은 서버 값을 쓴다.
 * 탭하면 링크·커스텀 액션·클릭 로그를 담은 [SDActionEvent]를 올린다.
 *
 * @param lineHeight 높이가 고정된 버튼이면 지정한다. 비우면 테마 기본 줄 높이(bodyLarge 24sp)를 물려받아
 * 글자가 버튼 안에서 위로 치우친다.
 */
@Composable
fun SDButton(
    model: SDButtonModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = SDButtonDefaults.Shape,
    contentPadding: PaddingValues = SDButtonDefaults.NoPadding,
    imageSpacing: Dp = SDButtonDefaults.ImageSpacing,
    fontSize: TextUnit = dpToSp(14),
    fontWeight: FontWeight = FontWeight.SemiBold,
    lineHeight: TextUnit = TextUnit.Unspecified,
    maxLines: Int = 1,
    textAlign: TextAlign? = null,
    fallbackLog: SDLogModel? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) {
    Row(
        modifier = modifier
            .sdSurface(style = model.style, shape = shape)
            .noRippleClickable { onAction(model.toActionEvent(fallbackLog)) }
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(imageSpacing, horizontalAlignment),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val image = @Composable { model.image?.let { SDImage(it) } }
        if (model.imageAlignment != SDImageAlignment.END) image()
        model.text?.let {
            SDText(
                model = it,
                fontSize = fontSize,
                fontWeight = fontWeight,
                lineHeight = lineHeight,
                maxLines = maxLines,
                textAlign = textAlign,
                style = LocalTextStyle.current.copy(lineHeightStyle = SDButtonDefaults.CenteredLineHeightStyle)
            )
        }
        if (model.imageAlignment == SDImageAlignment.END) image()
    }
}

/**
 * 좋아요처럼 선택 상태에 따라 서버가 준 두 버튼 중 하나를 그린다.
 */
@Composable
fun SDToggleButton(
    model: SDToggleActionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = SDButtonDefaults.Shape,
    contentPadding: PaddingValues = SDButtonDefaults.NoPadding,
    fontSize: TextUnit = dpToSp(12),
) {
    val button = model.current ?: return
    SDButton(
        model = button,
        onAction = onAction,
        modifier = modifier,
        shape = shape,
        contentPadding = contentPadding,
        fontSize = fontSize,
        fontWeight = FontWeight.Medium
    )
}
