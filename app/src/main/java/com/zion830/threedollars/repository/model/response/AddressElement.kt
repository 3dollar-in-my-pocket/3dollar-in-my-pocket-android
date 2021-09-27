package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class AddressElement(
    @SerializedName("code")
    val code: String = "",
    @SerializedName("longName")
    val longName: String = "",
    @SerializedName("shortName")
    val shortName: String = "",
    @SerializedName("types")
    val types: List<String> = listOf()
)