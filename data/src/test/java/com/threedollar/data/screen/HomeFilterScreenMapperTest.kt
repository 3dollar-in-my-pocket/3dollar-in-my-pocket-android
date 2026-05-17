package com.threedollar.data.screen

import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.threedollar.network.data.screen.HomeFilterBarResponse
import com.threedollar.network.data.screen.HomeFilterChipResponse
import com.threedollar.network.data.screen.HomeFilterImageResponse
import com.threedollar.network.data.screen.HomeFilterImageStyleResponse
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeFilterSectionResponse
import com.threedollar.network.data.screen.HomeFilterTextResponse
import com.threedollar.network.data.screen.HomeFilterViewLogResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeFilterScreenMapperTest {

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
}
