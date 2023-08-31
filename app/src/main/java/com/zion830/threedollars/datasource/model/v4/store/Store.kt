package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.categories.CategoriesResponse
import com.zion830.threedollars.datasource.model.v4.common.Address
import com.zion830.threedollars.datasource.model.v4.common.Location

data class Store(
    @SerializedName("address")
    val address: Address = Address(),
    @SerializedName("appearanceDays")
    val appearanceDays: List<String> = listOf(),
    @SerializedName("categories")
    val categories: List<CategoriesResponse> = listOf(),
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("location")
    val location: Location = Location(),
    @SerializedName("menus")
    val menus: List<Menu> = listOf(),
    @SerializedName("name")
    val name: String = "",
    @SerializedName("paymentMethods")
    val paymentMethods: List<String> = listOf(),
    @SerializedName("rating")
    val rating: Int = 0,
    @SerializedName("salesType")
    val salesType: String = "",
    @SerializedName("storeId")
    val storeId: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: String = "",
)