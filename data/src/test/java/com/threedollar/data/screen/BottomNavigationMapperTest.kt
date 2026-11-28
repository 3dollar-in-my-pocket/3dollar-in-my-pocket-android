package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.network.data.screen.BottomNavigationResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BottomNavigationMapperTest {

    private fun bottomNavigation(name: String): BottomNavigationResponse = Gson().fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText(),
        BottomNavigationResponse::class.java,
    )

    // TH-1650 TC1
    @Test
    fun `TH1650_TC1_응답순서대로_라벨_아이콘_선택색_type_link를_읽는다`() {
        // Given
        val response = bottomNavigation("BottomNavigation.json")

        // When
        val tabs = response.asTabModels()

        // Then
        assertEquals(listOf("home", "store_create", "event", "community", "my"), tabs.map { it.id })
        assertEquals(listOf("홈", "가게 제보", "이벤트", "커뮤니티", "마이페이지"), tabs.map { it.label })
        assertEquals("https://storage.threedollars.co.kr/app/event_solid.png", tabs[2].iconUrl)
        assertEquals("#FF5C43", tabs[2].selectedTintColor)
        assertEquals(listOf("APP_DEFINED", "APP_DEFINED", "DYNAMIC", "APP_DEFINED", "APP_DEFINED"), tabs.map { it.type })
        assertNull(tabs[0].link)
        assertEquals(SDLinkModel(type = "WEB", link = "https://echo.dev.threedollars.co.kr/events/point"), tabs[2].link)
    }

    // TH-1650 TC6
    @Test
    fun `TH1650_TC6_탭마다_서버_clickLog를_그대로_읽는다`() {
        // Given
        val response = bottomNavigation("BottomNavigation.json")

        // When
        val clickLog = response.asTabModels().first { it.id == "event" }.clickLog

        // Then
        requireNotNull(clickLog)
        assertEquals("bottom_navigation", clickLog.screenName)
        assertEquals("bar", clickLog.objectType)
        assertEquals("tab", clickLog.objectId)
        assertEquals(mapOf("value" to SDClickLogValue.StringValue("event")), clickLog.extraParameters)
    }

    // TH-1650 TC5
    @Test
    fun `TH1650_TC5_구응답처럼_type이_없거나_모르는_type이어도_크래시없이_원값을_넘긴다`() {
        // Given
        val legacy = bottomNavigation("BottomNavigationWithoutType.json")
        val unknown = bottomNavigation("BottomNavigationWithUnknownTabs.json")

        // When
        val legacyTabs = legacy.asTabModels()
        val unknownTabs = unknown.asTabModels()

        // Then
        assertEquals(5, legacyTabs.size)
        assertEquals(listOf<String?>(null), legacyTabs.map { it.type }.distinct())
        assertEquals("NATIVE_V2", unknownTabs.first { it.id == "future" }.type)
    }

    // TH-1650 TC5
    @Test
    fun `TH1650_TC5_id가_없는_탭과_빈_tabs는_뺀다`() {
        // Given
        val response = BottomNavigationResponse(
            tabs = bottomNavigation("BottomNavigation.json").tabs.orEmpty().mapIndexed { index, tab ->
                if (index == 0) tab.copy(id = " ") else tab
            },
        )

        // When
        val tabs = response.asTabModels()
        val empty = BottomNavigationResponse(tabs = null).asTabModels()

        // Then
        assertEquals(listOf("store_create", "event", "community", "my"), tabs.map { it.id })
        assertEquals(emptyList<Any>(), empty)
    }
}
