package com.zion830.threedollars.datasource.model.v4.common


import com.google.gson.annotations.SerializedName

data class Address(
    @SerializedName("fullAddress")
    val fullAddress: String = "",
)