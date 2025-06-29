package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class StickerRequest(
    @SerializedName("stickers")
    val stickers: List<Sticker>
) {
    data class Sticker(
        @SerializedName("stickerId")
        val stickerId: String = ""
    )
}