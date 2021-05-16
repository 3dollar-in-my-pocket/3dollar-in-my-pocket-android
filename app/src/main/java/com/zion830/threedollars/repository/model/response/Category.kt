package com.zion830.threedollars.repository.model.response

import com.zion830.threedollars.repository.model.MenuType

data class Category(
    val name: String? = "",
    val menu: List<Menu>? = emptyList()
) {
    val readableName: String = MenuType.of(name).getName()
}