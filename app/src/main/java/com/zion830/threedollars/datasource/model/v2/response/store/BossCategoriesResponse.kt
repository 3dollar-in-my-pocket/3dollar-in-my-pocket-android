package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.BuildConfig

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
        val categoryId: String? = if (BuildConfig.DEBUG) {
            "622b7d0105ecea5baeafd245"
        } else {
            "628a42eb899ec19e976e54d7"
        },
        @SerializedName("name")
        val name: String = "한식",
        @SerializedName("imageUrl")
        val imageUrl: String? = "https://storage.threedollars.co.kr/menu/icon_menu_3x_bab.png",
        @SerializedName("description")
        val description: String = "한식 만나기 30초 전"
    )
}