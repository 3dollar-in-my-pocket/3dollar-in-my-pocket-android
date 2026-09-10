package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeFilterBarType
import com.threedollar.common.serverdriven.model.HomeFilterRadioOption
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeRadioSelectionResolverTest {
    @Test
    fun `radio selection follows the same parameter value after options reorder`() {
        val oldBars = listOf(radio("sortType", "POPULAR", "DISTANCE"))
        val newBars = listOf(radio("sortType", "DISTANCE", "POPULAR"))

        assertEquals(
            mapOf("sortType" to 0),
            reconcileHomeRadioSelection(
                oldSelection = mapOf("sortType" to 1),
                oldBars = oldBars,
                newBars = newBars,
            ),
        )
    }

    private fun radio(key: String, vararg values: String) = HomeFilterBar.RadioBar(
        type = HomeFilterBarType.RADIO_BAR,
        paramKey = key,
        options = values.map { value ->
            HomeFilterRadioOption(
                chip = SDChipModel(text = SDTextModel(value, false)),
                paramValue = value,
                clickLog = null,
            )
        },
    )
}
