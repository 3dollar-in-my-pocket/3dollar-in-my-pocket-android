package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class PostUserStoreResponse(
    @SerializedName("storeId")
    val storeId: Int? = 0,
    @SerializedName("isOwner")
    val isOwner: Boolean? = false,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("salesTypeV2")
    val salesTypeV2: SalesTypeV2? = SalesTypeV2(),
    @SerializedName("rating")
    val rating: Double? = 0.0,
    @SerializedName("location")
    val location: Location? = Location(),
    @SerializedName("address")
    val address: Address = Address(),
    @SerializedName("categories")
    val categories: List<Category>? = listOf(),
    @SerializedName("appearanceDays")
    val appearanceDays: List<String>? = listOf(),
    @SerializedName("openingHours")
    val openingHours: OpeningHours? = OpeningHours(),
    @SerializedName("paymentMethods")
    val paymentMethods: List<String>? = listOf(),
    @SerializedName("menusV3")
    val menusV3: List<MenuV2>? = listOf(),
    @SerializedName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerializedName("activitiesStatus")
    val activitiesStatus: String? = "",
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = ""
)