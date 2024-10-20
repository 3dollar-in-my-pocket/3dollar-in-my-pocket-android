package com.home.domain.data.store

data class MarkerModel(
    val selected: StoreMarkerImageModel = StoreMarkerImageModel(),
    val unSelected: StoreMarkerImageModel = StoreMarkerImageModel()
)