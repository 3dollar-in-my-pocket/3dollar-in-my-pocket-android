package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class BossCategory(
    @SerializedName("categoryId")
    val categoryId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?
)