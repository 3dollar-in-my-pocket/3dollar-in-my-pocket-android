package com.zion830.threedollars.repository.model.v2.response.kakao


import com.google.gson.annotations.SerializedName

data class SearchAddressResponse(
    @SerializedName("meta")
    val meta: Meta = Meta(),
    @SerializedName("documents")
    val documents: List<Document> = listOf()
)