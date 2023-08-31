package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.ad.AdAndStoreItem

data class BossNearStoreResponse(
    @SerializedName("data")
    val data: List<BossNearStoreModel>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("resultCode")
    val resultCode: String?
){
    data class BossNearStoreModel(
        @SerializedName("bossStoreId")
        val bossStoreId: String,
        @SerializedName("categories")
        val categories: List<BossCategory> = listOf(),
        @SerializedName("createdAt")
        val createdAt: String?,
        @SerializedName("distance")
        val distance: Int,
        @SerializedName("location")
        val location: Location,
        @SerializedName("menus")
        val menus: List<BossStoreMenu>?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("openStatus")
        val openStatus: OpenStatus?,
        @SerializedName("totalFeedbacksCounts")
        val totalFeedbacksCounts: Int?,
        @SerializedName("updatedAt")
        val updatedAt: String?
    ) : AdAndStoreItem
}