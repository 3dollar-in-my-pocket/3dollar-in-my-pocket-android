package com.zion830.threedollars.core.ui.serverdriven

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import base.compose.ColorWhite
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDCardModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDHeaderModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.common.serverdriven.model.SDScreenModel
import com.threedollar.common.serverdriven.model.SDSurfaceStyleModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.ext.styledSegments
import com.threedollar.common.serverdriven.ext.displayText

private val Gray100 = Color(0xFF0F0F0F)
private val Gray50 = Color(0xFF666666)
private val Gray30 = Color(0xFFE4E4E4)
private val DefaultCardShape = RoundedCornerShape(20.dp)
private val DefaultButtonShape = RoundedCornerShape(12.dp)

@Composable
fun SDScreenBodyRenderer(
    screen: SDScreenModel,
    modifier: Modifier = Modifier,
    onAction: (SDLinkModel) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        screen.sections.forEach { section ->
            SDSectionRenderer(section = section, onAction = onAction)
        }
    }
}

@Composable
fun SDSectionRenderer(
    section: SDSectionModel,
    onAction: (SDLinkModel) -> Unit,
    onButtonAction: (SDButtonModel) -> Unit = { button -> button.link?.let(onAction) },
) {
    when (section) {
        is SDSectionModel.ActionBarSection -> {
            SDActionButton(
                button = section.actionBar.button,
                fillMaxWidth = true,
                onClick = {
                    onButtonAction(section.actionBar.button.copy(clickLog = section.actionBar.clickLog ?: section.actionBar.button.clickLog))
                },
            )
        }

        is SDSectionModel.HeaderSection -> {
            SDHeaderRenderer(header = section.header, onButtonAction = onButtonAction)
        }

        is SDSectionModel.CardsSection -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                section.cards.forEach { card ->
                    SDCardRenderer(card = card)
                }
            }
        }

        is SDSectionModel.Unknown -> Unit
    }
}

@Composable
private fun SDHeaderRenderer(header: SDHeaderModel, onButtonAction: (SDButtonModel) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SDTextRenderer(header.title, fontWeight = FontWeight.Bold, fontSizeDp = 24, lineHeightDp = 32)
            header.subTitle?.takeIf { it.displayText().isNotBlank() }?.let {
                SDTextRenderer(it, fontSizeDp = 14, lineHeightDp = 20)
            }
        }
        header.trailingAction?.let { button ->
            SDActionButton(button, fillMaxWidth = false, onClick = { onButtonAction(button) })
        }
    }
}

@Composable
fun SDCardRenderer(card: SDCardModel) {
    when (card) {
        is SDCardModel.DescriptionCard -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(card.style.toSurfaceModifier(DefaultCardShape))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                SDTextRenderer(
                    text = card.title,
                    fontWeight = FontWeight.Bold,
                    fontSizeDp = 16,
                    lineHeightDp = 24,
                )
                if (card.description.displayText().isNotBlank()) {
                    SDTextRenderer(
                        text = card.description,
                        color = Gray50,
                        fontWeight = FontWeight.Medium,
                        fontSizeDp = 12,
                        lineHeightDp = 18,
                    )
                }
            }
        }

        is SDCardModel.HistoryCard -> {
            val cardImage = card.image
            val subtitleChip = card.subTitleChip
            val subtitleChipImage = subtitleChip?.image
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(card.style.toSurfaceModifier(DefaultCardShape))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(subtitleChip?.style.toBackgroundColor(default = Color(0xFFEDF7EF))),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        cardImage != null -> SDImageRenderer(cardImage)
                        subtitleChipImage != null -> SDImageRenderer(subtitleChipImage)
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    SDTextRenderer(
                        text = card.title,
                        fontWeight = FontWeight.Bold,
                        fontSizeDp = 16,
                        lineHeightDp = 24,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        card.subTitles.forEach { subTitle ->
                            SDTextRenderer(
                                text = subTitle,
                                color = Gray50,
                                fontWeight = FontWeight.Medium,
                                fontSizeDp = 12,
                                lineHeightDp = 18,
                            )
                        }
                    }

                    subtitleChip?.takeIf {
                        it.image != null || it.additionalText != null || it.text.text.isNotBlank()
                    }?.let {
                        SDChipRenderer(chip = it)
                    }
                }

                card.metadata?.let {
                    SDTextRenderer(
                        text = it,
                        color = Gray50,
                        fontWeight = FontWeight.Medium,
                        fontSizeDp = 12,
                        lineHeightDp = 18,
                        maxLines = 1,
                    )
                }
            }
        }

        is SDCardModel.Unknown -> Unit
    }
}

