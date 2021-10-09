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
)