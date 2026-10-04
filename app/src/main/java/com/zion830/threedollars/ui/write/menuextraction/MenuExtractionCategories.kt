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

/** 인식 결과 화면에 띄울 목록. 카테고리 최대 개수 제한을 지킨다. */
fun List<SelectCategoryModel>.limitToMaxCategories(): List<SelectCategoryModel> = take(MAX_MENU_CATEGORY_COUNT)

/**
 * 인식 결과를 이미 고른 카테고리·입력한 메뉴에 합친다 (iOS `MenuForm.merging` 과 같은 규칙, TH-1439).
 * - 기존 카테고리 순서를 유지하고, 새 카테고리는 뒤에 최대 [MAX_MENU_CATEGORY_COUNT]개까지 붙인다.
 * - 같은 카테고리에 이름(공백·대소문자 무시)·수량·가격이 모두 같은 메뉴가 있으면 중복으로 보고 빼고,
 *   하나라도 다르면 별도 메뉴로 추가한다 (TH-1450, 메뉴판에 같은 메뉴가 묶음별 가격으로 여러 줄 있는 경우).
 * - 인식 메뉴가 들어간 카테고리의 빈 입력 칸은 정리한다.
 */
fun List<SelectCategoryModel>.mergeExtracted(extracted: List<SelectCategoryModel>): List<SelectCategoryModel> {
    val merged = toMutableList()
    extracted.forEach { category ->
        val categoryId = category.menuType.categoryId
        if (merged.size < MAX_MENU_CATEGORY_COUNT && merged.none { it.menuType.categoryId == categoryId }) {
            merged.add(category.copy(menuDetail = emptyList()))
        }
    }
    extracted.forEach { category ->
        val index = merged.indexOfFirst { it.menuType.categoryId == category.menuType.categoryId }
        if (index < 0) return@forEach
        val target = merged[index]
        val menus = target.menuDetail.orEmpty().filterNot { it.isBlankInput() }.toMutableList()
        category.menuDetail.orEmpty().forEach { menu -> menus.addIfNotDuplicate(menu.copy(category = target.menuType)) }
        merged[index] = target.copy(menuDetail = menus)
    }
    return merged
}

/** 합친 뒤 처음 보여줄 탭. 인식된 첫 카테고리, 없으면 첫 카테고리. */
fun List<SelectCategoryModel>.firstExtractedCategoryId(extracted: List<SelectCategoryModel>): String? =
    extracted.firstOrNull { category -> any { it.menuType.categoryId == category.menuType.categoryId } }?.menuType?.categoryId
        ?: firstOrNull()?.menuType?.categoryId

private fun MutableList<UserStoreMenuModel>.addIfNotDuplicate(menu: UserStoreMenuModel) {
    if (none { it.isSameMenu(menu) }) add(menu)
}

private fun UserStoreMenuModel.isSameMenu(other: UserStoreMenuModel): Boolean =
    name.normalizedMenuName() == other.name.normalizedMenuName() &&
        count == other.count &&
        price.orEmpty().trim() == other.price.orEmpty().trim()

private fun String?.normalizedMenuName(): String = orEmpty().lowercase().filterNot { it.isWhitespace() }

private fun UserStoreMenuModel.isBlankInput(): Boolean =
    name.isNullOrBlank() && count == null && price.isNullOrBlank()

/**
 * 입력된 가격·수량 중 0 이하인 값이 있으면 그 오류를 돌려준다. 비어 있는 값은 검사하지 않는다.
 */
fun List<SelectCategoryModel>.findMenuInputError(): MenuInputError? {
    val menus = flatMap { it.menuDetail.orEmpty() }
    if (menus.any { menu -> menu.count?.let { it <= 0 } == true }) return MenuInputError.MIN_COUNT
    if (menus.any { menu -> menu.price?.toLongOrNull()?.let { it <= 0 } == true }) return MenuInputError.MIN_PRICE
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
