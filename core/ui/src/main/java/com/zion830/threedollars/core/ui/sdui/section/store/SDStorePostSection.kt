package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray95
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.R as CommonR
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.section.SDStorePostSectionModel
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.component.SDSectionHeader
import com.zion830.threedollars.core.ui.sdui.element.SDImage
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.element.SDToggleButton
import com.zion830.threedollars.core.ui.sdui.element.toAnnotatedString
import com.zion830.threedollars.core.ui.sdui.foundation.sdClickable
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStorePostSectionDefaults {
    val CardSpacing = 12.dp
    val CardShape = RoundedCornerShape(16.dp)
    val CardInset = 16.dp
    val ContentSpacing = 12.dp
    val HeaderIconSize = 40.dp
    val HeaderSpacing = 8.dp
    val ImageHeight = 208.dp
    val ImageSpacing = 12.dp
    val ImageShape = RoundedCornerShape(8.dp)
    const val BODY_MAX_LINES = 6
    const val BODY_SHORTENING = " ... "
}

/**
 * POST 섹션: 사장님 소식 카드 목록. 본문은 6줄로 접고 ` ... 더보기` 를 탭하면 펼친다(펼침 상태는 카드별로 기억한다).
 *
 * @param onImageClick 사진을 탭하면 그 카드의 사진 목록과 탭한 위치를 넘긴다.
 */
