package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("contents")
    val contents: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("rating")
    val rating: Float? = 0.0f,
    @SerializedName("reviewId")
    val reviewId: Int? = 0,
    @SerializedName("status")
    val status: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = "",
    @SerializedName("isOwner")
    val isOwner: Boolean? = false,
    @SerializedName("images")
    val images: List<RepresentativeImage> = listOf(),
)