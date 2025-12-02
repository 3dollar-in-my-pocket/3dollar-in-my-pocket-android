package com.threedollar.network.data.advertisement


import com.google.gson.annotations.SerializedName

data class AdvertisementResponse(
    @SerializedName("advertisements")
    val advertisements: List<Advertisement>? = null
) {
    data class Advertisement(
        @SerializedName("advertisementId")
        val advertisementId: Int? = null, // 99
        @SerializedName("title")
        val title: Title? = null,
        @SerializedName("subTitle")
        val subTitle: SubTitle? = null,
        @SerializedName("extra")
        val extra: Extra? = null,
        @SerializedName("background")
        val background: Background? = null,
        @SerializedName("link")
        val link: Link? = null,
        @SerializedName("image")
        val image: Image? = null,
        @SerializedName("metadata")
        val metadata: MetaData? = null, // null
    ) {

        data class MetaData(
            @SerializedName("exposureIndex")
            val exposureIndex: Int? = null
        )

        data class Background(
            @SerializedName("color")
            val color: String? = null // #FFFFFF
        )

        data class Extra(
            @SerializedName("content")
            val content: String? = null, // 인스타 친구하쟈
            @SerializedName("fontColor")
            val fontColor: String? = null // #ffffff
        )

        data class Image(
            @SerializedName("height")
            val height: Int? = null, // 328
            @SerializedName("url")
            val url: String? = null, // https://storage.threedollars.co.kr/popup/v1-1771b1cc-fb16-4286-999b-6c7f188e29d8.png
            @SerializedName("width")
            val width: Int? = null // 343
        )

        data class Link(
            @SerializedName("type")
            val type: String? = null, // WEB
            @SerializedName("url")
            val url: String? = null // https://www.instagram.com/rolling_moongzzi/
        )

        data class SubTitle(
            @SerializedName("content")
            val content: String? = null, // 눈뭉찌와 벼리는 행복을 찾아서 굴러와찌!우리랑 같이 놀래?
            @SerializedName("fontColor")
            val fontColor: String? = null // #969696
        )

        data class Title(
            @SerializedName("content")
            val content: String? = null, // 쩝쩝박사 눈뭉찌 등장!
            @SerializedName("fontColor")
            val fontColor: String? = null // #0F0F0F
        )
    }
}