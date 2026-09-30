package com.zion830.threedollars.ui.like

import com.threedollar.domain.home.data.store.StickerModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LikeStickerTest {

    private fun sticker(count: Int, reactedByMe: Boolean) =
        StickerModel(stickerId = "LIKE", emoji = "❤️", count = count, reactedByMe = reactedByMe)

    // TH-989 TC2
    @Test
    fun `TH989_TC2_안누른_좋아요를_누르면_채워지고_개수가_1늘며_스티커id를_보낸다`() {
        // Given
        val stickers = listOf(sticker(count = 2, reactedByMe = false))

        // When
        val requestId = stickers.likeRequestStickerId()
        val toggled = stickers.toggledLike().likeSticker

        // Then
        assertEquals("LIKE", requestId)
        assertTrue(toggled.reactedByMe)
        assertEquals(3, toggled.count)
    }

    // TH-989 TC3
    @Test
    fun `TH989_TC3_누른_좋아요를_다시누르면_비워지고_개수가_1줄며_빈목록을_보낸다`() {
        // Given
        val stickers = listOf(sticker(count = 1, reactedByMe = true))

        // When
        val requestId = stickers.likeRequestStickerId()
        val toggled = stickers.toggledLike().likeSticker

        // Then
        assertNull(requestId)
        assertFalse(toggled.reactedByMe)
        assertEquals(0, toggled.count)
    }

    // TH-989 TC7
    @Test
    fun `TH989_TC7_두번_뒤집으면_원래_상태로_돌아온다`() {
        // Given
        val stickers = listOf(sticker(count = 5, reactedByMe = false))

        // When
        val restored = stickers.toggledLike().toggledLike()

        // Then
        assertEquals(stickers, restored)
    }

    // TH-989 TC1
    @Test
    fun `TH989_TC1_스티커가_없으면_0개_안누른_기본_좋아요로_본다`() {
        // Given
        val stickers = emptyList<StickerModel>()

        // When
        val sticker = stickers.likeSticker

        // Then
        assertEquals(0, sticker.count)
        assertFalse(sticker.reactedByMe)
        assertEquals("LIKE", stickers.likeRequestStickerId())
    }

    // TH-989 TC3
    @Test
    fun `TH989_TC3_개수가_0인데_취소해도_음수가_되지_않는다`() {
        // Given
        val stickers = listOf(sticker(count = 0, reactedByMe = true))

        // When
        val toggled = stickers.toggledLike().likeSticker

        // Then
        assertEquals(0, toggled.count)
    }
}
