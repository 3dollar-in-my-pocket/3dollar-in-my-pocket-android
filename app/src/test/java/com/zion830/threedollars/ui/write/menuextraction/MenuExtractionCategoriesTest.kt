package com.zion830.threedollars.ui.write.menuextraction

import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.threedollar.domain.store.model.ExtractedStoreMenuModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MenuExtractionCategoriesTest {

    private val cafe = CategoryModel(categoryId = "CAFE", name = "카페/디저트")
    private val etc = CategoryModel(categoryId = "ETC", name = "기타")

    private fun menu(name: String, category: CategoryModel, price: Int? = null, count: Int? = null) =
        ExtractedStoreMenuModel(name = name, count = count, price = price, category = category)

    // TH-1333 TC7
    @Test
    fun `TH1333_TC7_인식된_메뉴를_처음_나온_순서대로_카테고리별로_묶는다`() {
        // Given
        val menus = listOf(
            menu("아메리카노", cafe, price = 4000),
            menu("쿠키", etc, price = 3000),
            menu("라떼", cafe, price = 4500),
        )

        // When
        val categories = menus.toSelectCategories()

        // Then
        assertEquals(listOf("CAFE", "ETC"), categories.map { it.menuType.categoryId })
        assertEquals(listOf("아메리카노", "라떼"), categories.first().menuDetail?.map { it.name })
    }

    // TH-1333 TC8
    @Test
    fun `TH1333_TC8_카테고리_탭의_메뉴에_이름_수량_가격을_미리_채운다`() {
        // Given
        val menus = listOf(menu("붕어빵", cafe, price = 2000, count = 5))

        // When
        val filled = menus.toSelectCategories().single().menuDetail!!.single()

        // Then
        assertEquals("붕어빵", filled.name)
        assertEquals("2000", filled.price)
        assertEquals(5, filled.count)
        assertEquals("CAFE", filled.category.categoryId)
    }

    // TH-1333 TC9
    @Test
    fun `TH1333_TC9_수량_가격이_없으면_빈값으로_둔다`() {
        // Given
        val menus = listOf(menu("붕어빵", cafe))

        // When
        val filled = menus.toSelectCategories().single().menuDetail!!.single()

        // Then
        assertEquals("", filled.price)
        assertNull(filled.count)
    }

    // TH-1333 TC10
    @Test
    fun `TH1333_TC10_메뉴를_삭제하면_사라지고_메뉴추가하면_빈_항목이_붙는다`() {
        // Given
        val categories = listOf(menu("아메리카노", cafe), menu("라떼", cafe)).toSelectCategories()

        // When
        val removed = categories.removeMenu(categoryId = "CAFE", menuIndex = 0)
        val added = removed.addEmptyMenu(categoryId = "CAFE")

        // Then
        assertEquals(listOf("라떼"), removed.single().menuDetail?.map { it.name })
        assertEquals(listOf("라떼", ""), added.single().menuDetail?.map { it.name })
    }

    // TH-1333 TC11
    @Test
    fun `TH1333_TC11_가격이_0이면_최소가격_오류를_낸다`() {
        // Given
        val categories = listOf(menu("아메리카노", cafe, price = 4000)).toSelectCategories()
            .updateMenu(categoryId = "CAFE", menuIndex = 0, name = "아메리카노", price = "0", count = null)

        // When
        val error = categories.findMenuInputError()

        // Then
        assertEquals(MenuInputError.MIN_PRICE, error)
    }

    // TH-1333 TC11
    @Test
    fun `TH1333_TC11_수량이_0이면_최소수량_오류를_내고_빈값은_통과한다`() {
        // Given
        val zeroCount = listOf(menu("아메리카노", cafe, count = 0)).toSelectCategories()
        val empty = listOf(menu("아메리카노", cafe)).toSelectCategories()

        // When
        val zeroCountError = zeroCount.findMenuInputError()
        val emptyError = empty.findMenuInputError()

        // Then
        assertEquals(MenuInputError.MIN_COUNT, zeroCountError)
        assertNull(emptyError)
    }

    // TH-1333 TC12
    @Test
    fun `TH1333_TC12_인식결과_카테고리는_최대_10개까지만_남긴다`() {
        // Given
        val categories = (1..12).map { index ->
            SelectCategoryModel(
                menuType = CategoryModel(categoryId = "C$index"),
                menuDetail = listOf(UserStoreMenuModel(name = "메뉴$index")),
            )
        }

        // When
        val limited = categories.limitToMaxCategories()

        // Then
        assertEquals(MAX_MENU_CATEGORY_COUNT, limited.size)
        assertEquals("C10", limited.last().menuType.categoryId)
    }

    private fun selected(category: CategoryModel, vararg menus: UserStoreMenuModel) =
        SelectCategoryModel(menuType = category, menuDetail = menus.map { it.copy(category = category) })

    // TH-1333 TC19 (TH-1439)
    @Test
    fun `TH1333_TC19_등록하면_기존메뉴는_유지되고_인식메뉴가_추가된다`() {
        // Given
        val existing = listOf(selected(etc, UserStoreMenuModel(name = "기존 메뉴", price = "1000")))
        val extracted = listOf(menu("아메리카노", cafe, price = 4000), menu("쿠키", etc, price = 3000)).toSelectCategories()

        // When
        val merged = existing.mergeExtracted(extracted)

        // Then
        assertEquals(listOf("ETC", "CAFE"), merged.map { it.menuType.categoryId })
        assertEquals(listOf("기존 메뉴", "쿠키"), merged.first().menuDetail?.map { it.name })
        assertEquals("1000", merged.first().menuDetail?.first()?.price)
        assertEquals("CAFE", merged.firstExtractedCategoryId(extracted))
    }

    // TH-1333 TC20 (TH-1439)
    @Test
    fun `TH1333_TC20_이름이_같은_메뉴는_수량과_가격만_갱신된다`() {
        // Given
        val existing = listOf(selected(cafe, UserStoreMenuModel(name = "아메 리카노", price = "3500", count = 2)))
        val extracted = listOf(menu("아메리카노", cafe, price = 4000)).toSelectCategories()

        // When
        val menus = existing.mergeExtracted(extracted).single().menuDetail.orEmpty()

        // Then
        assertEquals(1, menus.size)
        assertEquals("4000", menus.single().price)
        assertEquals(2, menus.single().count)
    }

    // TH-1333 TC21 (TH-1439)
    @Test
    fun `TH1333_TC21_빈_메뉴_입력칸은_인식메뉴가_들어오면_정리된다`() {
        // Given
        val existing = listOf(selected(cafe, UserStoreMenuModel()))
        val extracted = listOf(menu("아메리카노", cafe, price = 4000)).toSelectCategories()

        // When
        val menus = existing.mergeExtracted(extracted).single().menuDetail.orEmpty()

        // Then
        assertEquals(listOf("아메리카노"), menus.map { it.name })
    }

    // TH-1333 TC22 (TH-1439)
    @Test
    fun `TH1333_TC22_기존_카테고리와_합쳐도_카테고리는_최대_10개다`() {
        // Given
        val existing = (0 until 9).map { selected(CategoryModel(categoryId = "EXISTING_$it")) }
        val extracted = listOf(menu("아메리카노", cafe), menu("쿠키", etc)).toSelectCategories()

        // When
        val merged = existing.mergeExtracted(extracted)

        // Then
        assertEquals(MAX_MENU_CATEGORY_COUNT, merged.size)
        assertEquals(existing.map { it.menuType.categoryId }, merged.take(9).map { it.menuType.categoryId })
        assertEquals("CAFE", merged.last().menuType.categoryId)
        assertEquals("CAFE", merged.firstExtractedCategoryId(extracted))
    }

    // TH-1333 TC23 (TH-1439)
    @Test
    fun `TH1333_TC23_카테고리_화면에서_고른_카테고리와_합쳐진다`() {
        // Given
        val selectedOnly = listOf(selected(CategoryModel(categoryId = "SELECTED"), UserStoreMenuModel()))
        val extracted = listOf(menu("아메리카노", cafe), menu("쿠키", etc)).toSelectCategories()

        // When
        val merged = selectedOnly.mergeExtracted(extracted)

        // Then
        assertEquals(listOf("SELECTED", "CAFE", "ETC"), merged.map { it.menuType.categoryId })
        assertEquals(listOf("아메리카노"), merged[1].menuDetail?.map { it.name })
    }

    @Test
    fun `인식값이_비어있으면_같은_이름의_기존_수량과_가격을_유지한다`() {
        // Given
        val existing = listOf(selected(cafe, UserStoreMenuModel(name = "아메리카노", price = "3500", count = 2)))
        val extracted = listOf(menu("아메리카노", cafe)).toSelectCategories()

        // When
        val updated = existing.mergeExtracted(extracted).single().menuDetail.orEmpty().single()

        // Then
        assertEquals("3500", updated.price)
        assertEquals(2, updated.count)
    }

    @Test
    fun `수량과_가격이_모두_0이면_수량_오류를_먼저_알린다`() {
        // Given
        val categories = listOf(menu("아메리카노", cafe, price = 0, count = 0)).toSelectCategories()

        // When
        val error = categories.findMenuInputError()

        // Then
        assertEquals(MenuInputError.MIN_COUNT, error)
    }
}
