package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeFilterBarType
import com.threedollar.common.serverdriven.model.HomeFilterRadioOption
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class HomeFilterQueryParamsBuilderTest {

    @Test
    fun build_includesCertifiedStoreQueryOnlyWhenEnabled() {
        val disabledParams = HomeFilterQueryParamsBuilder.build(
            state = HomeUIState(filterCertifiedStores = false),
            bars = emptyList(),
        )

        val enabledParams = HomeFilterQueryParamsBuilder.build(
            state = HomeUIState(filterCertifiedStores = true),
            bars = emptyList(),
        )

        assertFalse(disabledParams.containsKey("filterCertifiedStores"))
        assertEquals("true", enabledParams["filterCertifiedStores"])
    }

    @Test
    fun build_usesSelectedRadioOptionsAsDynamicQueries() {
        val bars = listOf(
            radioBar(paramKey = "filterOpenStatuses", paramValues = listOf(null, "SERVER_OPEN")),
            radioBar(paramKey = "filterConditions", paramValues = listOf(null, "SERVER_RECENT")),
            radioBar(paramKey = "sortType", paramValues = listOf("SERVER_DISTANCE", "SERVER_LATEST")),
            radioBar(paramKey = "targetStores", paramValues = listOf(null, "SERVER_TARGET")),
        )
        val state = HomeUIState(
            radioSelection = mapOf(
                "filterOpenStatuses" to 1,
                "filterConditions" to 1,
                "sortType" to 1,
                "targetStores" to 1,
            ),
        )

        val params = HomeFilterQueryParamsBuilder.build(state = state, bars = bars)

        assertEquals("SERVER_OPEN", params["filterOpenStatuses"])
        assertEquals("SERVER_RECENT", params["filterConditions"])
        assertEquals("SERVER_LATEST", params["sortType"])
        assertEquals("SERVER_TARGET", params["targetStores"])
    }

    @Test
    fun build_defaultsToFirstRadioOptionWithoutAddingAppFallbackSortType() {
        val bars = listOf(
            radioBar(paramKey = "filterConditions", paramValues = listOf(null, "SERVER_RECENT")),
            radioBar(paramKey = "sortType", paramValues = listOf("SERVER_DISTANCE", "SERVER_LATEST")),
        )

        val params = HomeFilterQueryParamsBuilder.build(
            state = HomeUIState(),
            bars = bars,
        )

        assertFalse(params.containsKey("filterConditions"))
        assertEquals("SERVER_DISTANCE", params["sortType"])
    }

    @Test
    fun build_doesNotCreateSortTypeWhenServerBarsAreMissing() {
        val params = HomeFilterQueryParamsBuilder.build(
            state = HomeUIState(),
            bars = emptyList(),
        )

        assertNull(params["sortType"])
    }

    private fun radioBar(
        paramKey: String,
        paramValues: List<String?>,
    ): HomeFilterBar.RadioBar = HomeFilterBar.RadioBar(
        type = HomeFilterBarType.RADIO_BAR,
        paramKey = paramKey,
        options = paramValues.mapIndexed { index, value ->
            HomeFilterRadioOption(
                chip = SDChipModel(
                    text = SDTextModel(
                        text = "$paramKey-$index",
                        isHtml = false,
                    ),
                ),
                paramValue = value,
                clickLog = null,
            )
        },
    )
}
