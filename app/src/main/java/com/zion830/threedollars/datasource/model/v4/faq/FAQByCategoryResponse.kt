package com.zion830.threedollars.datasource.model.v4.faq


import com.google.gson.annotations.SerializedName

data class FAQByCategoryResponse(
    @SerializedName("answer")
    val answer: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("faqId")
    val faqId: Int = 0,
    @SerializedName("question")
    val question: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = ""
)