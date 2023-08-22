package com.zion830.threedollars.ui.addstore.ui_model

import com.zion830.threedollars.datasource.model.v2.request.MyMenu
import com.zion830.threedollars.datasource.model.v2.response.store.CategoriesModel

data class SelectedCategory(
    val isSelected: Boolean = true,
    val menuType: CategoriesModel = CategoriesModel(),
    val menuDetail: List<MyMenu>? = listOf()
)