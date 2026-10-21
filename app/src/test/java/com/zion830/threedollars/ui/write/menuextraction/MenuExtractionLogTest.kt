package com.zion830.threedollars.ui.write.menuextraction

import com.threedollar.common.analytics.EventName
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import org.junit.Assert.assertEquals
import org.junit.Test

class MenuExtractionLogTest {

    private val cafe = CategoryModel(categoryId = "CAFE", name = "카페/디저트")
    private val etc = CategoryModel(categoryId = "ETC", name = "기타")

    private fun category(category: CategoryModel, vararg menus: UserStoreMenuModel) =
        SelectCategoryModel(menuType = category, menuDetail = menus.toList())

    private fun menu(category: CategoryModel, name: String = "", price: String = "", count: Int? = null) =
        UserStoreMenuModel(category = category, name = name, price = price, count = count)

    // TH-1486 TC1
    @Test
    fun `TH1486_TC1_인식결과를_받은_제보가_완료되면_write_detail_complete에_menu_extraction_used가_true로_실린다`() {
        // Given
        val menuExtractionUsed = true

        // When
        val parameters = MenuExtractionLog.writeDetailCompleteParameters(menuExtractionUsed)

        // Then
        assertEquals(mapOf(ParameterName.MENU_EXTRACTION_USED to "true"), parameters)
        assertEquals("write_detail_complete", ScreenName.WRITE_DETAIL_COMPLETE.value)
    }

    // TH-1486 TC2
    @Test
    fun `TH1486_TC2_AI를_쓰지않은_제보가_완료되면_menu_extraction_used가_false로_실린다`() {
        // Given
        val menuExtractionUsed = false

        // When
        val parameters = MenuExtractionLog.writeDetailCompleteParameters(menuExtractionUsed)

        // Then
        assertEquals(mapOf(ParameterName.MENU_EXTRACTION_USED to "false"), parameters)
    }

    // TH-1486 TC3
    @Test
    fun `TH1486_TC3_가게수정에서_AI로_메뉴를_등록하고_수정에_성공하면_edit_store_complete가_edit흐름으로_실린다`() {
        // Given
        val menuExtractionUsed = true
        val editedMenu = true

        // When
        val event = MenuExtractionLog.editStoreCompleteEvent(menuExtractionUsed, editedMenu, MenuExtractionFlow.EDIT)

        // Then
        assertEquals("edit_store_complete", event.name.value)
        assertEquals(EventName.EDIT_STORE_COMPLETE, event.name)
        assertEquals(ScreenName.EDIT_STORE, event.screen)
        assertEquals(
            mapOf(
                ParameterName.MENU_EXTRACTION_USED to "true",
                ParameterName.EDITED_MENU to "true",
                ParameterName.FLOW to "edit",
            ),
            event.extraParameters,
        )
    }

    // TH-1486 TC5
    @Test
    fun `TH1486_TC5_결과화면_등록을_누르면_register_click에_인식메뉴수와_빈칸을_뺀_최종메뉴수가_실린다`() {
        // Given
        val state = MenuExtractionState(
            phase = MenuExtractionState.Phase.RESULT,
            recognizedMenuCount = 3,
            categories = listOf(
                category(cafe, menu(cafe, "아메리카노", "4000"), menu(cafe, "라떼", "4500"), menu(cafe)),
                category(etc, menu(etc, price = "3000", count = 2)),
            ),
        )

        // When
        val event = MenuExtractionLog.registerClickEvent(state, MenuExtractionFlow.CREATE)

        // Then
        assertEquals(ScreenName.WRITE_DETAIL_MENU_EXTRACTION_RESULT, event.screen)
        assertEquals(LogObjectType.BUTTON, event.objectType)
        assertEquals(LogObjectId.REGISTER, event.objectId)
        assertEquals("register", event.extraParameters[ParameterName.OBJECT_ID])
        assertEquals(3, event.extraParameters[ParameterName.RECOGNIZED_MENU_COUNT])
        assertEquals(3, event.extraParameters[ParameterName.FINAL_MENU_COUNT])
        assertEquals("create", event.extraParameters[ParameterName.FLOW])
    }

    // TH-1486 TC6
    @Test
    fun `TH1486_TC6_제보완료후_메뉴추가에서_저장에_성공하면_edit_store_complete가_after_create흐름으로_실린다`() {
        // Given
        val flow = MenuExtractionFlow.AFTER_CREATE

        // When
        val event = MenuExtractionLog.editStoreCompleteEvent(menuExtractionUsed = true, editedMenu = true, flow = flow)
        val registerClick = MenuExtractionLog.registerClickEvent(MenuExtractionState(), flow)

        // Then
        assertEquals("after_create", event.extraParameters?.get(ParameterName.FLOW))
        assertEquals("after_create", registerClick.extraParameters[ParameterName.FLOW])
    }
}
