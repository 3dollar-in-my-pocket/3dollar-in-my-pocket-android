package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class UserStore(
    @SerializedName("address")
    val address: Address? = Address(),
    @SerializedName("appearanceDays")
    val appearanceDays: List<String>? = listOf(),
    @SerializedName("categories")
    val categories: List<Category>? = listOf(),
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("location")
    val location: Location? = Location(),
    @SerializedName("menus")
    val menus: List<UserStoreMenu>? = listOf(),
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("paymentMethods")
    val paymentMethods: List<String>? = listOf(),
    @SerializedName("rating")
    val rating: Double? = 0.0,
    @SerializedName("salesType")
    val salesType: String? = null,
    @SerializedName("storeId")
    val storeId: Int? = 0,
    @SerializedName("updatedAt")
    val updatedAt: String? = "",
    @SerializedName("openingHours")
    val openingHours: OpeningHours? = OpeningHours(),
    @SerializedName("isOwner")
    val isOwner: Boolean? = false,
    @SerializedName("salesTypeV2")
    val salesTypeV2: SalesTypeV2? = null,
    @SerializedName("menusV3")
    val menusV3: List<MenuV3>? = listOf(),
    @SerializedName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerializedName("activitiesStatus")
    val activitiesStatus: String? = null,
)
