package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BossStoreFeedbackTypeResponse(
    @SerializedName("data")
    val data: List<BossStoreFeedbackTypeModel>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("resultCode")
    val resultCode: String?
) {
    data class BossStoreFeedbackTypeModel(
        @SerializedName("description")
        val description: String?,
        @SerializedName("emoji")
        val emoji: String?,
        @SerializedName("feedbackType")
        val feedbackType: String?
    ) : Serializable
}