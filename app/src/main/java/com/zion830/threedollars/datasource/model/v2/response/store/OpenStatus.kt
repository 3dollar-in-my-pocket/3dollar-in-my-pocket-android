package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class OpenStatus(
    @SerializedName("openStartDateTime")
    val openStartDateTime: String?,
    @SerializedName("status")
    val status: String?
)