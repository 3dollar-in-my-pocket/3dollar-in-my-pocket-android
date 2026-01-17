package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CategoriesResponse(
    @SerializedName("data")
    val data: List<CategoriesModel> = emptyList(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = "",
)

data class CategoriesModel(
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

data class Classification(
    @SerializedName("type")
    val type: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("priority")
    val priority: Int = Int.MAX_VALUE,
)