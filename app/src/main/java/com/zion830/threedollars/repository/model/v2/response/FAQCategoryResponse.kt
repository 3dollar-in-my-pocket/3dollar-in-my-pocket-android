package com.zion830.threedollars.repository.model.v2.response


import com.google.gson.annotations.SerializedName

data class FAQCategoryResponse(
    @SerializedName("data")
    val data: List<FAQCategory> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

data class FAQCategory(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("displayOrder")
    val displayOrder: Int = 0
)