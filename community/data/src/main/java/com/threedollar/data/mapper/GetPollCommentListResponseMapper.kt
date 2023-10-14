package com.threedollar.data.mapper

import com.threedollar.common.utils.toDefaultInt
import com.threedollar.domain.data.Cursor
import com.threedollar.domain.data.PollComment
import com.threedollar.domain.data.PollCommentList
import com.threedollar.network.data.poll.response.GetPollCommentListResponse

object GetPollCommentListResponseMapper {

    fun GetPollCommentListResponse.toMapper(): PollCommentList {
        return PollCommentList(this.contents.orEmpty().map { it.toMapper() }, this.cursor.toMapper())
    }

    private fun GetPollCommentListResponse.Content.toMapper(): PollComment {
        return PollComment(this.current.toMapper())
    }

    private fun GetPollCommentListResponse.Content.Current?.toMapper(): PollComment.Current {
        return PollComment.Current(
            comment = this?.comment.toMapper(),
            commentReport = this?.commentReport.toMapper(),
            commentWriter = this?.commentWriter.toMapper(),
            poll = this?.poll.toMapper()
        )
    }

    private fun GetPollCommentListResponse.Content.Current.Comment?.toMapper(): PollComment.Current.Comment {
        return PollComment.Current.Comment(
            commentId = this?.commentId.orEmpty(),
            content = this?.content.orEmpty(),
            createdAt = this?.createdAt.orEmpty(),
            isOwner = this?.isOwner ?: false,
            status = this?.commentId.orEmpty(),
            updatedAt = this?.updatedAt.orEmpty()
        )
    }

    private fun GetPollCommentListResponse.Content.Current.CommentReport?.toMapper(): PollComment.Current.CommentReport {
        return PollComment.Current.CommentReport(this?.reportedByMe ?: false)
    }

    private fun GetPollCommentListResponse.Content.Current.CommentWriter?.toMapper(): PollComment.Current.CommentWriter {
        return PollComment.Current.CommentWriter(
            medal = this?.medal.toMapper(),
            name = this?.name.orEmpty(),
            socialType = this?.socialType.orEmpty(),
            userId = this?.userId.toDefaultInt()
        )
    }

    private fun GetPollCommentListResponse.Content.Current.CommentWriter.Medal?.toMapper(): PollComment.Current.CommentWriter.Medal {
        return PollComment.Current.CommentWriter.Medal(
            acquisition = this?.acquisition.toMapper(),
            createdAt = this?.createdAt.orEmpty(),
            disableIconUrl = this?.disableIconUrl.orEmpty(),
            iconUrl = this?.iconUrl.orEmpty(),
            introduction = this?.introduction.orEmpty(),
            medalId = this?.medalId.toDefaultInt(),
            name = this?.name.orEmpty(),
            updatedAt = this?.updatedAt.orEmpty()
        )
    }

    private fun GetPollCommentListResponse.Content.Current.CommentWriter.Medal.Acquisition?.toMapper(): PollComment.Current.CommentWriter.Medal.Acquisition {
        return PollComment.Current.CommentWriter.Medal.Acquisition(this?.description.orEmpty())
    }

    private fun GetPollCommentListResponse.Content.Current.Poll?.toMapper(): PollComment.Current.Poll {
        return PollComment.Current.Poll(this?.isWriter ?: false, this?.selectedOptions.orEmpty().map { it.toMapper() })
    }

    private fun GetPollCommentListResponse.Content.Current.Poll.SelectedOption?.toMapper(): PollComment.Current.Poll.SelectedOption {
        return PollComment.Current.Poll.SelectedOption(name = this?.name.orEmpty(), optionId = this?.optionId.orEmpty())
    }

    private fun GetPollCommentListResponse.Cursor?.toMapper(): Cursor {
        return Cursor(this?.nextCursor.orEmpty(), this?.hasMore ?: false)
    }

}