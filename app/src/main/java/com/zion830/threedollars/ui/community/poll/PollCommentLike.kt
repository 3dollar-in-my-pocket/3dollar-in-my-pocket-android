package com.zion830.threedollars.ui.community.poll

import com.threedollar.domain.community.data.PollComment
import com.zion830.threedollars.ui.like.toggledLike

/** 좋아요를 누르거나 취소한 뒤의 댓글. 첫 스티커의 눌림 상태와 개수만 바뀐다. */
fun PollComment.withToggledLike(): PollComment = copy(current = current.copy(stickers = current.stickers.toggledLike()))

/** 목록에서 같은 댓글을 [comment] 로 바꾼다. 없으면 그대로 둔다. */
fun List<PollComment>.replaceComment(comment: PollComment): List<PollComment> {
    val commentId = comment.current.comment.commentId
    return map { if (it.current.comment.commentId == commentId) comment else it }
}
