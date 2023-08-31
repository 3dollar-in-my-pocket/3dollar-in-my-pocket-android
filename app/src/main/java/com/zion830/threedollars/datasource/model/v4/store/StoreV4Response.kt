package com.zion830.threedollars.datasource.model.v4.store


import com.google.gson.annotations.SerializedName

data class StoreV4Response(
    @SerializedName("creator")
    val creator: Creator = Creator(),
    @SerializedName("distanceM")
    val distanceM: Int = 0,
    @SerializedName("favorite")
    val favorite: Favorite = Favorite(),
    @SerializedName("images")
    val images: Images = Images(),
    @SerializedName("reviews")
    val reviews: Reviews = Reviews(),
    @SerializedName("store")
    val store: Store = Store(),
    @SerializedName("visits")
    val visits: Visits = Visits()
)