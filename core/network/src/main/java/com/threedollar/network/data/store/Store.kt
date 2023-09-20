package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

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
    val updatedAt: String = ""
)