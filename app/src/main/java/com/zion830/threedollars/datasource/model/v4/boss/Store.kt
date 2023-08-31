package com.zion830.threedollars.datasource.model.v4.boss


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.common.Address
import com.zion830.threedollars.datasource.model.v4.common.Category
import com.zion830.threedollars.datasource.model.v4.common.Location

data class Store(
    @SerializedName("address")
    val address: Address = Address(),
    @SerializedName("appearanceDays")
    val appearanceDays: List<AppearanceDay> = listOf(),
    @SerializedName("categories")
    val categories: List<Category> = listOf(),
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("introduction")
    val introduction: String = "",
    @SerializedName("location")
    val location: Location = Location(),
    @SerializedName("menus")
    val menus: List<Menu> = listOf(),
    @SerializedName("name")
    val name: String = "",
    @SerializedName("snsUrl")
    val snsUrl: String = "",
    @SerializedName("storeId")
    val storeId: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
)