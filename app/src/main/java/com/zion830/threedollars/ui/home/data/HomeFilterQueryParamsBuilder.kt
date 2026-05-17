package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeFilterBar

internal object HomeFilterQueryParamsBuilder {
    fun build(
        state: HomeUIState,
        bars: List<HomeFilterBar>,
    ): Map<String, String> {
        val params = mutableMapOf<String, String>()

        bars.filterIsInstance<HomeFilterBar.RadioBar>().forEach { bar ->
            if (bar.paramKey == "targetStores") return@forEach

            val selectedIndex = state.radioSelection[bar.paramKey] ?: 0
            val value = bar.options.getOrNull(selectedIndex)?.paramValue ?: return@forEach
            params[bar.paramKey] = value
        }

        if (params["sortType"] == null) {
            params["sortType"] = state.homeSortType.name
        }
        if (state.filterCertifiedStores) {
            params["filterCertifiedStores"] = "true"
        }

        return params
    }
}
