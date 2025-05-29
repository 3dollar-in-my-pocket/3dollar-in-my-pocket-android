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

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

data class Section(
    @SerializedName("sectionType")
    val sectionType: SectionType,

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