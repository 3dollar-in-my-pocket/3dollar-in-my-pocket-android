package com.zion830.threedollars.datasource.model.v4.storeReview


import com.google.gson.annotations.SerializedName

data class ReviewModel(
    @SerializedName("review")
    val review: Review = Review(),
    @SerializedName("reviewReport")
    val reviewReport: ReviewReport = ReviewReport(),
    @SerializedName("reviewWriter")
    val reviewWriter: ReviewWriter = ReviewWriter(),
)