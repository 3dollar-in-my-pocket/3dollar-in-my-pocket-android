package com.home.domain.data.advertisement

import com.threedollar.common.data.AdAndStoreItem


data class AdvertisementModelV2(
    val advertisementId: Int, // 99
    val background: Background,
    val extra: Extra,
    val image: Image,
    val link: Link,
    val metadata: MetaData, // null
    val subTitle: SubTitle,
    val title: Title
) : AdAndStoreItem {
    data class Background(
        val color: String // #FFFFFF
    )

    data class Extra(
        val content: String, // 인스타 친구하쟈
        val fontColor: String // #ffffff
    )

    data class Image(
        val height: Int, // 328
        val url: String, // https://storage.threedollars.co.kr/popup/v1-1771b1cc-fb16-4286-999b-6c7f188e29d8.png
        val width: Int // 343
    )

    data class Link(
        val type: String, // WEB
        val url: String // https://www.instagram.com/rolling_moongzzi/
    )

    data class SubTitle(
        val content: String, // 눈뭉찌와 벼리는 행복을 찾아서 굴러와찌!우리랑 같이 놀래?
        val fontColor: String // #969696
    )

    data class Title(
        val content: String, // 쩝쩝박사 눈뭉찌 등장!
        val fontColor: String // #0F0F0F
    )

    data class MetaData(
        val exposureIndex: Int
    )
}