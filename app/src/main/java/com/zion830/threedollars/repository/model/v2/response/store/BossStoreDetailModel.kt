package com.zion830.threedollars.repository.model.v2.response.store


import com.google.gson.annotations.SerializedName

interface BossStoreDetailItem

data class BossStoreDetailModel(
    @SerializedName("appearanceDays")
    val appearanceDays: List<AppearanceDay>?,
    @SerializedName("bossStoreId")
    val bossStoreId: String?,
    @SerializedName("categories")
    val categories: List<Category>?,
    @SerializedName("contactsNumber")
    val contactsNumber: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("distance")
    val distance: Int?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("introduction")
    val introduction: String?,
    @SerializedName("location")
    val location: Location,
    @SerializedName("menus")
    val menus: List<Menu>?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("openStatus")
    val openStatus: OpenStatus?,
    @SerializedName("snsUrl")
    val snsUrl: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
) {
    data class Category(
        @SerializedName("categoryId")
        val categoryId: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("imageUrl")
        val imageUrl: String?
    )

    data class Menu(
        @SerializedName("imageUrl")
        val imageUrl: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("price")
        val price: Int?
    ) : BossStoreDetailItem

    data class AppearanceDay(
        @SerializedName("dayOfTheWeek")
        var dayOfTheWeek: String?,
        @SerializedName("locationDescription")
        val locationDescription: String?,
        @SerializedName("openingHours")
        val openingHours: OpeningHours?
    ) {
        data class OpeningHours(
            @SerializedName("endTime")
            val endTime: String?,
            @SerializedName("startTime")
            val startTime: String?
        )

        fun toModel(): AppearanceDayModel {
            when (dayOfTheWeek) {
                "MONDAY" -> {
                    dayOfTheWeek = "월요일"
                }
                "TUESDAY" -> {
                    dayOfTheWeek = "화요일"
                }
                "WEDNESDAY" -> {
                    dayOfTheWeek = "수요일"
                }
                "THURSDAY" -> {
                    dayOfTheWeek = "목요일"
                }
                "FRIDAY" -> {
                    dayOfTheWeek = "금요일"
                }
                "SATURDAY" -> {
                    dayOfTheWeek = "토요일"
                }
                "SUNDAY" -> {
                    dayOfTheWeek = "일요일"
                }
            }
            return AppearanceDayModel(
                dayOfTheWeek = dayOfTheWeek,
                locationDescription = locationDescription,
                openingHours = "${openingHours?.startTime}-${openingHours?.endTime}"
            )
        }
    }
}