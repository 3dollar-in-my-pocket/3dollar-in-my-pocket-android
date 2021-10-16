package com.zion830.threedollars.ui.addstore.ui_model

import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.request.MyMenu
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo

data class SelectedCategory(
    val isSelected: Boolean = true,
    val menuType: CategoryInfo = CategoryInfo(),
    val menuDetail: List<MyMenu>? = listOf()
)