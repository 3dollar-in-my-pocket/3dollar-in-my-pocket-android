package com.threedollar.domain.home.data.store

data class MarkerModel(
    val selected: StoreMarkerImageModel = StoreMarkerImageModel(),
    val unSelected: StoreMarkerImageModel = StoreMarkerImageModel()
)