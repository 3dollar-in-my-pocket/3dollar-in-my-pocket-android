package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class SearchByReviewResponse(
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

    fun isNotEmpty() = storeList0.size + storeList1.size + storeList2.size + storeList3.size + storeList4.size > 0

    fun getStoresOver3() = storeList3 + storeList4

    fun getAllStores() = storeList0 + storeList1 + storeList2 + storeList3 + storeList4
}