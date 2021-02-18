package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class SearchByDistanceResponse(
    @SerializedName("storeList100")
    val storeList100: List<StoreList> = listOf(),
    @SerializedName("storeList1000")
    val storeList1000: List<StoreList> = listOf(),
    @SerializedName("storeList50")
    val storeList50: List<StoreList> = listOf(),
    @SerializedName("storeList500")
    val storeList500: List<StoreList> = listOf()
) {

    fun isNotEmpty() = storeList100.size + storeList1000.size + storeList50.size + storeList500.size > 0

    fun getAllStores() = storeList100 + storeList50 + storeList1000 + storeList500
}