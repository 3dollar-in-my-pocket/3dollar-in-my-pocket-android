package com.zion830.threedollars.datasource.model.v4.poll


import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("commentId")
    val commentId: String = "",
    @SerializedName("content")
    val content: String = "",
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
)