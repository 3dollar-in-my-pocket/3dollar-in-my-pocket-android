package com.zion830.threedollars.datasource.model

import com.zion830.threedollars.datasource.model.v2.response.store.CategoryInfo
import com.zion830.threedollars.datasource.model.v2.response.store.Menu

data class Category(
    val category: CategoryInfo,
    val menu: List<Menu>? = emptyList()
)