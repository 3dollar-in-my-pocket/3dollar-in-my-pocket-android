package com.zion830.threedollars.ui.feed.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import base.compose.ColorB7B7B7
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray20
import base.compose.Gray95
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.domain.feed.model.FeedBodyModel
import com.threedollar.domain.feed.model.FeedBodyType
import com.threedollar.domain.feed.model.FeedCategoryModel
import com.threedollar.domain.feed.model.FeedHeaderModel
import com.threedollar.domain.feed.model.FeedImageModel
import com.threedollar.domain.feed.model.FeedModel
import com.threedollar.domain.feed.model.FeedRatingModel
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.ui.feed.model.FeedTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/** 서버 드리븐 피드 카드. 카테고리 → 헤더 → 본문 순서로 그리고, 탭하면 서버 `link` 로 이동한다. */
@Composable
internal fun FeedCard(
    feed: FeedModel,
    onClick: () -> Unit,
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
        FeedCategoryRow(category = feed.category, updatedAt = feed.updatedAt)
        feed.header?.let {
            Spacer(modifier = Modifier.height(16.dp))
            FeedHeader(header = it)
        }
        feed.body?.let {
            Spacer(modifier = Modifier.height(12.dp))
            FeedBody(body = it)
        }
    }
}

@Composable
private fun FeedCategoryRow(category: FeedCategoryModel?, updatedAt: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (category != null) {
            SDText(
                model = category.name,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .clip(CircleShape)
                    .background(category.style?.backgroundColor.toColor(fallback = Gray10))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                fontSize = dpToSp(12),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
        Text(
            text = feedTimeText(updatedAt),
            modifier = Modifier.padding(start = 12.dp),
            color = ColorB7B7B7,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = dpToSp(12),
            maxLines = 1,
        )
    }
}

@Composable
private fun FeedHeader(header: FeedHeaderModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = header.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(60.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            header.top?.let {
                SDText(
                    model = it,
                    fontSize = dpToSp(12),
                    fontWeight = FontWeight.Medium,
                    lineHeight = dpToSp(18),
                    maxLines = 1,
                    color = it.fontColor.toColor(fallback = ColorB7B7B7),
                )
            }
            header.content?.let {
                SDText(
                    model = it,
                    fontSize = dpToSp(16),
                    fontWeight = FontWeight.Bold,
                    lineHeight = dpToSp(24),
                    maxLines = 1,
                    color = it.fontColor.toColor(fallback = Gray100),
                )
            }
            if (header.metadata.isNotEmpty()) {
                FeedHeaderMetadataRow(header)
            }
        }
    }
}

@Composable
private fun FeedHeaderMetadataRow(header: FeedHeaderModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        header.metadata.forEachIndexed { index, metadata ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(8.dp)
                        .background(Gray20),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = metadata.iconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                )
                metadata.content?.let {
                    SDText(
                        model = it,
                        fontSize = dpToSp(12),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        color = it.fontColor.toColor(fallback = ColorB7B7B7),
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedBody(body: FeedBodyModel) {
    val shape = RoundedCornerShape(12.dp)
    val modifier = Modifier
        .fillMaxWidth()
        .clip(shape)
        .background(body.style?.backgroundColor.toColor(fallback = Gray10), shape)
    when (body.type) {
        FeedBodyType.CONTENT_ONLY -> FeedContentOnlyBody(body, modifier)
        FeedBodyType.CONTENT_WITH_TITLE -> FeedContentWithTitleBody(body, modifier, withImages = false)
        FeedBodyType.CONTENT_WITH_TITLE_AND_IMAGES -> FeedContentWithTitleBody(body, modifier, withImages = true)
        FeedBodyType.CONTENT_WITH_IMAGES -> FeedContentWithImagesBody(body, modifier)
    }
}

@Composable
private fun FeedContentOnlyBody(body: FeedBodyModel, modifier: Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        body.contentLeadingImageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(16.dp),
            )
        }
        body.content?.let { FeedBodyText(it) }
    }
}

@Composable
private fun FeedContentWithTitleBody(body: FeedBodyModel, modifier: Modifier, withImages: Boolean) {
    Column(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            body.title?.let {
                SDText(
                    model = it,
                    modifier = Modifier.weight(1f),
                    fontSize = dpToSp(12),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            } ?: Spacer(modifier = Modifier.weight(1f))
            body.rating?.let { FeedRatingStars(it) }
        }
        if (withImages && body.images.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(body.images) { image ->
                    AsyncImage(
                        model = image.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                }
            }
        }
        body.content?.let { FeedBodyText(it, maxLines = TITLE_BODY_MAX_LINES) }
    }
}

@Composable
private fun FeedContentWithImagesBody(body: FeedBodyModel, modifier: Modifier) {
    Column(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (body.images.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(body.images) { image -> FeedRatioImage(image) }
            }
        }
        body.content?.let {
            FeedBodyText(it, modifier = Modifier.padding(horizontal = 16.dp), fallbackColor = Gray95)
        }
    }
}

@Composable
private fun FeedRatioImage(image: FeedImageModel) {
    AsyncImage(
        model = image.imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .height(IMAGES_BODY_IMAGE_HEIGHT_DP.dp)
            .width((IMAGES_BODY_IMAGE_HEIGHT_DP * image.ratio).dp)
            .clip(RoundedCornerShape(8.dp)),
    )
}

@Composable
private fun FeedBodyText(
    model: SDTextModel,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    fallbackColor: Color = Gray100,
) {
    SDText(
        model = model,
        modifier = modifier,
        fontSize = dpToSp(14),
        fontWeight = FontWeight.Normal,
        lineHeight = dpToSp(20),
        maxLines = maxLines,
        color = model.fontColor.toColor(fallback = fallbackColor),
    )
}

@Composable
private fun FeedRatingStars(rating: FeedRatingModel) {
    val filled = rating.filledColor.toColor(fallback = Pink)
    val empty = rating.emptyColor.toColor(fallback = Gray20)
    val filledCount = rating.starRating.roundToInt().coerceIn(0, rating.maxRating)
    Row(
        modifier = Modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(rating.maxRating) { index ->
            Image(
                painter = painterResource(DesignSystemR.drawable.ic_star_solid_12),
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (index < filledCount) filled else empty),
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
private fun feedTimeText(updatedAt: String): String = when (val time = FeedTime.of(updatedAt)) {
    FeedTime.JustNow -> stringResource(CommonR.string.feed_time_just_now)
    is FeedTime.Minutes -> stringResource(CommonR.string.feed_time_minutes, time.value.toInt())
    is FeedTime.Hours -> stringResource(CommonR.string.feed_time_hours, time.value.toInt())
    is FeedTime.Days -> stringResource(CommonR.string.feed_time_days, time.value.toInt())
    is FeedTime.Date -> time.date.format(DateTimeFormatter.ofPattern(stringResource(CommonR.string.feed_time_date_format)))
    null -> ""
}

private val CARD_SHADOW = Color.Black.copy(alpha = 0.06f)
private const val TITLE_BODY_MAX_LINES = 5
private const val IMAGES_BODY_IMAGE_HEIGHT_DP = 124
