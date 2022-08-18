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
        val categoryId: String? = "622b7d0105ecea5baeafd245",
        @SerializedName("name")
        val name: String? = "한식",
        @SerializedName("imageUrl")
        val imageUrl: String? = "https://storage.threedollars.co.kr/menu/icon_menu_3x_bab.png"
    )
}