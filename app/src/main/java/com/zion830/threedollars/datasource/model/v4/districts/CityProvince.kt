package com.zion830.threedollars.datasource.model.v4.districts


import com.google.gson.annotations.SerializedName

data class CityProvince(
    @SerializedName("description")
    val description: String = "",
    @SerializedName("key")
    val key: String = ""
)