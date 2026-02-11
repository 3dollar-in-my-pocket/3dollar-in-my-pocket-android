package com.zion830.threedollars.ui.home.data

import com.naver.maps.geometry.LatLng
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.request.FilterConditionsTypeModel
import com.zion830.threedollars.ui.dialog.category.StoreCategoryItem

data class HomeUIState(
    var mapPosition: LatLng= DEFAULT_LOCATION,
    var userLocation: LatLng = DEFAULT_LOCATION,
    var currentDistanceM: Double = DEFAULT_DISTANCE_M,
    var filterCertifiedStores: Boolean = false,
    var homeSortType: HomeSortType = HomeSortType.DISTANCE_ASC,
    var homeStoreType: HomeStoreType = HomeStoreType.ALL,
    var filterConditionsType : List<FilterConditionsTypeModel> = listOf(FilterConditionsTypeModel.RECENT_ACTIVITY),
    val selectedCategory: StoreCategoryItem? = null
) {
    companion object {
        val DEFAULT_LOCATION = LatLng(37.56, 126.97) // 서울
        const val DEFAULT_DISTANCE_M = 100000.0
    }
}