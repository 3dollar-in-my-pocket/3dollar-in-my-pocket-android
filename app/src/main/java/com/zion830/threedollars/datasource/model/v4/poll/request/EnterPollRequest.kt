package com.zion830.threedollars.datasource.model.v4.poll.request

import com.google.gson.annotations.SerializedName

data class EnterPollRequest(
    @SerializedName("options")
    val options: List<OptionsRequest>,
)

data class OptionsRequest(
    @SerializedName("optionId")
    val optionId: String,
)