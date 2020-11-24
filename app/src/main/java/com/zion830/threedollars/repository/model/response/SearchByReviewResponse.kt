package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName

data class SearchByReviewResponse(
    @SerializedName("storeList0")
    val storeList0: List<StoreList>,
    @SerializedName("storeList1")
    val storeList1: List<StoreList>,
    @SerializedName("storeList2")
    val storeList2: List<StoreList>,
    @SerializedName("storeList3")
    val storeList3: List<StoreList>,
    @SerializedName("storeList4")
    val storeList4: List<StoreList>
)