package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName


data class Store(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("storeName")
    val storeName: String = ""
)