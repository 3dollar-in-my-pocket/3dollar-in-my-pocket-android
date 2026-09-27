package com.threedollar.domain.feed.model

import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel

/** 피드 목록 한 페이지. [nextCursor]가 null 이면 마지막 페이지다. */
data class FeedPageModel(
    val feeds: List<FeedModel>,
    val nextCursor: String?,
)

/** 서버 드리븐 피드 카드 한 장. 헤더·본문은 서버 `type` 으로 모양이 정해진다. */
data class FeedModel(
    val feedId: String,
    val category: FeedCategoryModel?,
    val header: FeedHeaderModel?,
    val body: FeedBodyModel?,
    val link: SDLink?,
    val updatedAt: String,
)

data class FeedCategoryModel(
    val name: SDTextModel,
    val style: SDSurfaceStyleModel?,
)

/** 알 수 없는 헤더 타입은 [FeedHeaderType.GENERAL] 로 그린다. */
data class FeedHeaderModel(
    val type: FeedHeaderType,
    val imageUrl: String?,
    val top: SDTextModel?,
    val content: SDTextModel?,
    val metadata: List<FeedHeaderMetadataModel>,
)

enum class FeedHeaderType {
    GENERAL;

    companion object {
        fun from(value: String?): FeedHeaderType = entries.firstOrNull { it.name == value } ?: GENERAL
    }
}

data class FeedHeaderMetadataModel(
    val iconUrl: String?,
    val content: SDTextModel?,
)

/** 알 수 없는 본문 타입은 [FeedBodyType.CONTENT_ONLY] 로 그린다. */
data class FeedBodyModel(
    val type: FeedBodyType,
    val title: SDTextModel?,
    val content: SDTextModel?,
    val contentLeadingImageUrl: String?,
    val images: List<FeedImageModel>,
    val style: SDSurfaceStyleModel?,
    val rating: FeedRatingModel?,
)

enum class FeedBodyType {
    CONTENT_ONLY,
    CONTENT_WITH_TITLE,
    CONTENT_WITH_TITLE_AND_IMAGES,
    CONTENT_WITH_IMAGES;

    companion object {
        private const val TITLE_CONTENT_IMAGES = "TITLE_CONTENT_IMAGES"

        fun from(value: String?): FeedBodyType = when (value) {
            TITLE_CONTENT_IMAGES -> CONTENT_WITH_IMAGES
            else -> entries.firstOrNull { it.name == value } ?: CONTENT_ONLY
        }
    }
}

/** [ratio]는 서버가 정한 표시 크기의 가로/세로 비율(원본 이미지 비율이 아니다). */
data class FeedImageModel(
    val imageUrl: String,
    val ratio: Float,
)

data class FeedRatingModel(
    val starRating: Float,
    val maxRating: Int,
    val filledColor: String?,
    val emptyColor: String?,
)
