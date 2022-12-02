package com.zion830.threedollars.datasource.model.v2.request

import com.google.gson.annotations.SerializedName

data class BossStoreFeedbackRequest(
    @SerializedName("feedbackTypes")
    val feedbackTypes: List<String>
)