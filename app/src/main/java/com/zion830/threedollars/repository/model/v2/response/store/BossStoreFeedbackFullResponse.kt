package com.zion830.threedollars.repository.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class BossStoreFeedbackFullResponse(
    @SerializedName("data")
    val data: List<BossStoreFeedbackFullModel>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("resultCode")
    val resultCode: String?
) {
    data class BossStoreFeedbackFullModel(
        @SerializedName("count")
        val count: Int,
        @SerializedName("feedbackType")
        val feedbackType: String?,
        @SerializedName("ratio")
        val ratio: Double?
    )
}