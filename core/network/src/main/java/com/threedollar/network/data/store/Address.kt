package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("fullAddress")
    val fullAddress: String? = ""
)