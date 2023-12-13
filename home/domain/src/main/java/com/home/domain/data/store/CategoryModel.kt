package com.home.domain.data.store

import java.io.Serializable

interface CategoryItem
data class CategoryModel(
    val categoryId: String = "",
    val classificationModel: ClassificationModel = ClassificationModel(),
    val description: String = "",
    val disableImageUrl: String = "",
    val imageUrl: String = "",
    val isNew: Boolean = false,
    val name: String = "",
    val isSelected: Boolean = false
) : CategoryItem, Serializable

data class SelectCategoryModel(
    val menuType: CategoryModel = CategoryModel(),
    val menuDetail: List<UserStoreMenuModel>? = listOf()
) : CategoryItem
data class AddCategoryModel(val isEnabled : Boolean = true) : CategoryItem