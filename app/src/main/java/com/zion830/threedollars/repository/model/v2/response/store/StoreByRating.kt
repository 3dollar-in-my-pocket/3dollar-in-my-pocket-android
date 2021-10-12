package com.zion830.threedollars.repository.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class StoreByRating(
    @SerializedName("storeList0")
    val storeList0: List<StoreList> = listOf(),
    @SerializedName("storeList1")
    val storeList1: List<StoreList> = listOf(),
    @SerializedName("storeList2")
    val storeList2: List<StoreList> = listOf(),
    @SerializedName("storeList3")
    val storeList3: List<StoreList> = listOf(),
    @SerializedName("storeList4")
    val storeList4: List<StoreList> = listOf()
) {
    fun isNotEmpty() = !(storeList0.isEmpty() && storeList1.isEmpty() && storeList2.isEmpty() && storeList3.isEmpty() && storeList4.isEmpty())

    fun getStoresOver3(): List<StoreList> = storeList3 + storeList4

    fun getAllStores() = storeList0 + storeList1 + storeList2 + storeList3 + storeList4
}