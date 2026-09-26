package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeFilterBarType
import com.threedollar.common.serverdriven.model.HomeFilterRadioOption
import com.threedollar.common.serverdriven.model.HomeMapControl
import com.threedollar.common.serverdriven.model.HomeMapControlButton
import com.threedollar.common.serverdriven.model.HomeMapControlFilterOption
import com.threedollar.common.serverdriven.model.HomeMapControlType
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.threedollar.common.serverdriven.model.HomeScreenSectionType
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeMapControlResolverTest {

    private val favoriteFilter = HomeMapControl.Filter(
        type = HomeMapControlType.FILTER,
        paramKey = FAVORITE_PARAM_KEY,
        options = listOf(
            HomeMapControlFilterOption(paramValue = true, button = button(EMPTY_BOOKMARK_URL)),
            HomeMapControlFilterOption(paramValue = false, button = button(FILLED_BOOKMARK_URL)),
        ),
    )

    private val currentLocationAction = HomeMapControl.Action(
        type = HomeMapControlType.ACTION,
        button = button(
            url = LOCATION_URL,
            customAction = SDCustomActionModel(
                actionType = "HOME_MAP_CONTROL_SECTION_MOVE_TO_CURRENT_LOCATION",
                extraParams = mapOf("MAP_ZOOM_LEVEL" to SDClickLogValue.DoubleValue(15.0)),
            ),
        ),
    )

    @Test
    fun `TH1349_TC1_HOME_MAP_CONTROL섹션이있으면_controls순서대로_버튼을그린다`() {
        val sections = listOf(
            HomeScreenSection.HomeFilterSectionModel(type = HomeScreenSectionType.HOME_FILTER),
            HomeScreenSection.HomeMapControlSectionModel(
                type = HomeScreenSectionType.HOME_MAP_CONTROL,
                controls = listOf(favoriteFilter, currentLocationAction),
            ),
        )

        val items = HomeMapControlResolver.items(
            controls = HomeMapControlResolver.controls(sections),
            filterValues = emptyMap(),
        )

        assertEquals(listOf(EMPTY_BOOKMARK_URL, LOCATION_URL), items.imageUrls())
    }

    @Test
    fun `TH1349_TC2_현재위치액션이면_서버줌레벨을_꺼낸다`() {
        val action = currentLocationAction.button.customAction

        assertTrue(HomeMapControlResolver.isMoveToCurrentLocation(action))
        assertEquals(15.0, HomeMapControlResolver.zoomLevel(action))
    }

    @Test
    fun `TH1349_TC2_줌레벨이_정수나_문자열로와도_읽고_없으면_null이다`() {
        fun action(value: SDClickLogValue?) = SDCustomActionModel(
            actionType = "HOME_MAP_CONTROL_SECTION_MOVE_TO_CURRENT_LOCATION",
            extraParams = value?.let { mapOf("MAP_ZOOM_LEVEL" to it) }.orEmpty(),
        )

        assertEquals(13.0, HomeMapControlResolver.zoomLevel(action(SDClickLogValue.IntValue(13))))
        assertEquals(14.5, HomeMapControlResolver.zoomLevel(action(SDClickLogValue.StringValue("14.5"))))
        assertNull(HomeMapControlResolver.zoomLevel(action(null)))
    }

    @Test
    fun `TH1349_TC2_모르는액션타입은_현재위치이동이아니다`() {
        val action = SDCustomActionModel(actionType = "UNKNOWN_ACTION")

        assertFalse(HomeMapControlResolver.isMoveToCurrentLocation(action))
        assertFalse(HomeMapControlResolver.isMoveToCurrentLocation(null))
    }

    @Test
    fun `TH1349_TC3_필터가꺼진상태에서탭하면_켜짐버튼으로바뀌고_focusFavoriteStores가true가된다`() {
        val controls = listOf(favoriteFilter, currentLocationAction)
        val before = emptyMap<String, Boolean>()

        assertEquals("false", HomeMapControlResolver.queryParams(controls, before)[FAVORITE_PARAM_KEY])

        val after = HomeMapControlResolver.toggle(favoriteFilter, before)

        assertEquals(listOf(FILLED_BOOKMARK_URL, LOCATION_URL), HomeMapControlResolver.items(controls, after).imageUrls())
        assertEquals("true", HomeMapControlResolver.queryParams(controls, after)[FAVORITE_PARAM_KEY])
    }

    @Test
    fun `TH1349_TC4_필터가켜진상태에서다시탭하면_꺼짐버튼으로돌아가고_focusFavoriteStores가false가된다`() {
        val controls = listOf(favoriteFilter)
        val turnedOn = mapOf(FAVORITE_PARAM_KEY to true)

        val after = HomeMapControlResolver.toggle(favoriteFilter, turnedOn)

        assertEquals(listOf(EMPTY_BOOKMARK_URL), HomeMapControlResolver.items(controls, after).imageUrls())
        assertEquals("false", HomeMapControlResolver.queryParams(controls, after)[FAVORITE_PARAM_KEY])
    }

    @Test
    fun `TH1349_TC3_필터값은_주변가게조회_dynamicParams에_함께실린다`() {
        val params = HomeAroundStoreRequestParamsBuilder.build(
            state = HomeUIState(
                radioSelection = mapOf("sortType" to 0),
                mapControlFilterValues = mapOf(FAVORITE_PARAM_KEY to true),
            ),
            bars = listOf(sortTypeBar()),
            mapControls = listOf(favoriteFilter),
        )

        assertEquals("true", params.dynamicParams[FAVORITE_PARAM_KEY])
        assertEquals("DISTANCE_ASC", params.dynamicParams["sortType"])
    }

    @Test
    fun `TH1349_TC6_HOME_MAP_CONTROL섹션이없으면_현재위치버튼만_폴백으로그리고_필터파라미터가없다`() {
        val sections = listOf(
            HomeScreenSection.HomeFilterSectionModel(type = HomeScreenSectionType.HOME_FILTER),
        )
        val controls = HomeMapControlResolver.controls(sections)

        assertEquals(
            listOf(HomeMapControlItem.FallbackCurrentLocation),
            HomeMapControlResolver.items(controls, emptyMap()),
        )
        assertTrue(HomeMapControlResolver.queryParams(controls, emptyMap()).isEmpty())
    }

    @Test
    fun `TH1349_TC6_옵션이하나뿐인필터도_그옵션을_그대로보여준다`() {
        val singleOption = favoriteFilter.copy(options = favoriteFilter.options.take(1))

        val items = HomeMapControlResolver.items(listOf(singleOption), mapOf(FAVORITE_PARAM_KEY to true))

        assertEquals(listOf(EMPTY_BOOKMARK_URL), items.imageUrls())
    }

    private fun List<HomeMapControlItem>.imageUrls(): List<String> =
        map { (it as HomeMapControlItem.ServerDriven).button.image.url }

    private fun sortTypeBar() = HomeFilterBar.RadioBar(
        type = HomeFilterBarType.RADIO_BAR,
        paramKey = "sortType",
        options = listOf(
            HomeFilterRadioOption(
                chip = SDChipModel(
                    text = SDTextModel(text = "거리순", isHtml = false),
                ),
                paramValue = "DISTANCE_ASC",
                clickLog = null,
            ),
        ),
    )

    private fun button(
        url: String,
        customAction: SDCustomActionModel? = null,
    ) = HomeMapControlButton(
        image = SDImageModel(url = url),
        customAction = customAction,
    )

    private companion object {
        const val FAVORITE_PARAM_KEY = "focusFavoriteStores"
        const val EMPTY_BOOKMARK_URL = "https://storage.threedollars.co.kr/app/bookmark_empty.png"
        const val FILLED_BOOKMARK_URL = "https://storage.threedollars.co.kr/app/bookmark.png"
        const val LOCATION_URL = "https://storage.threedollars.co.kr/app/location-current.png"
    }
}
