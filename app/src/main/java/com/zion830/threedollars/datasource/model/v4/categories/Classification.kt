package com.zion830.threedollars.datasource.model.v4.categories

import com.google.gson.annotations.SerializedName

data class Classification(
    @SerializedName("type")
    val type: String = "",
    @SerializedName("description")
    val description: String = "",
)