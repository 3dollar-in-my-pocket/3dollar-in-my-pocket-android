package com.zion830.threedollars.datasource.model

import com.zion830.threedollars.datasource.model.v2.response.store.CategoriesModel
import com.zion830.threedollars.datasource.model.v2.response.store.Menu

data class Category(
    val category: CategoriesModel,
    val menu: List<Menu>? = emptyList()
)