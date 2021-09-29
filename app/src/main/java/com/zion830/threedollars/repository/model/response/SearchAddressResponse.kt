package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class SearchAddressResponse(
    @SerializedName("addresses")
    val addresses: List<Addresse>? = listOf(),
    @SerializedName("errorMessage")
    val errorMessage: String = "",
    @SerializedName("meta")
    val meta: Meta = Meta(),
    @SerializedName("status")
    val status: String = ""
)