@Composable
fun SDActionButton(
    button: SDButtonModel,
    fillMaxWidth: Boolean,
    onAction: (SDLinkModel) -> Unit,
) = SDActionButton(
    button = button,
    fillMaxWidth = fillMaxWidth,
    onClick = { button.link?.let(onAction) },
)

@Composable
fun SDActionButton(
    button: SDButtonModel,
    fillMaxWidth: Boolean,
    modifier: Modifier = Modifier,
    imageOverride: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val sizeModifier = if (fillMaxWidth) {
        modifier
            .fillMaxWidth()
            .height(48.dp)
    } else {
        modifier.defaultMinSize(minHeight = 40.dp)
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = sizeModifier,
        shape = DefaultButtonShape,
        border = button.style.toBorderStroke(defaultColor = Gray100),
        colors = ButtonDefaults.buttonColors(
            containerColor = button.style.toBackgroundColor(default = ColorWhite),
            contentColor = button.text.fontColor.toComposeColor(default = ColorWhite),
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
        ),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
    ) {
        val image = button.image
        val hasImage = image != null || imageOverride != null
        val hasText = remember(button.text) { button.text.displayText().isNotBlank() }
        val imageAtEnd = button.imageAlignment.equals("END", ignoreCase = true)
        Row(
            modifier = if (hasText && imageAtEnd && fillMaxWidth) Modifier.fillMaxWidth() else Modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (hasImage && !imageAtEnd) {
                imageOverride?.invoke() ?: image?.let { SDImageRenderer(image = it) }
            }
            if (hasText) {
                SDTextRenderer(
                    text = button.text,
                    fontSizeDp = 16,
                    lineHeightDp = 24,
                    maxLines = 1,
                )
            }
            if (hasImage && imageAtEnd) {
                if (hasText && fillMaxWidth) Spacer(Modifier.weight(1f))
                imageOverride?.invoke() ?: image?.let { SDImageRenderer(image = it) }
            }
        }
    }
}

@Composable
fun SDChipRenderer(
    chip: SDChipModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
) {
    val image = chip.image?.takeIf { it.url.isNotBlank() }
    val hasText = chip.text.displayText().isNotBlank()
    val additionalText = chip.additionalText?.takeIf { it.displayText().isNotBlank() }
    if (image == null && !hasText && additionalText == null) return
    val imageAtEnd = chip.imageAlignment.equals("END", ignoreCase = true)
    val spacing = chip.contentSpacing?.takeIf { it.isFinite() }?.coerceAtLeast(0.0) ?: 4.0
    val shape = RoundedCornerShape(999.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(chip.style.toBackgroundColor(default = Color.Transparent))
            .then(chip.style.toBorderModifier(shape = shape, defaultColor = Color.Transparent))
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (image != null && !imageAtEnd) {
            SDImageRenderer(image)
        }
        if (hasText) {
            SDTextRenderer(
                text = chip.text,
                fontWeight = FontWeight.Medium,
                fontSizeDp = 12,
                lineHeightDp = 18,
            )
        }
        additionalText?.let {
            SDTextRenderer(
                text = it,
                color = Gray50,
                fontWeight = FontWeight.Medium,
                fontSizeDp = 12,
                lineHeightDp = 18,
            )
        }
        if (image != null && imageAtEnd) {
            SDImageRenderer(image)
        }
    }
}

fun Modifier.serverDrivenSurface(
    style: SDSurfaceStyleModel?,
    shape: Shape,
    defaultColor: Color = ColorWhite,
): Modifier = then(style.toSurfaceModifier(shape = shape, defaultColor = defaultColor))

