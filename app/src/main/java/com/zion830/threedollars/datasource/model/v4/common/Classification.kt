package com.zion830.threedollars.datasource.model.v4.common


import com.google.gson.annotations.SerializedName

data class Classification(
    @SerializedName("description")
    val description: String = "",
    @SerializedName("type")
    val type: String = "",
)