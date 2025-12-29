package com.threedollar.network.data.store


import com.google.gson.annotations.SerializedName

data class UserStoreResponse(
    @SerializedName("creator")
    val creator: Creator? = Creator(),
    @SerializedName("distanceM")
    val distanceM: Int? = 0,
    @SerializedName("favorite")
    val favorite: Favorite? = Favorite(),
    @SerializedName("images")
    val images: Images? = Images(),
    @SerializedName("reviews")
    val reviews: Reviews? = Reviews(),
    @SerializedName("store")
    val store: UserStore? = UserStore(),
    @SerializedName("tags")
    val tags: Tags? = Tags(),
    @SerializedName("visits")
    val visits: Visits? = Visits(),
    @SerializedName("openStatus")
    val openStatus: OpenStatus? = OpenStatus()
)