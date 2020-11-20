package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName


data class MyStoreResponse(
    @SerializedName("content")
    val store: List<Store>? = listOf(),
    @SerializedName("totalElements")
    val totalElements: Int = 0,
    @SerializedName("totalPages")
    val totalPages: Int = 0
)