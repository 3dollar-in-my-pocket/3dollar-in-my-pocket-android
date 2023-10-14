package com.threedollar.domain.repository

import com.threedollar.domain.data.Category
import com.threedollar.domain.data.CommentId
import com.threedollar.domain.data.CreatePolicy
import com.threedollar.domain.data.DefaultResponse
import com.threedollar.domain.data.PollComment
import com.threedollar.domain.data.PollId
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.UserPollItem
import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {

    fun createPoll(pollCreateApiRequest: PollCreateApiRequest): Flow<PollId>
    fun getPollId(id: String): Flow<PollItem>
    fun putPollChoice(id: String): Flow<DefaultResponse>
    fun deletePollChoice(id: String): Flow<DefaultResponse>
    fun reportPoll(id: String): Flow<DefaultResponse>
    fun getPollCategories(): Flow<List<Category>>
    fun getPollList(categoryId: String, sortType: String?, cursor: Int?): Flow<List<PollItem>>
    fun getPollPolicy(): Flow<CreatePolicy>
    fun getUserPollList(cursor: Int?): Flow<List<UserPollItem>>
    fun createPollComment(id: String, pollCommentApiRequest: PollCommentApiRequest): Flow<CommentId>
    fun deletePollComment(pollId: String, commentId: String): Flow<DefaultResponse>
    fun editPollComment(pollId: String, commentId: String): Flow<DefaultResponse>
    fun reportPollComment(pollId: String, commentId: String): Flow<DefaultResponse>
    fun getPollCommentList(id: String, cursor: Int?): Flow<List<PollComment>>

}