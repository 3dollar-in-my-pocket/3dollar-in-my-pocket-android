package com.zion830.threedollars.ui.community.poll

import com.threedollar.domain.community.data.PollComment
import com.threedollar.domain.home.data.store.StickerModel
import com.zion830.threedollars.ui.like.likeSticker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class PollCommentLikeTest {

    // TH-989 TC2
    @Test
    fun `TH989_TC2_댓글좋아요를_누르면_해당댓글의_스티커만_바뀐다`() {
        // Given
        val comment = comment(id = "1", count = 0, reactedByMe = false)

        // When
        val toggled = comment.withToggledLike()

        // Then
        assertTrue(toggled.current.stickers.likeSticker.reactedByMe)
        assertEquals(1, toggled.current.stickers.likeSticker.count)
        assertEquals(comment.current.comment, toggled.current.comment)
    }

    // TH-989 TC4
    @Test
    fun `TH989_TC4_내가쓴_댓글도_같은규칙으로_토글된다`() {
        // Given
        val mine = comment(id = "1", count = 3, reactedByMe = false, isOwner = true)

        // When
        val toggled = mine.withToggledLike()

        // Then
        assertEquals(4, toggled.current.stickers.likeSticker.count)
    }

    // TH-989 TC11
    @Test
    fun `TH989_TC11_좋아요_반영은_같은id_댓글만_바꾸고_순서를_유지한다`() {
        // Given
        val comments = listOf(comment("1", 0, false), comment("2", 1, false), comment("3", 2, true))
        val toggled = comments[1].withToggledLike()

        // When
        val result = comments.replaceComment(toggled)

        // Then
        assertEquals(listOf("1", "2", "3"), result.map { it.current.comment.commentId })
        assertSame(comments[0], result[0])
        assertSame(toggled, result[1])
        assertSame(comments[2], result[2])
    }

    private fun comment(id: String, count: Int, reactedByMe: Boolean, isOwner: Boolean = false) = PollComment(
        current = PollComment.Current(
            comment = PollComment.Current.Comment(
                commentId = id,
                content = "댓글 $id",
                createdAt = "",
                isOwner = isOwner,
                status = "ACTIVE",
                updatedAt = "",
            ),
            commentReport = PollComment.Current.CommentReport(reportedByMe = false),
            commentWriter = PollComment.Current.CommentWriter(
                medal = PollComment.Current.CommentWriter.Medal(
                    acquisition = PollComment.Current.CommentWriter.Medal.Acquisition(description = ""),
                    createdAt = "",
                    disableIconUrl = "",
                    iconUrl = "",
                    introduction = "",
                    medalId = 0,
                    name = "",
                    updatedAt = "",
                ),
                name = "",
                socialType = "",
                userId = 0,
            ),
            poll = PollComment.Current.Poll(isWriter = false, selectedOptions = emptyList()),
            stickers = listOf(StickerModel(stickerId = "LIKE", emoji = "", count = count, reactedByMe = reactedByMe)),
        ),
    )
}
