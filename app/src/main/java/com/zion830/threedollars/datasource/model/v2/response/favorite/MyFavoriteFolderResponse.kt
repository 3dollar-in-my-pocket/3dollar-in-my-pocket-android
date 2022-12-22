package com.zion830.threedollars.datasource.model.v2.response.favorite

import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.datasource.model.v2.response.AdAndStoreItem

data class MyFavoriteFolderResponse(
    @SerializedName("cursor")
    val cursor: MyFavoriteFolderCursorModel = MyFavoriteFolderCursorModel(),
    @SerializedName("favorites")
    val favorites: List<MyFavoriteFolderFavoriteModel> = listOf(),
    @SerializedName("folderId")
    val folderId: String = "",
    @SerializedName("introduction")
    val introduction: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("user")
    val user: MyFavoriteFolderUserModel = MyFavoriteFolderUserModel()
) {
    data class MyFavoriteFolderCategoryModel(
        val category: String,
        val categoryId: String,
        val description: String,
        val imageUrl: String,
        val isNew: Boolean,
        val name: String
    )

    data class MyFavoriteFolderCursorModel(
        val hasMore: Boolean = false,
        val nextCursor: String? = "",
        val totalCount: Int = 0
    )

    data class MyFavoriteFolderFavoriteModel(
        val categories: List<MyFavoriteFolderCategoryModel>,
        val storeId: String,
        val storeName: String,
        val storeType: String,
        val isDeleted: Boolean
    ) : AdAndStoreItem

    data class MyFavoriteFolderMedalModel(
        val createdAt: String = "",
        val disableIconUrl: String = "",
        val iconUrl: String = "",
        val medalId: Int = 0,
        val name: String = "",
        val updatedAt: String = ""
    )

    data class MyFavoriteFolderUserModel(
        val medal: MyFavoriteFolderMedalModel = MyFavoriteFolderMedalModel(),
        val name: String = ""
    )
}
