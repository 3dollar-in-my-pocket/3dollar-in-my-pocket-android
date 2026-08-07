package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeFilterBar

data class HomeAroundStoreRequestParams(
    val distanceM: Double,
    val categoryIds: Array<String>?,
    val targetStores: Array<String>?,
    val mapLatitude: Double,
    val mapLongitude: Double,
    val deviceLatitude: Double,
    val deviceLongitude: Double,
    val dynamicParams: Map<String, String>,
)

object HomeAroundStoreRequestParamsBuilder {

    fun build(
        state: HomeUIState,
        bars: List<HomeFilterBar>,
    ): HomeAroundStoreRequestParams = HomeAroundStoreRequestParams(
        distanceM = state.currentDistanceM,
        categoryIds = state.selectedCategory?.id?.let { arrayOf(it) },
        targetStores = null,
        mapLatitude = state.mapPosition.latitude,
        mapLongitude = state.mapPosition.longitude,
        deviceLatitude = state.userLocation.latitude,
        deviceLongitude = state.userLocation.longitude,
        dynamicParams = HomeFilterQueryParamsBuilder.build(
            state = state,
            bars = bars,
        ),
    )
}
