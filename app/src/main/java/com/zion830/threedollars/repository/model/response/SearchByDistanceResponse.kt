package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class SearchByDistanceResponse(
    @SerializedName("storeList100")
    val storeList100: List<StoreList100>,
    @SerializedName("storeList1000")
    val storeList1000: List<StoreList1000>,
    @SerializedName("storeList50")
    val storeList50: List<StoreList50>,
    @SerializedName("storeList500")
    val storeList500: List<StoreList500>
)