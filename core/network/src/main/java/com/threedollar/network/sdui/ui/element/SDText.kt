package com.threedollar.network.sdui.ui.element

import android.text.TextUtils
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.core.text.HtmlCompat
import com.threedollar.common.compose.utils.toColor
import com.threedollar.network.sdui.model.element.SDTextModel

@Composable
fun SDText(
    model: SDTextModel,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    if (model.isHtml == true) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                TextView(context).apply {
                    includeFontPadding = false
                }
            },
            update = { textView ->
                textView.text = HtmlCompat.fromHtml(model.text.orEmpty(), HtmlCompat.FROM_HTML_MODE_COMPACT)
                textView.setTextColor(
                    try {
                        model.fontColor.orEmpty().toColorInt()
                    } catch (_: Exception) {
                        Color.Black.toArgb()
                    }
                )
                try {
                    if (fontSize.isSp) {
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize.value)
                    }
                } catch (e: Exception) {
                    // ignore
                }
                textView.maxLines = maxLines
                textView.ellipsize = when (overflow) {
                    TextOverflow.Ellipsis -> TextUtils.TruncateAt.END
                    TextOverflow.StartEllipsis -> TextUtils.TruncateAt.START
                    TextOverflow.MiddleEllipsis -> TextUtils.TruncateAt.MIDDLE
                    else -> null
                }
            },
        )
    } else {
        Text(
            text = model.text.orEmpty(),
            modifier = modifier,
            color = model.fontColor.toColor(fallback = Color.Black),
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
}

@Preview(showBackground = true)
@Composable
private fun PreviewSDText() {
    SDText(
        model = SDTextModel(
            text = "안녕하세요, 가나다라마바사",
            isHtml = false,
            fontColor = "#000000"
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSDTextHtml() {
    SDText(
        model = SDTextModel(
            text = "<b>굵은 텍스트</b> 일반 텍스트",
            isHtml = true,
            fontColor = "#FF5733"
        )
    )
}