@Composable
fun SDStorePostSection(
    model: SDStorePostSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
    onImageClick: (images: List<SDImageModel>, index: Int) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(SDSectionDefaults.HeaderContentSpacing)
    ) {
        SDSectionHeader(model = model.header, onAction = onAction)
        val cards = model.cards.orEmpty()
        if (cards.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(horizontal = SDSectionDefaults.HorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(SDStorePostSectionDefaults.CardSpacing)
            ) {
                cards.forEachIndexed { index, card ->
                    key(card.cardId ?: index) {
                        PostCard(card = card, onAction = onAction, onImageClick = onImageClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCard(
    card: SDStorePostSectionModel.Card,
    onAction: (SDActionEvent) -> Unit,
    onImageClick: (List<SDImageModel>, Int) -> Unit,
) {
    var expanded by rememberSaveable(card.cardId) { mutableStateOf(false) }
    val cardEvent = card.link?.let { SDActionEvent(link = it, clickLog = card.clickLog) }
    val insetModifier = Modifier.padding(horizontal = SDStorePostSectionDefaults.CardInset)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sdSurface(style = card.style, shape = SDStorePostSectionDefaults.CardShape)
            .sdClickable(event = cardEvent, onAction = onAction)
            .padding(vertical = SDStorePostSectionDefaults.CardInset),
        verticalArrangement = Arrangement.spacedBy(SDStorePostSectionDefaults.ContentSpacing)
    ) {
        card.header?.let { PostHeader(header = it, modifier = insetModifier) }
        val images = card.images.orEmpty()
        if (images.isNotEmpty()) {
            PostImages(images = images, onImageClick = onImageClick)
        }
        card.body?.let { body ->
            PostBody(
                body = body,
                expanded = expanded,
                onExpand = { expanded = true },
                modifier = insetModifier.fillMaxWidth()
            )
        }
        card.like?.let { like ->
            SDToggleButton(model = like, onAction = onAction, modifier = insetModifier)
        }
    }
}

@Composable
private fun PostHeader(header: SDChipModel, modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SDStorePostSectionDefaults.HeaderSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        header.image?.let { image ->
            SDImage(
                model = image,
                sizeFromStyle = false,
                modifier = Modifier
                    .size(SDStorePostSectionDefaults.HeaderIconSize)
                    .clip(CircleShape)
            )
        }
        Column {
            header.text?.let {
                SDText(
                    model = it,
                    fontSize = dpToSp(14),
                    fontWeight = FontWeight.W700,
                    lineHeight = dpToSp(20),
                    maxLines = 1
                )
            }
            header.additionalText?.let {
                SDText(model = it, fontSize = dpToSp(12), lineHeight = dpToSp(18), maxLines = 1)
            }
        }
    }
}

@Composable
private fun PostImages(
    images: List<SDImageModel>,
    onImageClick: (List<SDImageModel>, Int) -> Unit,
) {
    val height = SDStorePostSectionDefaults.ImageHeight
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        contentPadding = PaddingValues(horizontal = SDStorePostSectionDefaults.CardInset),
        horizontalArrangement = Arrangement.spacedBy(SDStorePostSectionDefaults.ImageSpacing)
    ) {
        itemsIndexed(images) { index, image ->
            val style = image.style
            val ratio = if (style != null && style.height > 0f) style.width / style.height else 1f
            SDImage(
                model = image,
                sizeFromStyle = false,
                modifier = Modifier
                    .size(width = height * ratio, height = height)
                    .clip(SDStorePostSectionDefaults.ImageShape)
                    .background(Gray10)
                    .noRippleClickable { onImageClick(images, index) }
            )
        }
    }
}

/**
 * 본문. 접힌 상태에서 6줄을 넘으면 6번째 줄 끝을 ` ... 더보기` 로 바꾸고, 그 본문을 탭하면 펼친다.
 * 넘치지 않으면 탭은 카드로 전달된다.
 */
@Composable
private fun PostBody(
    body: SDTextModel,
    expanded: Boolean,
    onExpand: () -> Unit,
    modifier: Modifier,
) {
    val density = LocalDensity.current
    val fullText = remember(body.text, body.isHtml, density.fontScale) { body.toAnnotatedString(density) }
    val style = TextStyle(
        color = body.fontColor.toColor(fallback = Gray95),
        fontSize = dpToSp(14),
        fontWeight = FontWeight.W400,
        lineHeight = dpToSp(20),
        fontFamily = PretendardFontFamily
    )
    val moreText = stringResource(CommonR.string.more)
    val moreColor = colorResource(DesignSystemR.color.color_B7B7B7)
    val textMeasurer = rememberTextMeasurer()
    BoxWithConstraints(modifier = modifier) {
        val maxWidth = constraints.maxWidth
        val collapsedText = remember(fullText, style, maxWidth, expanded, moreText, moreColor) {
            if (expanded || maxWidth == Constraints.Infinity) {
                null
            } else {
                val suffix = buildAnnotatedString {
                    append(SDStorePostSectionDefaults.BODY_SHORTENING)
                    withStyle(SpanStyle(color = moreColor, textDecoration = TextDecoration.Underline)) { append(moreText) }
                }
                collapseText(fullText, suffix, textMeasurer, style, maxWidth, SDStorePostSectionDefaults.BODY_MAX_LINES)
            }
        }
        Text(
            text = collapsedText ?: fullText,
            style = style,
            modifier = if (collapsedText != null) Modifier.noRippleClickable(onClick = onExpand) else Modifier
        )
    }
}

/**
 * [maxLines] 안에 들어가면 null, 넘치면 마지막 줄을 잘라 [suffix] 를 붙인 문자열을 돌려준다.
 */
private fun collapseText(
    fullText: AnnotatedString,
    suffix: AnnotatedString,
    textMeasurer: TextMeasurer,
    style: TextStyle,
    maxWidth: Int,
    maxLines: Int,
): AnnotatedString? {
    val constraints = Constraints(maxWidth = maxWidth)
    val layout = textMeasurer.measure(text = fullText, style = style, constraints = constraints)
    if (layout.lineCount <= maxLines) return null

    val lastLine = maxLines - 1
    val lineStart = layout.getLineStart(lastLine)
    val suffixWidth = textMeasurer.measure(text = suffix, style = style).size.width
    val lineY = (layout.getLineTop(lastLine) + layout.getLineBottom(lastLine)) / 2
    val fitEnd = layout.getOffsetForPosition(Offset((maxWidth - suffixWidth).coerceAtLeast(0).toFloat(), lineY))
    var end = minOf(layout.getLineEnd(lastLine, visibleEnd = true), fitEnd).coerceAtLeast(lineStart)

    fun candidate(end: Int): AnnotatedString = buildAnnotatedString {
        append(fullText.subSequence(0, end))
        append(suffix)
    }
    while (end > lineStart) {
        val text = candidate(end)
        if (textMeasurer.measure(text = text, style = style, constraints = constraints).lineCount <= maxLines) return text
        end--
    }
    return candidate(lineStart)
}
