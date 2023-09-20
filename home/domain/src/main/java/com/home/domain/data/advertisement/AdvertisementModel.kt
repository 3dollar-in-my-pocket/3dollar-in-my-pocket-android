package com.home.domain.data.advertisement

import com.threedollar.common.data.AdAndStoreItem

data class AdvertisementModel(
    val advertisementId: Int = 0,
    val bgColor: String? = "",
    val extraContent: String? = "",
    val fontColor: String? = "",
    val imageHeight: Int = 0,
    val imageUrl: String? = "",
    val imageWidth: Int = 0,
    val linkUrl: String? = "",
    val subTitle: String? = "",
    val title: String? = ""
) : AdAndStoreItem