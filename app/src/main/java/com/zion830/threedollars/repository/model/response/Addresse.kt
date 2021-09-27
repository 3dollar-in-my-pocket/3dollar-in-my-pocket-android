package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class Addresse(
    @SerializedName("addressElements")
    val addressElements: List<AddressElement> = listOf(),
    @SerializedName("distance")
    val distance: Double = 0.0,
    @SerializedName("englishAddress")
    val englishAddress: String = "",
    @SerializedName("jibunAddress")
    val jibunAddress: String = "",
    @SerializedName("roadAddress")
    val roadAddress: String = "",
    @SerializedName("x")
    val x: String = "",
    @SerializedName("y")
    val y: String = ""
)