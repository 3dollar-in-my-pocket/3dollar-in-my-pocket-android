package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class BossStore(
    @SerializedName("storeId")
    val storeId: String = "",

    @SerializedName("isOwner")
    val isOwner: Boolean = false,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("rating")
    val rating: Float = 0.0f,

    @SerializedName("location")
    val location: Location = Location(),

    @SerializedName("address")
    val address: Address = Address(),

    @SerializedName("representativeImages")
    val representativeImages: List<RepresentativeImage> = emptyList(),

    @SerializedName("introduction")
    val introduction: String = "",

    @SerializedName("snsUrl")
    val snsUrl: String = "",

    @SerializedName("menus")
    val menus: List<Menu> = emptyList(),

    @SerializedName("appearanceDays")
    val appearanceDays: List<AppearanceDay> = emptyList(),

    @SerializedName("categories")
    val categories: List<Category> = emptyList(),

    @SerializedName("accountNumbers")
    val accountNumbers: List<AccountNumber> = emptyList(),

    @SerializedName("contactsNumbers")
    val contactsNumbers: List<ContactNumber> = emptyList(),

    @SerializedName("activitiesStatus")
    val activitiesStatus: ActivitiesStatus = ActivitiesStatus.RECENT_ACTIVITY,

    @SerializedName("createdAt")
    val createdAt: String = "",

    @SerializedName("updatedAt")
    val updatedAt: String = ""
)

