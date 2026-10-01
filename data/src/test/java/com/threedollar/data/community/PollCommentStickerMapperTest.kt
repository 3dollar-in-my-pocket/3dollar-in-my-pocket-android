package com.threedollar.data.community

import com.google.gson.Gson
import com.threedollar.data.community.mapper.GetPollCommentListResponseMapper.toMapper
import com.threedollar.network.data.poll.response.GetPollCommentListResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PollCommentStickerMapperTest {

    // TH-989 TC1
    @Test
    fun `TH989_TC1_댓글응답의_stickers를_count와_reactedByMe로_매핑한다`() {
        // Given
        val json = """
            {"current": {
              "comment": {"commentId": "10", "content": "슈붕", "isOwner": false, "status": "ACTIVE"},
              "commentReport": {"reportedByMe": false},
              "stickers": [{"stickerId": "LIKE", "emoji": "❤️", "count": 4, "reactedByMe": true}]
            }}
        """.trimIndent()

        // When
        val comment = Gson().fromJson(json, GetPollCommentListResponse.Content::class.java).toMapper()

        // Then
        val sticker = comment.current.stickers.single()
        assertEquals("LIKE", sticker.stickerId)
        assertEquals(4, sticker.count)
        assertTrue(sticker.reactedByMe)
    }

    // TH-989 TC1
    @Test
    fun `TH989_TC1_stickers가_없거나_id가_빈_항목은_크래시없이_빈목록으로_둔다`() {
        // Given
        val withoutStickers = """{"current": {"comment": {"commentId": "10"}}}"""
        val withBrokenSticker = """{"current": {"comment": {"commentId": "11"}, "stickers": [null, {"count": 3}]}}"""

        // When
        val first = Gson().fromJson(withoutStickers, GetPollCommentListResponse.Content::class.java).toMapper()
        val second = Gson().fromJson(withBrokenSticker, GetPollCommentListResponse.Content::class.java).toMapper()

        // Then
        assertTrue(first.current.stickers.isEmpty())
        assertTrue(second.current.stickers.isEmpty())
    }
}
