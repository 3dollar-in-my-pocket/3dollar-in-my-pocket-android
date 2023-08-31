package com.zion830.threedollars.datasource.model.v4.aroundStore


import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("categoryId")
    val categoryId: String = "",
    @SerializedName("classification")
    val classification: Classification = Classification(),
    @SerializedName("description")
    val description: String = "",
    @SerializedName("disableImageUrl")
    val disableImageUrl: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("isNew")
    val isNew: Boolean = false,
    @SerializedName("name")
    val name: String = "",
)