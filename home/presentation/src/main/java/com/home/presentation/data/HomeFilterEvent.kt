package com.home.presentation.data

import com.home.domain.request.FilterConditionsTypeModel

data class HomeFilterEvent(
    val homeSortType: HomeSortType = HomeSortType.DISTANCE_ASC,
    val homeStoreType: HomeStoreType = HomeStoreType.ALL,
    val filterConditionsType : FilterConditionsTypeModel = FilterConditionsTypeModel.NO_RECENT_ACTIVITY
)