package com.threedollar.network.sdui.ui.element

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
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
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val text = remember(model) {
        if (model.isHtml == true) {
            AnnotatedString.fromHtml(model.text.orEmpty())
        } else {
            AnnotatedString(model.text.orEmpty())
        }
    }

    Text(
        text = text,
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
        inlineContent = inlineContent,
        onTextLayout = onTextLayout,
        style = style
    )
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
