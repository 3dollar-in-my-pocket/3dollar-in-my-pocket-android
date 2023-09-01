package com.zion830.threedollars.datasource.model.v4.storeReview


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.categories.CategoriesResponse
import com.zion830.threedollars.datasource.model.v4.common.Address
import com.zion830.threedollars.datasource.model.v4.common.Location

data class Store(
    @SerializedName("address")
    val address: Address = Address(),
    @SerializedName("categories")
    val categories: List<CategoriesResponse> = listOf(),
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,
    @SerializedName("location")
    val location: Location = Location(),
    @SerializedName("name")
    val name: String = "",
    @SerializedName("reviewRating")
    val reviewRating: Int = 0,
    @SerializedName("salesType")
    val salesType: String = "",
    @SerializedName("storeId")
    val storeId: Int = 0,
    @SerializedName("updatedAt")
    val updatedAt: String = "",
)