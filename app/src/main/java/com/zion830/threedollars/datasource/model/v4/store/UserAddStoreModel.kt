package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName

data class UserAddStoreModel(
    @SerializedName("distanceM")
    val distanceM: Int = 0,
    @SerializedName("store")
    val store: UserAddStore = UserAddStore(),
    @SerializedName("visits")
    val visits: Visits = Visits()
)