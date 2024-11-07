package com.home.domain.data.store

import com.threedollar.common.data.AdAndStoreItem
import java.io.Serializable

data class CategoryModel(
    val categoryId: String = "",
    val classificationModel: ClassificationModel = ClassificationModel(),
    val description: String = "",
    val disableImageUrl: String = "",
    val imageUrl: String = "",
    val isNew: Boolean = false,
    val name: String = "",
    val isSelected: Boolean = false,
) : AdAndStoreItem, Serializable

data class SelectCategoryModel(
    val menuType: CategoryModel = CategoryModel(),
    val menuDetail: List<UserStoreMenuModel>? = listOf(),
) : AdAndStoreItem

data class AddCategoryModel(val isEnabled: Boolean = true) : AdAndStoreItem