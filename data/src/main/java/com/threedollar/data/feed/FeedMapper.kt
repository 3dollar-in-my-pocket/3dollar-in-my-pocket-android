package com.threedollar.data.feed

import com.threedollar.domain.feed.model.FeedBodyModel
import com.threedollar.domain.feed.model.FeedBodyType
import com.threedollar.domain.feed.model.FeedCategoryModel
import com.threedollar.domain.feed.model.FeedHeaderMetadataModel
import com.threedollar.domain.feed.model.FeedHeaderModel
import com.threedollar.domain.feed.model.FeedHeaderType
import com.threedollar.domain.feed.model.FeedImageModel
import com.threedollar.domain.feed.model.FeedModel
import com.threedollar.domain.feed.model.FeedPageModel
import com.threedollar.domain.feed.model.FeedRatingModel
import com.threedollar.network.data.feed.FeedBodyResponse
import com.threedollar.network.data.feed.FeedCategoryResponse
import com.threedollar.network.data.feed.FeedHeaderResponse
import com.threedollar.network.data.feed.FeedImageResponse
import com.threedollar.network.data.feed.FeedListResponse
import com.threedollar.network.data.feed.FeedRatingResponse
import com.threedollar.network.data.feed.FeedResponse

fun FeedListResponse.asModel(): FeedPageModel = FeedPageModel(
    feeds = contents.orEmpty().mapNotNull { it.asModel() },
    nextCursor = cursor?.takeIf { it.hasMore == true }?.nextCursor,
)

private fun FeedResponse.asModel(): FeedModel? {
    val id = feedId?.takeIf { it.isNotBlank() } ?: return null
    return FeedModel(
        feedId = id,
        category = category?.asModel(),
        header = header?.asModel(),
        body = body?.asModel(),
        link = link,
        updatedAt = updatedAt ?: createdAt.orEmpty(),
    )
}

private fun FeedCategoryResponse.asModel(): FeedCategoryModel? =
    name?.let { FeedCategoryModel(name = it, style = style) }

private fun FeedHeaderResponse.asModel() = FeedHeaderModel(
    type = FeedHeaderType.from(type),
    imageUrl = image?.imageUrl,
    top = top,
    content = content,
    metadata = metadata.orEmpty().map { FeedHeaderMetadataModel(iconUrl = it.icon?.imageUrl, content = it.content) },
)

private fun FeedBodyResponse.asModel() = FeedBodyModel(
    type = FeedBodyType.from(type),
    title = title,
    content = content,
    contentLeadingImageUrl = contentLeadingImage?.imageUrl,
    images = images.orEmpty().mapNotNull { it.asModel() },
    style = style,
    rating = additionalInfos?.rating?.asModel(),
)

/**
 * 이미지 칸 비율은 서버가 정한 표시 크기(`width`/`height`)로 정한다 (iOS 와 동일).
 * `ratio` 는 원본 이미지 비율이라 칸 크기에 쓰지 않고, 크기 정보가 없으면 iOS 처럼 그리지 않는다.
 */
private fun FeedImageResponse.asModel(): FeedImageModel? {
    val url = imageUrl?.takeIf { it.isNotBlank() } ?: return null
    val w = width ?: 0
    val h = height ?: 0
    if (w <= 0 || h <= 0) return null
    return FeedImageModel(imageUrl = url, ratio = w.toFloat() / h)
}

private fun FeedRatingResponse.asModel(): FeedRatingModel? {
    val rating = starRating ?: return null
    return FeedRatingModel(
        starRating = rating,
        maxRating = maxRating?.takeIf { it > 0 } ?: DEFAULT_MAX_RATING,
        filledColor = style?.filledColor,
        emptyColor = style?.emptyColor,
    )
}

private const val DEFAULT_MAX_RATING = 5
