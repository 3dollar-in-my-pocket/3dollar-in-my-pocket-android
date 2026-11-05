package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class StoreReviewRequest(
    @SerializedName("contents")
    val contents: String,
    @SerializedName("rating")
    val rating: Int? = null,
    @SerializedName("storeId")
    val storeId: Int? = null,
    @SerializedName("images")
    val images: List<ImageRequest>? = null,
    @SerializedName("feedbacks")
    val feedbacks: List<FeedbackRequest>? = null,
)

data class ImageRequest(
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int
)

data class FeedbackRequest(
    @SerializedName("emojiType")
    val emojiType: String
)
