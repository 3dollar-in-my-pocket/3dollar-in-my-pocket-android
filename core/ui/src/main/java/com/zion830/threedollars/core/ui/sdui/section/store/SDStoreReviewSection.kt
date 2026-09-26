package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.dpToSp
import com.threedollar.common.R as CommonR
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.section.SDStoreReviewSectionModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.component.SDSectionHeader
import com.zion830.threedollars.core.ui.sdui.element.SDButton
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.element.SDImage
import com.zion830.threedollars.core.ui.sdui.element.SDRatingChip
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.element.SDToggleButton
import com.zion830.threedollars.core.ui.sdui.foundation.backgroundColorOr
import com.zion830.threedollars.core.ui.sdui.foundation.sdClickable
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreReviewSectionDefaults {
    val ContentSpacing = 8.dp
    val SummaryShape = RoundedCornerShape(20.dp)
    val SummaryPadding = 14.dp
    val SummaryTitleSpacing = 6.dp
    val CardShape = RoundedCornerShape(20.dp)
    val CardInset = 16.dp
    val HeaderBadgeSpacing = 6.dp
    val BadgeShape = RoundedCornerShape(4.dp)
    val BadgePadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
    val BadgeSpacing = 4.dp
    val ImageSize = 96.dp
    val ImageShape = RoundedCornerShape(8.dp)
    val BlindedShape = RoundedCornerShape(8.dp)
    val BlindedInset = 12.dp
    val ReplyShape = RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
    val ReplyTailWidth = 16.dp
    val ReplyTailHeight = 12.dp
    val ReplyPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    val MoreButtonHeight = 46.dp
}

/**
 * REVIEW 섹션: 평균 별점 요약, 리뷰 카드 목록(사장님 답글·블라인드 포함), 리뷰 더보기 버튼.
 * 카드의 신고·삭제(`header.trailingAction`)와 좋아요는 [onAction]으로 올린다. 리뷰가 없으면 빈 상태 뷰를 보여준다.
 *
 * @param onImageClick 사진을 탭하면 그 리뷰의 사진 목록과 탭한 위치를 넘긴다.
 */
@Composable
fun SDStoreReviewSection(
    model: SDStoreReviewSectionModel,
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
        Column(
            modifier = Modifier.padding(horizontal = SDSectionDefaults.HorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(SDStoreReviewSectionDefaults.ContentSpacing)
        ) {
            model.summary?.let { ReviewSummary(it) }
            val cards = model.cards.orEmpty()
            if (cards.isEmpty()) {
                SDStoreSectionEmptyView(text = stringResource(CommonR.string.review_empty))
            }
            cards.forEachIndexed { index, card ->
                key(card.cardId ?: index) {
                    if (card.isBlinded) {
                        BlindedReviewCard(card)
                    } else {
                        ReviewCard(card = card, onAction = onAction, onImageClick = onImageClick)
                    }
                }
            }
            model.more?.button?.let { more ->
                SDButton(
                    model = more,
                    onAction = onAction,
                    fallbackLog = model.more?.clickLog,
                    shape = RectangleShape,
                    fontSize = dpToSp(12),
                    fontWeight = FontWeight.W500,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SDStoreReviewSectionDefaults.MoreButtonHeight)
                )
            }
        }
    }
}

@Composable
private fun ReviewSummary(summary: SDStoreReviewSectionModel.Summary) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sdSurface(style = summary.style, shape = SDStoreReviewSectionDefaults.SummaryShape)
            .padding(SDStoreReviewSectionDefaults.SummaryPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SDStoreReviewSectionDefaults.SummaryTitleSpacing)
    ) {
        summary.title?.let { SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W500, maxLines = 1) }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            summary.stars?.let { SDRatingChip(model = it) }
            summary.rating?.let { SDText(model = it, fontSize = dpToSp(18), fontWeight = FontWeight.W700, maxLines = 1) }
        }
    }
}

@Composable
private fun BlindedReviewCard(card: SDStoreReviewSectionModel.Card) {
    val body = card.body ?: return
    SDText(
        model = body,
        fontSize = dpToSp(14),
        fontWeight = FontWeight.W400,
        modifier = Modifier
            .fillMaxWidth()
            .sdSurface(style = card.style, shape = SDStoreReviewSectionDefaults.BlindedShape)
            .padding(SDStoreReviewSectionDefaults.BlindedInset)
    )
}

