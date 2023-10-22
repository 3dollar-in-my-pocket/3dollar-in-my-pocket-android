package com.threedollar.domain.data

data class PopularStore(
    val account: Account,
    val address: Address,
    val categories: List<Category>,
    val createdAt: String, // 2023-10-22T13:32:51
    val isDeleted: Boolean, // true
    val isOwner: Boolean, // true
    val location: Location,
    val storeId: String, // string
    val storeName: String, // string
    val storeType: String, // USER_STORE
    val updatedAt: String // 2023-10-22T13:32:51
) {
    data class Account(
        val accountId: String, // string
        val accountType: String // BOSS_ACCOUNT
    )

    data class Address(
        val fullAddress: String // string
    )

    data class Category(
        val categoryId: String, // BUNGEOPPANG
        val classification: Classification,
        val description: String, // string
        val imageUrl: String, // string
        val isNew: Boolean, // true
        val name: String // string
    ) {
        data class Classification(
            val description: String, // string
            val type: String // MEAL
        )
    }

    data class Location(
        val latitude: Int, // 0
        val longitude: Int // 0
    )
}