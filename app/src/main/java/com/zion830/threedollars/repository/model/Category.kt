package com.zion830.threedollars.repository.model

import com.zion830.threedollars.repository.model.v2.response.store.Menu

data class Category(
    val name: String? = "",
    val menu: List<Menu>? = emptyList()
) {
    val readableName: String = MenuType.of(name).getName()
}