package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class StoreMenuExtractionListResponse(
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("menus")
    val menus: List<StoreMenuExtractionResponse>? = null,
)

data class StoreMenuExtractionResponse(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("count")
    val count: Int? = null,
    @SerializedName("price")
    val price: Int? = null,
    @SerializedName("category")
    val category: StoreMenuExtractionCategoryResponse? = null,
)

data class StoreMenuExtractionCategoryResponse(
    @SerializedName("categoryId")
    val categoryId: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("disableImageUrl")
    val disableImageUrl: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("isNew")
    val isNew: Boolean? = null,
    @SerializedName("classification")
    val classification: StoreMenuExtractionClassificationResponse? = null,
)

data class StoreMenuExtractionClassificationResponse(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("description")
    val description: String? = null,
)