@Composable
fun SDTextRenderer(
    text: SDTextModel,
    modifier: Modifier = Modifier,
    color: Color = text.fontColor.toComposeColor(default = Gray100),
    fontWeight: FontWeight = FontWeight.Normal,
    fontSizeDp: Int = 14,
    lineHeightDp: Int = 20,
    maxLines: Int = Int.MAX_VALUE,
) {
    val density = LocalDensity.current
    val annotatedText = remember(text, color, fontWeight, fontSizeDp, density.fontScale) {
        buildAnnotatedString {
            text.styledSegments().forEach { segment ->
                pushStyle(
                    SpanStyle(
                        color = segment.fontColor.toComposeColor(default = color),
                        fontWeight = segment.fontWeight.toComposeFontWeight(default = fontWeight),
                        fontSize = segment.fontSizePx
                            ?.let { (it / density.fontScale).sp }
                            ?: (fontSizeDp / density.fontScale).sp,
                    )
                )
                append(segment.text)
                pop()
            }
        }
    }
    Text(
        text = annotatedText,
        modifier = modifier,
        color = color,
        fontFamily = PretendardFontFamily,
        fontWeight = fontWeight,
        fontSize = dpToSp(fontSizeDp),
        lineHeight = dpToSp(lineHeightDp),
        maxLines = maxLines,
        overflow = if (maxLines == Int.MAX_VALUE) TextOverflow.Clip else TextOverflow.Ellipsis,
    )
}

@Composable
fun SDTextSpansRenderer(
    textSpans: AnnotatedString,
    modifier: Modifier = Modifier,
    fontSizeDp: Int = 14,
    lineHeightDp: Int = 20,
) {
    Text(
        text = textSpans,
        modifier = modifier,
        fontFamily = PretendardFontFamily,
        fontSize = dpToSp(fontSizeDp),
        lineHeight = dpToSp(lineHeightDp),
    )
}

fun buildAnnotatedStringFromSpans(spans: List<SDTextModel>): AnnotatedString = buildAnnotatedString {
    spans.forEach { span ->
        pushStyle(
            SpanStyle(
                color = span.fontColor.toComposeColor(default = Gray100),
            )
        )
        append(HtmlCompat.fromHtml(span.text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString())
        pop()
    }
}

@Composable
fun SDImageRenderer(
    image: SDImageModel,
    modifier: Modifier = Modifier,
    defaultWidthDp: Double = 16.0,
    defaultHeightDp: Double = 16.0,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val width = image.style?.width ?: defaultWidthDp
    val height = image.style?.height ?: defaultHeightDp
    if (!width.isFinite() || !height.isFinite() || width <= 0.0 || height <= 0.0) return
    AsyncImage(
        model = image.url,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier.width(width.dp).aspectRatio((width / height).toFloat()),
    )
}

private fun SDSurfaceStyleModel?.toSurfaceModifier(
    shape: Shape,
    defaultColor: Color = ColorWhite,
): Modifier = Modifier
    .clip(shape)
    .background(this.toBackgroundColor(default = defaultColor))
    .then(this.toBorderModifier(shape = shape, defaultColor = Color.Transparent))

private fun SDSurfaceStyleModel?.toBorderModifier(
    shape: Shape,
    defaultColor: Color,
): Modifier {
    val border = this?.border ?: return Modifier
    val width = ((border.width ?: 0.0).toFloat()).dp
    return if (width.value > 0f) {
        Modifier.border(BorderStroke(width, border.color.toComposeColor(default = defaultColor)), shape)
    } else {
        Modifier
    }
}

private fun SDSurfaceStyleModel?.toBorderStroke(defaultColor: Color): BorderStroke? {
    val border = this?.border ?: return null
    val width = ((border.width ?: 0.0).toFloat()).dp
    return if (width.value > 0f) {
        BorderStroke(width, border.color.toComposeColor(default = defaultColor))
    } else {
        null
    }
}

private fun SDSurfaceStyleModel?.toBackgroundColor(default: Color): Color =
    this?.backgroundColor.toComposeColor(default)

private fun String?.toComposeColor(default: Color): Color = toColor(fallback = default)

private fun Int?.toComposeFontWeight(default: FontWeight): FontWeight = when (this) {
    100 -> FontWeight.Thin
    200 -> FontWeight.ExtraLight
    300 -> FontWeight.Light
    400 -> FontWeight.Normal
    500 -> FontWeight.Medium
    600 -> FontWeight.SemiBold
    700 -> FontWeight.Bold
    800 -> FontWeight.ExtraBold
    900 -> FontWeight.Black
    else -> default
}
