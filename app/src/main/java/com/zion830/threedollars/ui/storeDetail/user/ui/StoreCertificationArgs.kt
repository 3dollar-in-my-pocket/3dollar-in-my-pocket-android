package com.zion830.threedollars.ui.storeDetail.user.ui

import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.LocationModel
import com.threedollar.domain.home.data.store.UserStoreModel
import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.model.SDChipModel
import java.io.Serializable

data class StoreCertificationArgs(
    val storeId: Int,
    val storeName: String,
    val latitude: Double,
    val longitude: Double,
    val categories: List<StoreCertificationCategoryArgs> = emptyList(),
) : Serializable {
    fun toUserStoreModel(): UserStoreModel {
        return UserStoreModel(
            storeId = storeId,
            name = storeName,
            location = LocationModel(
                latitude = latitude,
                longitude = longitude,
            ),
            categories = categories.map { category ->
                CategoryModel(
                    name = category.name,
                    imageUrl = category.imageUrl,
                )
            },
        )
    }
}

data class StoreCertificationCategoryArgs(
    val name: String,
    val imageUrl: String = "",
) : Serializable

internal fun List<SDChipModel>.storeCertificationCategories(): List<StoreCertificationCategoryArgs> =
    take(1).mapNotNull { chip ->
        chip.text.displayText().takeIf(String::isNotBlank)?.let { name ->
            StoreCertificationCategoryArgs(name, chip.image?.url.orEmpty())
        }
    }
