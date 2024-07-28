package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class MyReportedContent(
    @SerializedName("store")
    val store: MyReportedStore? = null,
    @SerializedName("distanceM")
    val distanceM: Int? = 0,
    @SerializedName("visits")
    val visits: MyReportedVisits? = null,
    @SerializedName("tags")
    val tags: Tags? = null
)

data class MyReportedStore(
    @SerializedName("storeId")
    val storeId: Int? = 0,
    @SerializedName("isOwner")
    val isOwner: Boolean? = false,
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("salesType")
    val salesType: String? = "",
    @SerializedName("rating")
    val rating: Int? = 0,
    @SerializedName("location")
    val location: Location? = null,
    @SerializedName("address")
    val address: Address? = null,
    @SerializedName("categories")
    val categories: List<Category>? = emptyList(),
    @SerializedName("appearanceDays")
    val appearanceDays: List<String>? = emptyList(),
    @SerializedName("openingHours")
    val openingHours: OpeningHours? = null,
    @SerializedName("paymentMethods")
    val paymentMethods: List<String>? = emptyList(),
    @SerializedName("menus")
    val menus: List<MyReportedMenu>? = emptyList(),
    @SerializedName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerializedName("activitiesStatus")
    val activitiesStatus: String? = "",
    @SerializedName("createdAt")
    val createdAt: String? = "",
    @SerializedName("updatedAt")
    val updatedAt: String? = ""
)

data class MyReportedMenu(
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("price")
    val price: String? = "",
    @SerializedName("category")
    val category: Category? = null
)

data class MyReportedVisits(
    @SerializedName("count")
    val count: MyReportedCount? = null
)

data class MyReportedCount(
    @SerializedName("existsCounts")
    val existsCounts: Int? = 0,
    @SerializedName("notExistsCounts")
    val notExistsCounts: Int? = 0,
    @SerializedName("isCertified")
    val isCertified: Boolean? = false
)