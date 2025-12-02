package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class StoreReviewDetailResponse(
    @SerializedName("review")
    val review: Review? = Review(),
    @SerializedName("reviewReport")
    val reviewReport: ReviewReport? = ReviewReport(),
    @SerializedName("reviewWriter")
    val reviewWriter: ReviewWriter? = ReviewWriter(),
    @SerializedName("stickers")
    val stickers: List<Sticker>? = listOf(),
    @SerializedName("comments")
    val comments: ContentListCommentResponse = ContentListCommentResponse()
)