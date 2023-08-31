package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.common.Cursor

data class UserAddStoreResponse(
    @SerializedName("contents")
    val userAddStoreModels: List<UserAddStoreModel> = listOf(),
    @SerializedName("cursor")
    val cursor: Cursor = Cursor(),
)