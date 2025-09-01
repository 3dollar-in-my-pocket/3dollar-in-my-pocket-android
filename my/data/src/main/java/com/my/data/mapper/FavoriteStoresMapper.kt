package com.my.data.mapper

import com.my.domain.model.FavoriteStoresModel
import com.my.domain.model.FavoriteStoreModel
import com.my.domain.model.CategoryModel
import com.my.domain.model.AddressModel
import com.my.domain.model.CursorModel
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse

object FavoriteStoresMapper {

    fun toDomainModel(response: MyFavoriteFolderResponse): FavoriteStoresModel {
        return FavoriteStoresModel(
            contents = response.favorites.map { favorite ->
                FavoriteStoreModel(
                    storeId = favorite.storeId,
                    storeName = favorite.storeName,
                    storeType = favorite.storeType,
                    categories = favorite.categories.map { category ->
                        CategoryModel(
                            categoryId = category.categoryId,
                            name = category.name,
                            imageUrl = category.imageUrl
                        )
                    },
                    rating = 0.0, // Not available in API response
                    reviewsCount = 0, // Not available in API response
                    isDeleted = favorite.isDeleted,
                    address = AddressModel(
                        fullAddress = "", // Not available in API response
                        district = "" // Not available in API response
                    ),
                    distance = 0 // Not available in API response
                )
            },
            cursor = CursorModel(
                hasMore = response.cursor.hasMore,
                nextCursor = response.cursor.nextCursor
            )
        )
    }
}