package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class FaqByTag(
    @SerializedName("answer")
    val answer: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("question")
    val question: String,
    @SerializedName("tags")
    val tags: List<FaqTag>,
    @SerializedName("updatedAt")
    val updatedAt: String
)