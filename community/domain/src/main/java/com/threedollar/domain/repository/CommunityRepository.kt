package com.threedollar.domain.repository

import com.home.domain.data.store.ReportReasonsModel
import com.home.domain.request.ReportReasonsGroupType
import com.threedollar.domain.data.Category
import com.threedollar.domain.data.CommentId
import com.threedollar.domain.data.CreatePolicy
import com.threedollar.domain.data.DefaultResponse
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.domain.data.PollComment
import com.threedollar.domain.data.PollCommentList
import com.threedollar.domain.data.PollId
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.PollList
import com.threedollar.domain.data.PopularStores
import com.threedollar.domain.data.UserPollItemList
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {

    fun createPoll(pollCreateApiRequest: PollCreateApiRequest): Flow<PollId>
    fun getPollId(id: String): Flow<PollItem>
    fun putPollChoice(id: String, optionId: String): Flow<DefaultResponse>
    fun deletePollChoice(id: String): Flow<DefaultResponse>
    fun reportPoll(id: String, reason: String, reasonDetail: String?): Flow<DefaultResponse>
    fun getPollCategories(): Flow<List<Category>>
    fun getPollList(categoryId: String, sortType: String, cursor: String): Flow<PollList>
    fun getPollPolicy(): Flow<CreatePolicy>
    fun getUserPollList(cursor: Int?): Flow<UserPollItemList>
    fun createPollComment(id: String, content: String): Flow<CommentId>
    fun deletePollComment(pollId: String, commentId: String): Flow<DefaultResponse>
    fun editPollComment(pollId: String, commentId: String, content: String): Flow<DefaultResponse>
    fun reportPollComment(pollId: String, commentId: String, reason: String, reasonDetail: String?): Flow<DefaultResponse>
    fun getPollCommentList(id: String, cursor: String?): Flow<PollCommentList>
    fun getNeighborhoods(): Flow<Neighborhoods>
    fun getPopularStores(criteria: String, district: String, cursor: String): Flow<PopularStores>
    fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<ReportReasonsModel>
}