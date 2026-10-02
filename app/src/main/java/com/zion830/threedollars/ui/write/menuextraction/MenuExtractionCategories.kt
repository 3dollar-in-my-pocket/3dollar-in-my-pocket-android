package com.zion830.threedollars.ui.write.menuextraction

import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.threedollar.domain.store.model.ExtractedStoreMenuModel

/** 가게 제보·정보 수정 공통 카테고리 최대 개수. */
const val MAX_MENU_CATEGORY_COUNT = 10

enum class MenuInputError { MIN_PRICE, MIN_COUNT }

/**
 * AI 가 인식한 메뉴를 카테고리별로 묶는다. 카테고리 순서는 응답에서 처음 나온 순서를 따른다.
 * 인식하지 못한 수량·가격은 빈 값으로 둔다.
 */
fun List<ExtractedStoreMenuModel>.toSelectCategories(): List<SelectCategoryModel> =
    groupBy { it.category.categoryId }
        .map { (_, menus) ->
            val category = menus.first().category
            SelectCategoryModel(
                menuType = category,
                menuDetail = menus.map { menu ->
                    UserStoreMenuModel(
                        category = category,
                        name = menu.name,
                        price = menu.price?.toString().orEmpty(),
                        count = menu.count,
                    )
                },
            )
        }

/** 제보·정보 수정 화면에 덮어쓸 목록. 카테고리 최대 개수 제한을 지킨다. */
fun List<SelectCategoryModel>.limitToMaxCategories(): List<SelectCategoryModel> = take(MAX_MENU_CATEGORY_COUNT)

/**
 * 입력된 가격·수량 중 0 이하인 값이 있으면 그 오류를 돌려준다. 비어 있는 값은 검사하지 않는다.
 */
fun List<SelectCategoryModel>.findMenuInputError(): MenuInputError? {
    val menus = flatMap { it.menuDetail.orEmpty() }
    if (menus.any { menu -> menu.price?.toLongOrNull()?.let { it <= 0 } == true }) return MenuInputError.MIN_PRICE
    if (menus.any { menu -> menu.count?.let { it <= 0 } == true }) return MenuInputError.MIN_COUNT
    return null
}

fun List<SelectCategoryModel>.addEmptyMenu(categoryId: String): List<SelectCategoryModel> = map { category ->
    if (category.menuType.categoryId != categoryId) return@map category
    category.copy(menuDetail = category.menuDetail.orEmpty() + UserStoreMenuModel(category = category.menuType))
}

fun List<SelectCategoryModel>.removeMenu(categoryId: String, menuIndex: Int): List<SelectCategoryModel> = map { category ->
    if (category.menuType.categoryId != categoryId) return@map category
    category.copy(menuDetail = category.menuDetail.orEmpty().filterIndexed { index, _ -> index != menuIndex })
}

fun List<SelectCategoryModel>.updateMenu(
    categoryId: String,
    menuIndex: Int,
    name: String,
    price: String,
    count: Int?,
): List<SelectCategoryModel> = map { category ->
    if (category.menuType.categoryId != categoryId) return@map category
    category.copy(
        menuDetail = category.menuDetail.orEmpty().mapIndexed { index, menu ->
            if (index == menuIndex) menu.copy(name = name, price = price, count = count) else menu
        },
    )
}
