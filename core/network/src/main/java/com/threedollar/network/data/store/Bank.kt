package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Bank(
    @SerializedName("key")
    val key: String? = "",
    @SerializedName("description")
    val description: String? = "",
)
