package com.zion830.threedollars.datasource.model.v4.storeReview.request

import com.google.gson.annotations.SerializedName

data class StoreReviewReportRequest(
    @SerializedName("reason")
    val reason: String,
    @SerializedName("reasonDetail")
    val reasonDetail: String,
)