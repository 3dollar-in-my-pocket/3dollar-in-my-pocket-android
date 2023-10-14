package com.threedollar.data.repository

import com.threedollar.domain.data.Category
import com.threedollar.domain.data.CommentId
import com.threedollar.domain.data.CreatePolicy
import com.threedollar.domain.data.DefaultResponse
import com.threedollar.domain.data.PollComment
import com.threedollar.domain.data.PollId
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.UserPollItem
import com.threedollar.domain.repository.CommunityRepository
import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(private val communityRepository: CommunityRepository) : CommunityRepository {
    override fun createPoll(pollCreateApiRequest: PollCreateApiRequest): Flow<PollId> = communityRepository.createPoll(pollCreateApiRequest)

    override fun getPollId(id: String): Flow<PollItem> = communityRepository.getPollId(id)

    override fun putPollChoice(id: String): Flow<DefaultResponse> = communityRepository.putPollChoice(id)

    override fun deletePollChoice(id: String): Flow<DefaultResponse> = communityRepository.deletePollChoice(id)

    override fun reportPoll(id: String): Flow<DefaultResponse> = communityRepository.reportPoll(id)

    override fun getPollCategories(): Flow<List<Category>> = communityRepository.getPollCategories()

    override fun getPollList(categoryId: String, sortType: String?, cursor: Int?): Flow<List<PollItem>> =
        communityRepository.getPollList(categoryId, sortType, cursor)

    override fun getPollPolicy(): Flow<CreatePolicy> = communityRepository.getPollPolicy()

    override fun getUserPollList(cursor: Int?): Flow<List<UserPollItem>> = communityRepository.getUserPollList(cursor)

    override fun createPollComment(id: String, pollCommentApiRequest: PollCommentApiRequest): Flow<CommentId> =
        communityRepository.createPollComment(id, pollCommentApiRequest)

    override fun deletePollComment(pollId: String, commentId: String): Flow<DefaultResponse> =
        communityRepository.deletePollComment(pollId, commentId)

    override fun editPollComment(pollId: String, commentId: String): Flow<DefaultResponse> = communityRepository.editPollComment(pollId, commentId)

    override fun reportPollComment(pollId: String, commentId: String): Flow<DefaultResponse> =
        communityRepository.reportPollComment(pollId, commentId)

    override fun getPollCommentList(id: String, cursor: Int?): Flow<List<PollComment>> = communityRepository.getPollCommentList(id, cursor)

}