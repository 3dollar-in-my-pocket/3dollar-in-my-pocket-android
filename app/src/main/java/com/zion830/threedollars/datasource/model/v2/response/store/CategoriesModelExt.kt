package com.zion830.threedollars.datasource.model.v2.response.store

import com.zion830.threedollars.ui.dialog.category.StoreCategory
import com.zion830.threedollars.ui.dialog.category.StoreCategoryClassification
import com.zion830.threedollars.ui.dialog.category.StoreCategoryItem

fun List<CategoriesModel>.toStoreCategories(): List<StoreCategory> =
    groupBy { it.classification }
        .map { (classification, items) ->
            StoreCategory(
                classification = StoreCategoryClassification(
                    type = classification.type,
                    name = classification.description,
                    priority = classification.priority
                ),
                items = items.map { item ->
                    StoreCategoryItem(
                        id = item.categoryId,
                        name = item.name,
                        description = item.description,
                        imageUrl = item.imageUrl,
                        disableImageUrl = item.disableImageUrl,
                        isNew = item.isNew
                    )
                }
            )
        }
        .sortedBy { it.classification.priority }
