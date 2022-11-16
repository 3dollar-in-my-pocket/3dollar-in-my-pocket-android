package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("resultCode")
    val resultCode: String = "",
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val data: List<CategoryInfo> = listOf()
)