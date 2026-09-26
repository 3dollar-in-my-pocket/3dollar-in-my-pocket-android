package com.threedollar.data.screen

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.threedollar.common.serverdriven.model.HomeMapControl
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.network.data.screen.HomeFilterImageResponse
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeFilterSectionResponse
import com.threedollar.network.data.screen.HomeMapControlButtonResponse
import com.threedollar.network.data.screen.HomeMapControlResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeMapControlScreenMapperTest {

    @Test
    fun `TH1349_TC1_HOME_MAP_CONTROL섹션이내려오면_controls순서대로_매핑된다`() {
        val section = mapControlSection(loadFixture())

        assertEquals(
            listOf(HomeMapControl.Filter::class, HomeMapControl.Action::class),
            section.controls.map { it::class },
        )
        val filter = section.controls[0] as HomeMapControl.Filter
        assertEquals("focusFavoriteStores", filter.paramKey)
        assertEquals(listOf(true, false), filter.options.map { it.paramValue })
        assertEquals("https://storage.threedollars.co.kr/app/bookmark_empty.png", filter.options[0].button.image.url)
    }

    @Test
    fun `TH1349_TC1_컨트롤버튼의_이미지스타일과_배경테두리를_그대로_매핑한다`() {
        val action = mapControlSection(loadFixture()).controls[1] as HomeMapControl.Action
        val button = action.button

        assertEquals(28.0, button.image.style?.width)
        assertEquals(28.0, button.image.style?.height)
        assertFalse(button.image.style?.dimmed ?: true)
        assertEquals("#FFFFFF", button.style?.backgroundColor)
        assertEquals("#E2E2E2", button.style?.border?.color)
        assertEquals(1.0, button.style?.border?.width)
    }

    @Test
    fun `TH1349_TC2_ACTION컨트롤은_현재위치이동액션과_줌레벨을_매핑한다`() {
        val action = mapControlSection(loadFixture()).controls[1] as HomeMapControl.Action
        val customAction = action.button.customAction

        assertEquals("HOME_MAP_CONTROL_SECTION_MOVE_TO_CURRENT_LOCATION", customAction?.actionType)
        assertTrue(customAction?.extraParams?.get("MAP_ZOOM_LEVEL") is SDClickLogValue.DoubleValue)
    }

    @Test
    fun `TH1349_TC5_컨트롤버튼의_서버clickLog를_매핑한다`() {
        val filter = mapControlSection(loadFixture()).controls[0] as HomeMapControl.Filter
        val clickLog = filter.options[0].button.clickLog

        assertEquals("home", clickLog?.screenName)
        assertEquals("button", clickLog?.objectType)
        assertEquals("map_favorite_store_filter", clickLog?.objectId)
        assertEquals("false", clickLog?.extraParameters?.get("value")?.anyValue)
    }

    @Test
    fun `TH1349_TC6_모르는컨트롤타입과_이미지없는버튼은_건너뛴다`() {
        val response = HomeFilterScreenResponse(
            sections = listOf(
                HomeFilterSectionResponse(
                    type = "HOME_MAP_CONTROL",
                    controls = listOf(
                        HomeMapControlResponse(type = "NEW_CONTROL", button = buttonResponse("https://example.com/new.png")),
                        HomeMapControlResponse(type = "ACTION", button = buttonResponse(url = null)),
                        HomeMapControlResponse(type = "FILTER", paramKey = "focusFavoriteStores", options = emptyList()),
                        HomeMapControlResponse(type = "ACTION", button = buttonResponse("https://example.com/location.png")),
                    ),
                ),
            ),
        )

        val controls = mapControlSection(response).controls

        assertEquals(1, controls.size)
        assertEquals("https://example.com/location.png", (controls.single() as HomeMapControl.Action).button.image.url)
    }

    @Test
    fun `TH1349_TC6_HOME_MAP_CONTROL섹션이없어도_HOME_FILTER섹션은_그대로_매핑된다`() {
        val response = HomeFilterScreenResponse(
            sections = listOf(HomeFilterSectionResponse(type = "HOME_FILTER")),
        )

        val sections = response.asModel().sections

        assertEquals(1, sections.size)
        assertTrue(sections.single() is HomeScreenSection.HomeFilterSectionModel)
    }

    private fun mapControlSection(response: HomeFilterScreenResponse): HomeScreenSection.HomeMapControlSectionModel =
        response.asModel().sections.filterIsInstance<HomeScreenSection.HomeMapControlSectionModel>().single()

    private fun buttonResponse(url: String?) = HomeMapControlButtonResponse(
        image = HomeFilterImageResponse(url = url),
    )

    private fun loadFixture(): HomeFilterScreenResponse {
        val json = requireNotNull(javaClass.classLoader?.getResource("screen/HomeFilterScreenWithMapControl.json")).readText()
        val data = JsonParser.parseString(json).asJsonObject.get("data")
        return Gson().fromJson(data, HomeFilterScreenResponse::class.java)
    }
}
