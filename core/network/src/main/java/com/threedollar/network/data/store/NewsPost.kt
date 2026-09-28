package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

data class NewsPost(
    @SerializedName("postId")
    val postId: String,

    @SerializedName("body")
    val body: String,

    @SerializedName("sections")
    val sections: List<Section>,

    @SerializedName("isOwner")
    val isOwner: Boolean,

    @SerializedName("stickers")
    val stickers: List<Sticker>,

    @SerializedName("store")
    val store: NewsPostStore? = null,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

/** 가게 소식 목록(`news-posts`)에서만 내려오는 작성 가게 정보. */
data class NewsPostStore(
    @SerializedName("storeId")
    val storeId: String? = null,

    @SerializedName("storeName")
    val storeName: String? = null,

    @SerializedName("categories")
    val categories: List<Category>? = null,
)

data class Section(
    /** 알 수 없는 타입이면 Gson 이 null 을 넣는다. */
    @SerializedName("sectionType")
    val sectionType: SectionType?,

    @SerializedName("url")
    val url: String,

    @SerializedName("ratio")
    val ratio: Float
)

data class Sticker(
    @SerializedName("stickerId")
    val stickerId: String,

    @SerializedName("emoji")
    val emoji: String,

    @SerializedName("count")
    val count: Int,

    @SerializedName("reactedByMe")
    val reactedByMe: Boolean
)

enum class SectionType {
    @SerializedName("IMAGE")
    IMAGE, UNKNOWN
}