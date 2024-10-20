package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class Store(
    @SerializedName("storeType")
    val storeType: String? = "",
    @SerializedName("storeId")
    val storeId: String? = "",
    @SerializedName("isOwner")
    val isOwner: Boolean = false,
    @SerializedName("account")
    val account: Account? = Account(),
    @SerializedName("storeName")
    val storeName: String? = "",
    @SerializedName("address")
    val address: Address? = Address(),
    @SerializedName("location")
    val location: Location? = Location(),
    @SerializedName("categories")
    val categories: List<Category>? = listOf(),
    @SerializedName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerializedName("activitiesStatus")
    val activitiesStatus: String? = "",
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = "",
)