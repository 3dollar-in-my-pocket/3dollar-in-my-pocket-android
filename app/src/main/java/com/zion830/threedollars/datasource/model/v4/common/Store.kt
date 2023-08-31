package com.zion830.threedollars.datasource.model.v4.common


import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v4.common.Account
import com.zion830.threedollars.datasource.model.v4.common.Address
import com.zion830.threedollars.datasource.model.v4.common.Category
import com.zion830.threedollars.datasource.model.v4.common.Location

data class Store(
    @SerializedName("account")
    val account: Account = Account(),
    @SerializedName("address")
    val address: Address = Address(),
    @SerializedName("categories")
    val categories: List<Category> = listOf(),
    @SerializedName("createdAt")
    val createdAt: String = "",
    @SerializedName("isDeleted")
    val isDeleted: Boolean = false,
    @SerializedName("location")
    val location: Location = Location(),
    @SerializedName("storeId")
    val storeId: String = "",
    @SerializedName("storeName")
    val storeName: String = "",
    @SerializedName("storeType")
    val storeType: String = "",
    @SerializedName("updatedAt")
    val updatedAt: String = "",
)