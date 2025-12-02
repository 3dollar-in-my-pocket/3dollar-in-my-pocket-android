package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class BossStoreReviewRequest(
    @SerializedName("storeId")
    val storeId: String,
    @SerializedName("contents")
    val contents: String,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("images")
    val images: List<ImageRequest>,
    @SerializedName("feedbacks")
    val feedbacks: List<FeedbackRequest>
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