package com.zion830.threedollars.datasource.model.v4.favorite.request

import com.google.gson.annotations.SerializedName

data class FavoriteInfoRequest(
    @SerializedName("introduction")
    val introduction: String,
    @SerializedName("name")
    val name: String
)