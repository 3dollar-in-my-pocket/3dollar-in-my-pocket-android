package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeFilterBarType
import com.threedollar.common.serverdriven.model.HomeFilterRadioOption
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun build_usesSelectedRadioOptionsAndSkipsTypedTargetStoresQuery() {
        val bars = listOf(
            radioBar(paramKey = "filterOpenStatuses", paramValues = listOf(null, "OPEN")),
            radioBar(paramKey = "filterConditions", paramValues = listOf(null, "RECENT_ACTIVITY")),
            radioBar(paramKey = "sortType", paramValues = listOf("DISTANCE_ASC", "LATEST")),
            radioBar(paramKey = "targetStores", paramValues = listOf(null, "BOSS_STORE")),
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

        assertEquals("OPEN", params["filterOpenStatuses"])
        assertEquals("RECENT_ACTIVITY", params["filterConditions"])
        assertEquals("LATEST", params["sortType"])
        assertFalse(params.containsKey("targetStores"))
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
