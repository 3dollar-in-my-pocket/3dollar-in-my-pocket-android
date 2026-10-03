package com.threedollar.domain.store.model

import com.threedollar.domain.home.data.store.CategoryModel

/**
 * 메뉴판 사진에서 AI 가 인식한 메뉴 하나. 수량·가격은 인식하지 못하면 null 이다.
 */
data class ExtractedStoreMenuModel(
    val name: String,
    val count: Int?,
    val price: Int?,
    val category: CategoryModel,
)
