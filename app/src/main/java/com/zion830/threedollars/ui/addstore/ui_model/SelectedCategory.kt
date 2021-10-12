package com.zion830.threedollars.ui.addstore.ui_model

import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.request.MyMenu

data class SelectedCategory(
    val isSelected: Boolean = true,
    val menuType: MenuType = MenuType.BUNGEOPPANG,
    val menuDetail: List<MyMenu>? = listOf()
)