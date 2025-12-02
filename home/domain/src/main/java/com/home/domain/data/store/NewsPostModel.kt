package com.home.domain.data.store

data class NewsPostModel(
    val postId: String,
    val body: String,
    val sections: List<SectionModel>,
    val isOwner: Boolean,
    val stickers: List<StickerModel>,
    val createdAt: String,
    val updatedAt: String
)

enum class SectionTypeModel {
    IMAGE, UNKNOWN
}

data class SectionModel(
    val sectionType: SectionTypeModel,
    val url: String,
    val ratio: Float
)