package com.zion830.threedollars.ui.addstore.ui_model

import com.home.domain.data.store.CategoryModel
import com.home.domain.data.store.UserStoreMenuModel

data class SelectedCategory(
    val isSelected: Boolean = true,
    val menuType: CategoryModel = CategoryModel(),
    val menuDetail: List<UserStoreMenuModel>? = listOf()
)