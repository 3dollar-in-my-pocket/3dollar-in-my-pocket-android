package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class ReviewContent(
    @SerializedName("review")
    val review: Review? = Review(),
    @SerializedName("reviewReport")
    val reviewReport: ReviewReport? = ReviewReport(),
    @SerializedName("reviewWriter")
    val reviewWriter: ReviewWriter? = ReviewWriter(),
)