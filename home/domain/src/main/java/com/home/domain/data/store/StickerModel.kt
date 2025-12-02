package com.home.domain.data.store

data class StickerModel(
    val stickerId: String,
    val emoji: String,
    val count: Int,
    val reactedByMe: Boolean
)