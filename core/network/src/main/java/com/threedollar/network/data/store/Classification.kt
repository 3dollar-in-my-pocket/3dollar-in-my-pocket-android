package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Classification(
    @SerializedName("description")
    val description: String = "",
    @SerializedName("type")
    val type: String = ""
)