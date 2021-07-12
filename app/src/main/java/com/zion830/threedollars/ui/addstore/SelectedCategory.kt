package com.zion830.threedollars.ui.addstore

import com.zion830.threedollars.repository.model.MenuType

data class SelectedCategory(
    private val isSelected: Boolean = true,
    private val menuType: MenuType
)