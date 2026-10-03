package com.threedollar.data.store

import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.ClassificationModel
import com.threedollar.domain.store.model.ExtractedStoreMenuModel
import com.threedollar.network.data.store.StoreMenuExtractionCategoryResponse
import com.threedollar.network.data.store.StoreMenuExtractionListResponse
import com.threedollar.network.data.store.StoreMenuExtractionResponse

/**
 * 이름이나 카테고리가 비어 등록할 수 없는 메뉴는 버린다.
 */
fun StoreMenuExtractionListResponse.asExtractedMenus(): List<ExtractedStoreMenuModel> =
    menus.orEmpty().mapNotNull { it.asModelOrNull() }

private fun StoreMenuExtractionResponse.asModelOrNull(): ExtractedStoreMenuModel? {
    val menuName = name?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val categoryModel = category?.asModelOrNull() ?: return null
    return ExtractedStoreMenuModel(
        name = menuName,
        count = count,
        price = price,
        category = categoryModel,
    )
}

private fun StoreMenuExtractionCategoryResponse.asModelOrNull(): CategoryModel? {
    val id = categoryId?.takeIf { it.isNotBlank() } ?: return null
    return CategoryModel(
        categoryId = id,
        classificationModel = ClassificationModel(
            description = classification?.description.orEmpty(),
            type = classification?.type.orEmpty(),
        ),
        description = description.orEmpty(),
        disableImageUrl = disableImageUrl.orEmpty(),
        imageUrl = imageUrl.orEmpty(),
        isNew = isNew ?: false,
        name = name.orEmpty(),
    )
}
