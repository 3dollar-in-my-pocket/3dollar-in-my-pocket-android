package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeFilterBar

internal fun reconcileHomeRadioSelection(
    oldSelection: Map<String, Int>,
    oldBars: List<HomeFilterBar>,
    newBars: List<HomeFilterBar>,
): Map<String, Int> {
    val oldValues = oldBars.filterIsInstance<HomeFilterBar.RadioBar>().associate { bar ->
        val selectedIndex = oldSelection[bar.paramKey] ?: 0
        bar.paramKey to bar.options.getOrNull(selectedIndex)?.paramValue
    }
    return newBars.filterIsInstance<HomeFilterBar.RadioBar>().associate { bar ->
        val oldValue = oldValues[bar.paramKey]
        val newIndex = oldValue?.let { value -> bar.options.indexOfFirst { it.paramValue == value } }
            ?.takeIf { it >= 0 }
            ?: 0
        bar.paramKey to newIndex
    }
}
