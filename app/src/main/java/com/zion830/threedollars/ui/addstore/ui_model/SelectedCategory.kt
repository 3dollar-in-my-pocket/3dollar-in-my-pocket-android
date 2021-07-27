package com.zion830.threedollars.ui.addstore.ui_model

import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.response.Menu

data class SelectedCategory(
    val isSelected: Boolean = true,
    val menuType: MenuType = MenuType.BUNGEOPPANG,
    val menuDetail: List<Menu>? = listOf()
)