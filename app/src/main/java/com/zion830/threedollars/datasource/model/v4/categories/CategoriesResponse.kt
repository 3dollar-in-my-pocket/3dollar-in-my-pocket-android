package com.zion830.threedollars.datasource.model.v4.categories


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoriesResponse(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("categoryId")
    val categoryId: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("disbleImageUrl")
    val disableImageUrl: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("classification")
    val classification: Classification = Classification(),
    @SerializedName("isNew")
    val isNew: Boolean = false,
    var isSelected: Boolean = false,
) : Serializable