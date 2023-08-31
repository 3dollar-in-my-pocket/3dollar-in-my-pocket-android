package com.zion830.threedollars.datasource.model.v4.report


import com.google.gson.annotations.SerializedName

data class Reason(
    @SerializedName("description")
    val description: String = "",
    @SerializedName("hasReasonDetail")
    val hasReasonDetail: Boolean = false,
    @SerializedName("type")
    val type: String = "",
)