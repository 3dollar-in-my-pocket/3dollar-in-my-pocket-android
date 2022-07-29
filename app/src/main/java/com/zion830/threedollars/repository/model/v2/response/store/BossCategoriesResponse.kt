package com.zion830.threedollars.repository.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class BossCategoriesResponse(
    @SerializedName("data")
    val data: List<BossCategoriesModel>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("resultCode")
    val resultCode: String?
) {
    data class BossCategoriesModel(
        @SerializedName("categoryId")
        val categoryId: String? = "All",
        @SerializedName("name")
        val name: String? = "전체"
    )
}