package com.zion830.threedollars.ui.like

import com.threedollar.domain.home.data.store.StickerModel

/** 서버가 스티커를 내려주지 않을 때 쓰는 좋아요 스티커 id. 가게 상세·소식 좋아요와 같다. */
private const val DEFAULT_LIKE_STICKER_ID = "LIKE"

/** 좋아요 버튼이 보여줄 스티커. 응답 `stickers` 의 첫 항목이고, 없으면 0개·안 누른 상태다. */
val List<StickerModel>.likeSticker: StickerModel
    get() = firstOrNull() ?: StickerModel(stickerId = DEFAULT_LIKE_STICKER_ID, emoji = "", count = 0, reactedByMe = false)

/** 스티커 교체 API 에 보낼 id. 이미 누른 상태면 null 을 보내 빈 목록으로 취소한다. */
fun List<StickerModel>.likeRequestStickerId(): String? = likeSticker.takeUnless { it.reactedByMe }?.stickerId

/** 첫 스티커의 눌림 상태를 뒤집고 개수를 ±1 한 스티커 목록. 개수는 0 아래로 내려가지 않는다. */
fun List<StickerModel>.toggledLike(): List<StickerModel> {
    val sticker = likeSticker
    val reacted = !sticker.reactedByMe
    val toggled = sticker.copy(
        reactedByMe = reacted,
        count = (sticker.count + if (reacted) 1 else -1).coerceAtLeast(0),
    )
    return listOf(toggled) + drop(1)
}
