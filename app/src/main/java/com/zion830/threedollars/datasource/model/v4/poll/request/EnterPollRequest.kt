package com.zion830.threedollars.datasource.model.v4.poll.request

import com.google.gson.annotations.SerializedName

data class EnterPollRequest(
    @SerializedName("optionIds")
    val optionIds: List<String>,
)