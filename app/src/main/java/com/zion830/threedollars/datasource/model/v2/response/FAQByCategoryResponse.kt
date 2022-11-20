package com.zion830.threedollars.datasource.model.v2.response


import com.google.gson.annotations.SerializedName

data class FAQByCategoryResponse(
    @SerializedName("data")
    val data: List<FAQ> = listOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("resultCode")
    val resultCode: String = ""
)

data class FAQ(
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