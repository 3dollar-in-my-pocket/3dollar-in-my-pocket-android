package com.zion830.threedollars.core.ui.sdui.element

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import base.compose.PretendardFontFamily
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.text.SDHtmlText

/**
 * 서버 텍스트를 그린다. `isHtml` 이면 span 의 font-size·font-weight·color 를 구간별로 적용하고,
 * span 에 값이 없는 구간은 파라미터로 받은 기본값을 쓴다. 서버 px 은 dp 로 취급해 글자 크기 설정의 영향을 받지 않는다.
 */
@Composable
fun SDText(
    model: SDTextModel,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = PretendardFontFamily,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    color: Color = model.fontColor.toColor(fallback = Color.Black),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val density = LocalDensity.current
    val text = remember(model.text, model.isHtml, density.fontScale) { model.toAnnotatedString(density) }
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}

internal fun SDTextModel.toAnnotatedString(density: Density): AnnotatedString {
    if (isHtml != true) return AnnotatedString(text.orEmpty())
    return buildAnnotatedString {
        SDHtmlText.parse(text).forEach { run ->
            pushStyle(
                SpanStyle(
                    color = run.color.toColor(fallback = Color.Unspecified),
                    fontSize = run.fontSize?.let { (it / density.fontScale).sp } ?: TextUnit.Unspecified,
                    fontWeight = run.fontWeight?.let { FontWeight(it.coerceIn(1, 1000)) }
                )
            )
            append(run.text)
            pop()
        }
    }
}
