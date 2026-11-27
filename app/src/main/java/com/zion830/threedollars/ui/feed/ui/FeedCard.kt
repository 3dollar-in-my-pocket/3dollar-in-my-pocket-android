package com.zion830.threedollars.ui.feed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray20
import base.compose.Gray40
import base.compose.dpToSp
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsBodyBlockModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsBodyHeadingModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsSummaryModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.element.SDImage
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.ui.feed.model.FeedBadgeText

/**
 * 동네 소식 카드(`CONTENT_CARD`). 배지·경과 시간 → 가게 요약 → 본문 블록 순서로 서버 값을 그대로 그린다.
 * 카드를 누르면 [onClick], 본문 사진을 누르면 [onImageClick](블록 위치, 사진 위치)을 부른다.
 */
@Composable
internal fun FeedCard(
    card: SDHomeNewsItemModel.ContentCard,
    onClick: () -> Unit,
    onImageClick: (blockIndex: Int, imageIndex: Int) -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = shape, ambientColor = CARD_SHADOW, spotColor = CARD_SHADOW)
            .background(ColorWhite, shape)
            .clip(shape)
            .noRippleClickable(onClick = onClick)
            .padding(16.dp),
    ) {
        FeedCardTopRow(card)
        card.summary?.let {
            Spacer(modifier = Modifier.height(16.dp))
            FeedSummary(it)
        }
        card.bodyBlocks.forEachIndexed { blockIndex, block ->
            Spacer(modifier = Modifier.height(12.dp))
            FeedBodyBlock(block = block, onImageClick = { imageIndex -> onImageClick(blockIndex, imageIndex) })
        }
    }
}

@Composable
private fun FeedCardTopRow(card: SDHomeNewsItemModel.ContentCard) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        card.badge?.let { badge ->
            FeedBadge(badge = badge, modifier = Modifier.weight(1f, fill = false))
        }
        card.trailingText?.let {
            FeedText(
                model = it,
                modifier = Modifier.padding(start = 12.dp),
                fontSize = 12,
                fallbackColor = Gray40,
            )
        }
    }
}

/** 이모지는 문구와 떼어 따로 그려야 기기 이모지 폰트와 상관없이 글자가 배지 가운데에 온다 ([FeedBadgeText]). */
@Composable
private fun FeedBadge(badge: SDChipModel, modifier: Modifier = Modifier) {
    val text = badge.text ?: return
    val parts = remember(text) { FeedBadgeText.split(text) }
    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(badge.style?.backgroundColor.toColor(fallback = Gray10))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        parts.leadingEmoji?.let { emoji ->
            Text(text = emoji, fontSize = dpToSp(12), maxLines = 1)
            Spacer(modifier = Modifier.width(4.dp))
        }
        FeedText(model = parts.text, fontSize = 12, maxLines = 1, fontWeight = FontWeight.Bold, fallbackColor = ColorWhite)
    }
}

@Composable
private fun FeedSummary(summary: SDHomeNewsSummaryModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        summary.leadingImage?.let {
            SDImage(model = it, contentScale = ContentScale.Fit)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            summary.overline?.takeUnless { it.text.isNullOrBlank() }?.let {
                FeedText(model = it, fontSize = 12, lineHeight = 18, maxLines = 1, fallbackColor = Gray40)
            }
            summary.title?.let {
                FeedText(model = it, fontSize = 16, lineHeight = 24, maxLines = 1, fontWeight = FontWeight.Bold)
            }
            if (summary.metadata.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    summary.metadata.forEachIndexed { index, chip ->
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(8.dp)
                                    .background(Gray20),
                            )
                        }
                        SDChip(model = chip)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedBodyBlock(block: SDHomeNewsBodyBlockModel, onImageClick: (Int) -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(block.style?.backgroundColor.toColor(fallback = Gray10), shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        block.heading?.let { FeedBodyHeading(it) }
        if (block.images.isNotEmpty()) {
            FeedBodyImages(images = block.images, onImageClick = onImageClick)
        }
        block.text?.takeUnless { it.text.isNullOrBlank() }?.let { text ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                block.textLeadingImage?.let {
                    SDImage(model = it, modifier = Modifier.padding(top = 2.dp))
                }
                FeedText(model = text, fontSize = 14, lineHeight = 20, maxLines = BODY_MAX_LINES)
            }
        }
    }
}

@Composable
private fun FeedBodyHeading(heading: SDHomeNewsBodyHeadingModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        heading.leadingImage?.let {
            SDImage(model = it)
            Spacer(modifier = Modifier.width(4.dp))
        }
        heading.title?.let {
            FeedText(model = it, modifier = Modifier.weight(1f), fontSize = 12, maxLines = 1, fontWeight = FontWeight.Bold)
        } ?: Spacer(modifier = Modifier.weight(1f))
        if (heading.trailingImages.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                heading.trailingImages.forEach { SDImage(model = it) }
            }
        }
    }
}

/** 사진은 서버 `style` 크기대로 그려서 한 번에 보이는 개수가 사진 크기에 따라 정해진다. */
@Composable
private fun FeedBodyImages(images: List<SDImageModel>, onImageClick: (Int) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(images) { index, image ->
            SDImage(
                model = image,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .noRippleClickable { onImageClick(index) },
            )
        }
    }
}

@Composable
private fun FeedText(
    model: SDTextModel,
    fontSize: Int,
    modifier: Modifier = Modifier,
    lineHeight: Int? = null,
    maxLines: Int = Int.MAX_VALUE,
    fontWeight: FontWeight = FontWeight.Normal,
    fallbackColor: Color = Gray100,
) {
    SDText(
        model = model,
        modifier = modifier,
        fontSize = dpToSp(fontSize),
        fontWeight = fontWeight,
        lineHeight = lineHeight?.let { dpToSp(it) } ?: TextUnit.Unspecified,
        maxLines = maxLines,
        color = model.fontColor.toColor(fallback = fallbackColor),
    )
}

private val CARD_SHADOW = Color.Black.copy(alpha = 0.06f)
private const val BODY_MAX_LINES = 5