@Composable
private fun ReviewCard(
    card: SDStoreReviewSectionModel.Card,
    onAction: (SDActionEvent) -> Unit,
    onImageClick: (List<SDImageModel>, Int) -> Unit,
) {
    val inset = SDStoreReviewSectionDefaults.CardInset
    val cardEvent = card.link?.let { SDActionEvent(link = it, clickLog = card.clickLog) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .sdSurface(style = card.style, shape = SDStoreReviewSectionDefaults.CardShape)
            .sdClickable(event = cardEvent, onAction = onAction)
            .padding(vertical = inset),
        verticalArrangement = Arrangement.spacedBy(SDStoreReviewSectionDefaults.ContentSpacing)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = inset),
            verticalArrangement = Arrangement.spacedBy(SDStoreReviewSectionDefaults.HeaderBadgeSpacing)
        ) {
            ReviewCardHeader(card = card, onAction = onAction)
            ReviewBadges(card)
        }
        val images = card.images.orEmpty()
        if (images.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = inset),
                horizontalArrangement = Arrangement.spacedBy(SDStoreReviewSectionDefaults.ContentSpacing)
            ) {
                itemsIndexed(images) { index, image ->
                    SDImage(
                        model = image,
                        sizeFromStyle = false,
                        modifier = Modifier
                            .size(SDStoreReviewSectionDefaults.ImageSize)
                            .clip(SDStoreReviewSectionDefaults.ImageShape)
                            .background(Gray10)
                            .noRippleClickable { onImageClick(images, index) }
                    )
                }
            }
        }
        card.body?.let {
            SDText(
                model = it,
                fontSize = dpToSp(14),
                fontWeight = FontWeight.W400,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = inset)
            )
        }
        card.like?.let { SDToggleButton(model = it, onAction = onAction, modifier = Modifier.padding(horizontal = inset)) }
        card.reply?.let { ReviewReply(reply = it, modifier = Modifier.padding(horizontal = inset)) }
    }
}

@Composable
private fun ReviewCardHeader(
    card: SDStoreReviewSectionModel.Card,
    onAction: (SDActionEvent) -> Unit,
) {
    val header = card.header ?: return
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f)) {
            header.title?.let { SDText(model = it, fontSize = dpToSp(13), fontWeight = FontWeight.W500, maxLines = 1) }
        }
        header.subTitle?.let {
            Spacer(modifier = Modifier.width(8.dp))
            SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W500, maxLines = 1)
        }
        header.trailingAction?.let { action ->
            Spacer(modifier = Modifier.width(6.dp))
            SDButton(model = action, onAction = onAction, fontSize = dpToSp(12), fontWeight = FontWeight.W700)
        }
    }
}

@Composable
private fun ReviewBadges(card: SDStoreReviewSectionModel.Card) {
    val metadata = card.metadata.orEmpty()
    val stars = card.stars
    if (metadata.isEmpty() && stars == null) return
    Row(
        horizontalArrangement = Arrangement.spacedBy(SDStoreReviewSectionDefaults.BadgeSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        metadata.forEach { chip ->
            SDChip(
                model = chip,
                modifier = Modifier
                    .sdSurface(style = chip.style, shape = SDStoreReviewSectionDefaults.BadgeShape)
                    .padding(SDStoreReviewSectionDefaults.BadgePadding)
            )
        }
        stars?.let {
            SDRatingChip(
                model = it,
                modifier = Modifier
                    .sdSurface(style = it.style, shape = SDStoreReviewSectionDefaults.BadgeShape)
                    .padding(SDStoreReviewSectionDefaults.BadgePadding)
            )
        }
    }
}

/**
 * 사장님 답글 말풍선. 왼쪽 위 꼬리는 말풍선과 같은 배경색으로 그린다.
 */
@Composable
private fun ReviewReply(reply: SDStoreReviewSectionModel.Reply, modifier: Modifier) {
    val bubbleColor = reply.style.backgroundColorOr(Gray10)
    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier.size(
                width = SDStoreReviewSectionDefaults.ReplyTailWidth,
                height = SDStoreReviewSectionDefaults.ReplyTailHeight
            )
        ) {
            val tail = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, size.height)
                lineTo(size.width, size.height)
                close()
            }
            drawPath(path = tail, color = bubbleColor)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .sdSurface(style = reply.style, shape = SDStoreReviewSectionDefaults.ReplyShape, defaultBackground = Gray10)
                .padding(SDStoreReviewSectionDefaults.ReplyPadding),
            verticalArrangement = Arrangement.spacedBy(SDStoreReviewSectionDefaults.ContentSpacing)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(modifier = Modifier.weight(1f)) {
                    reply.header?.title?.let {
                        SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W700, maxLines = 1)
                    }
                }
                reply.header?.subTitle?.let {
                    Spacer(modifier = Modifier.width(8.dp))
                    SDText(model = it, fontSize = dpToSp(12), fontWeight = FontWeight.W500, maxLines = 1)
                }
            }
            reply.body?.let { SDText(model = it, fontSize = dpToSp(14), fontWeight = FontWeight.W400) }
        }
    }
}
