package com.home.presentation.data

data class HomeFilterEvent(
    val homeSortType: HomeSortType = HomeSortType.DISTANCE_ASC,
    val homeStoreType: HomeStoreType = HomeStoreType.ALL,
)