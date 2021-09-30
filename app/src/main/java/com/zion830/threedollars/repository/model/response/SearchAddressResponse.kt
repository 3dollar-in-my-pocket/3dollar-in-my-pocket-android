package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class SearchAddressResponse(
    @SerializedName("documents")
    val documents: List<Document> = listOf(),
    @SerializedName("meta")
    val meta: MetaX = MetaX()
)