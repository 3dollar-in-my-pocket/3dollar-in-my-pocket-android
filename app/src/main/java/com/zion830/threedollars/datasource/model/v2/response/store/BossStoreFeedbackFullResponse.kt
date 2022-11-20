package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.utils.SharedPrefUtils

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
        val ratio: Double
    ) {
        fun feedbackFullModelToReviewModel(): FoodTruckReviewModel {
            val feedbackTypeList = SharedPrefUtils.getFeedbackType()

            val feedbackType = feedbackTypeList.find { feedbackType ->
                feedbackType.feedbackType == this.feedbackType
            }
            return FoodTruckReviewModel(
                count = count,
                feedbackType = this.feedbackType,
                ratio = ratio,
                description = feedbackType?.description,
                emoji = feedbackType?.emoji
            )
        }
    }

    data class FoodTruckReviewModel(
        val count: Int,
        val feedbackType: String?,
        val ratio: Double,
        val description: String?,
        val emoji: String?
    )
}