package com.zion830.threedollars.repository.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class StoreByRating(
    @SerializedName("storeList0")
    val storeList100: List<StoreList> = listOf(),
    @SerializedName("storeList1")
    val storeList1000: List<StoreList> = listOf(),
    @SerializedName("storeList2")
    val storeList50: List<StoreList> = listOf(),
    @SerializedName("storeList3")
    val storeList500: List<StoreList> = listOf(),
    @SerializedName("storeList4")
    val storeListOver1000: List<StoreList> = listOf()
)