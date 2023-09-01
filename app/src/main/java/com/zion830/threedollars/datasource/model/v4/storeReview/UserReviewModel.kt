package com.zion830.threedollars.datasource.model.v4.storeReview


import com.google.gson.annotations.SerializedName

data class UserReviewModel(
    @SerializedName("review")
    val review: Review = Review(),
    @SerializedName("reviewWriter")
    val reviewWriter: ReviewWriter = ReviewWriter(),
    @SerializedName("store")
    val store: Store = Store()
)