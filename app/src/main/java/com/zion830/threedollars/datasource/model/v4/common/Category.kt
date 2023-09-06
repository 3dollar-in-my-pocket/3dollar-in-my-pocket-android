package com.zion830.threedollars.datasource.model.v4.common


import com.google.gson.annotations.SerializedName

data class Category(
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