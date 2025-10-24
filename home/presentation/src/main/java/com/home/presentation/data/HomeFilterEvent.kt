package com.home.presentation.data

import com.threedollar.domain.home.request.FilterConditionsTypeModel

data class HomeFilterEvent(
    val homeSortType: HomeSortType = HomeSortType.DISTANCE_ASC,
    val homeStoreType: HomeStoreType = HomeStoreType.ALL,
    val filterConditionsType : List<FilterConditionsTypeModel> = listOf()
)