package com.threedollar.network.data.feed

import com.google.gson.annotations.SerializedName
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.network.data.store.Cursor

data class FeedListResponse(
    @SerializedName("contents")
    val contents: List<FeedResponse>? = null,
    @SerializedName("cursor")
    val cursor: Cursor? = null,
)

data class FeedResponse(
    @SerializedName("feedId")
    val feedId: String? = null,
    @SerializedName("category")
    val category: FeedCategoryResponse? = null,
    @SerializedName("header")
    val header: FeedHeaderResponse? = null,
    @SerializedName("body")
    val body: FeedBodyResponse? = null,
    @SerializedName("link")
    val link: SDLink? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
)

data class FeedCategoryResponse(
    @SerializedName("categoryId")
    val categoryId: String? = null,
    @SerializedName("name")
    val name: SDTextModel? = null,
    @SerializedName("style")
    val style: SDSurfaceStyleModel? = null,
)

/** `type` 으로 구분되는 헤더. 타입별 필드를 모두 nullable 로 받아 알 수 없는 타입도 파싱이 깨지지 않게 한다. */
data class FeedHeaderResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("image")
    val image: FeedImageResponse? = null,
    @SerializedName("top")
    val top: SDTextModel? = null,
    @SerializedName("content")
    val content: SDTextModel? = null,
    @SerializedName("metadata")
    val metadata: List<FeedHeaderMetadataResponse>? = null,
)

data class FeedHeaderMetadataResponse(
    @SerializedName("icon")
    val icon: FeedImageResponse? = null,
    @SerializedName("content")
    val content: SDTextModel? = null,
)

/** `type` 으로 구분되는 본문. 타입별 필드를 모두 nullable 로 받아 알 수 없는 타입도 파싱이 깨지지 않게 한다. */
data class FeedBodyResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("title")
    val title: SDTextModel? = null,
    @SerializedName("content")
    val content: SDTextModel? = null,
    @SerializedName("contentLeadingImage")
    val contentLeadingImage: FeedImageResponse? = null,
    @SerializedName("images")
    val images: List<FeedImageResponse>? = null,
    @SerializedName("style")
    val style: SDSurfaceStyleModel? = null,
    @SerializedName("additionalInfos")
    val additionalInfos: FeedAdditionalInfoResponse? = null,
)

data class FeedImageResponse(
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("width")
    val width: Int? = null,
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("ratio")
    val ratio: Float? = null,
)

data class FeedAdditionalInfoResponse(
    @SerializedName("rating")
    val rating: FeedRatingResponse? = null,
)

data class FeedRatingResponse(
    @SerializedName("starRating")
    val starRating: Float? = null,
    @SerializedName("maxRating")
    val maxRating: Int? = null,
    @SerializedName("style")
    val style: FeedRatingStyleResponse? = null,
)

data class FeedRatingStyleResponse(
    @SerializedName("filledColor")
    val filledColor: String? = null,
    @SerializedName("emptyColor")
    val emptyColor: String? = null,
)
