package com.zion830.threedollars.datasource.model.v4.faq


import com.google.gson.annotations.SerializedName

data class FAQCategoryResponse(
    @SerializedName("category")
    val category: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("displayOrder")
    val displayOrder: Int = 0
)