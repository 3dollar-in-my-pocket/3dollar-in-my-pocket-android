package com.threedollar.network.data.neighborhood


import com.google.gson.annotations.SerializedName

data class GetPopularStoresResponse(
    @SerializedName("contents")
    val contents: List<Content>? = null,
    @SerializedName("cursor")
    val cursor: Cursor? = null
) {
    data class Content(
        @SerializedName("account")
        val account: Account? = null,
        @SerializedName("address")
        val address: Address? = null,
        @SerializedName("categories")
        val categories: List<Category>? = null,
        @SerializedName("createdAt")
        val createdAt: String? = null, // 2023-10-22T13:32:51
        @SerializedName("isDeleted")
        val isDeleted: Boolean? = null, // true
        @SerializedName("isOwner")
        val isOwner: Boolean? = null, // true
        @SerializedName("location")
        val location: Location? = null,
        @SerializedName("storeId")
        val storeId: String? = null, // string
        @SerializedName("storeName")
        val storeName: String? = null, // string
        @SerializedName("storeType")
        val storeType: String? = null, // USER_STORE
        @SerializedName("updatedAt")
        val updatedAt: String? = null // 2023-10-22T13:32:51
    ) {
        data class Account(
            @SerializedName("accountId")
            val accountId: String? = null, // string
            @SerializedName("accountType")
            val accountType: String? = null // BOSS_ACCOUNT
        )

        data class Address(
            @SerializedName("fullAddress")
            val fullAddress: String? = null // string
        )

        data class Category(
            @SerializedName("categoryId")
            val categoryId: String? = null, // BUNGEOPPANG
            @SerializedName("classification")
            val classification: Classification? = null,
            @SerializedName("description")
            val description: String? = null, // string
            @SerializedName("imageUrl")
            val imageUrl: String? = null, // string
            @SerializedName("isNew")
            val isNew: Boolean? = null, // true
            @SerializedName("name")
            val name: String? = null // string
        ) {
            data class Classification(
                @SerializedName("description")
                val description: String? = null, // string
                @SerializedName("type")
                val type: String? = null // MEAL
            )
        }

        data class Location(
            @SerializedName("latitude")
            val latitude: Int? = null, // 0
            @SerializedName("longitude")
            val longitude: Int? = null // 0
        )
    }

    data class Cursor(
        @SerializedName("hasMore")
        val hasMore: Boolean? = null, // true
        @SerializedName("nextCursor")
        val nextCursor: String? = null // string
    )
}