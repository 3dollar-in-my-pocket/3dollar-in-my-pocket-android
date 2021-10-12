package com.zion830.threedollars.repository.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class StoreByDistance(
    @SerializedName("storeList100")
    val storeList100: List<StoreList> = listOf(),
    @SerializedName("storeList1000")
    val storeList1000: List<StoreList> = listOf(),
    @SerializedName("storeList50")
    val storeList50: List<StoreList> = listOf(),
    @SerializedName("storeList500")
    val storeList500: List<StoreList> = listOf(),
    @SerializedName("storeListOver1000")
    val storeListOver1000: List<StoreList> = listOf()
) {

    fun isNotEmpty() =
        !(storeList100.isEmpty() && storeList1000.isEmpty() && storeList50.isEmpty() && storeList500.isEmpty() && storeListOver1000.isEmpty())

    fun getLongestStore() = storeList1000 + storeListOver1000

    fun getAllStores() = storeList100 + storeList50 + storeList500 + storeList1000 + storeListOver1000
}