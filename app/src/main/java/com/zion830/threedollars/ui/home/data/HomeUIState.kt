package com.zion830.threedollars.ui.home.data

import com.naver.maps.geometry.LatLng
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.zion830.threedollars.ui.dialog.category.StoreCategoryItem

data class HomeUIState(
    var mapPosition: LatLng = DEFAULT_LOCATION,
    var userLocation: LatLng = DEFAULT_LOCATION,
    var currentDistanceM: Double = DEFAULT_DISTANCE_M,
    var filterCertifiedStores: Boolean = false,
    val selectedCategory: StoreCategoryItem? = null,
    val filterSections: List<HomeScreenSection> = emptyList(),
    val radioSelection: Map<String, Int> = emptyMap(),
    val hasLoadedFilterScreen: Boolean = false,
) {
    companion object {
        val DEFAULT_LOCATION = LatLng(37.56, 126.97) // 서울
        const val DEFAULT_DISTANCE_M = 100000.0
    }
}
