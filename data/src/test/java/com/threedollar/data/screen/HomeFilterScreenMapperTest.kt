package com.threedollar.data.screen

import com.google.gson.Gson
import com.google.gson.JsonPrimitive
import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.threedollar.network.data.screen.HomeFilterBarResponse
import com.threedollar.network.data.screen.HomeFilterChipResponse
import com.threedollar.network.data.screen.HomeFilterClickLogResponse
import com.threedollar.network.data.screen.HomeFilterConfigurationResponse
import com.threedollar.network.data.screen.HomeFilterImageResponse
import com.threedollar.network.data.screen.HomeFilterImageStyleResponse
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeFilterSectionResponse
import com.threedollar.network.data.screen.HomeFilterTextResponse
import com.threedollar.network.data.screen.HomeFilterViewLogResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeFilterScreenMapperTest {

    private fun homeScreen(name: String): HomeFilterScreenResponse = Gson().fromJson(
        requireNotNull(javaClass.classLoader?.getResource("screen/$name")).readText(),
        HomeFilterScreenResponse::class.java,
    )

    // TH-1401 TC1
    @Test
    fun `TH1401_TC1_실서버홈응답이면_탭순서와기본선택_표시와로그를_보존한다`() {
        // Given
        val response = homeScreen("HomeFilterScreenWithTabs.json")

        // When
        val model = response.asModel()

        // Then
        val tabSections = model.sections.filterIsInstance<HomeScreenSection.HomeBottomSheetTabSectionModel>()
        assertEquals(1, tabSections.size)
        val tabs = tabSections.single().tabs.tabs
        assertEquals(listOf("CURATION", "DEFAULT"), tabs.map { it.tabId })
        assertEquals(listOf("CURATION", "STORE_LIST"), tabs.map { it.viewType })
        assertEquals(listOf(true, false), tabs.map { it.defaultSelected })
        assertTrue(tabs.first().selected.title?.isHtml == true)
        assertTrue(tabs.first().selected.title?.text.orEmpty().contains("요즘 뜨는 간식"))
        assertEquals("CLICK", tabs.first().clickLog?.eventType)
        assertEquals("CURATION", tabs.first().clickLog?.extraParameters?.get("value"))
        assertTrue(model.sections.any { it is HomeScreenSection.HomeFilterSectionModel })
        assertTrue(model.sections.any { it is HomeScreenSection.HomeMapControlSectionModel })
    }

    // TH-1401 TC8
    @Test
    fun `TH1401_TC8_실데이터에_미지원viewType이추가돼도_지원탭순서를_유지한다`() {
        // Given
        val response = homeScreen("HomeFilterScreenWithUnknownTab.json")

        // When
        val tabSections = response.asModel().sections.filterIsInstance<HomeScreenSection.HomeBottomSheetTabSectionModel>()

        // Then
        assertEquals(1, tabSections.size)
        val tabs = tabSections.single().tabs.tabs
        assertEquals(listOf("CURATION", "DEFAULT"), tabs.map { it.tabId })
        assertEquals(listOf("CURATION", "STORE_LIST"), tabs.map { it.viewType })
    }

    @Test
    fun homeFilterMapper_mapsViewLogAdditionalTextAndImageStyle() {
        val response = HomeFilterScreenResponse(
            viewLog = HomeFilterViewLogResponse(screenName = "home"),
            sections = listOf(
                HomeFilterSectionResponse(
                    type = "HOME_FILTER",
                    bars = listOf(
                        HomeFilterBarResponse(
                            type = "CATEGORY_BAR",
                            categoriesFilter = HomeFilterChipResponse(
                                image = HomeFilterImageResponse(
                                    url = "https://example.com/icon.png",
                                    style = HomeFilterImageStyleResponse(width = 18.0, height = 18.0),
                                ),
                                text = HomeFilterTextResponse(
                                    text = "음식 종류",
                                    isHtml = false,
                                    fontColor = "#5A5A5A",
                                ),
                                additionalText = HomeFilterTextResponse(
                                    text = "NEW",
                                    isHtml = false,
                                    fontColor = "#FF858F",
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val model = response.asModel()
        val section = model.sections.single() as HomeScreenSection.HomeFilterSectionModel
        val categoryBar = section.bars.single() as HomeFilterBar.CategoryBar

        assertEquals("home", model.viewLog?.screenName)
        assertEquals("NEW", categoryBar.categoriesFilter.additionalText?.text)
        assertEquals(18.0, categoryBar.categoriesFilter.image?.style?.width)
        assertEquals(18.0, categoryBar.categoriesFilter.image?.style?.height)
    }

    @Test
    fun homeFilterMapper_preservesLongClickLogNumbers() {
        val longStoreId = 855453324337299456L
        val response = HomeFilterScreenResponse(
            sections = listOf(
                HomeFilterSectionResponse(
                    type = "HOME_FILTER",
                    bars = listOf(
                        HomeFilterBarResponse(
                            type = "CATEGORY_BAR",
                            categoriesFilter = HomeFilterChipResponse(
                                text = HomeFilterTextResponse(text = "음식 종류"),
                            ),
                            categoriesFilterClickLog = HomeFilterClickLogResponse(
                                screenName = "home",
                                objectType = "filter",
                                objectId = "category",
                                extraParameters = mapOf("STORE_ID" to JsonPrimitive(longStoreId)),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val section = response.asModel().sections.single() as HomeScreenSection.HomeFilterSectionModel
        val categoryBar = section.bars.single() as HomeFilterBar.CategoryBar

        assertEquals(longStoreId, categoryBar.categoriesFilterClickLog?.extraParameters?.get("STORE_ID")?.anyValue)
    }

    @Test
    fun homeFilterMapper_mapsInitialMapZoomLevel() {
        val response = HomeFilterScreenResponse(
            configuration = HomeFilterConfigurationResponse(initialMapZoomLevel = 15.0),
        )

        assertEquals(15.0, response.asModel().configuration?.initialMapZoomLevel)
    }

    @Test
    fun homeFilterMapper_keepsNullConfigurationWhenResponseHasNone() {
        val response = HomeFilterScreenResponse()

        assertNull(response.asModel().configuration)
    }
}
