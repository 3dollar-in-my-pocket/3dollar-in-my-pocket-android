package com.threedollar.domain.repository

import com.home.domain.data.store.ReportReasonsModel
import com.home.domain.request.ReportReasonsGroupType
import com.threedollar.common.base.BaseResponse
import com.threedollar.domain.data.AdvertisementModelV2
import com.threedollar.domain.data.Category
import com.threedollar.domain.data.CommentId
import com.threedollar.domain.data.CreatePolicy
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.domain.data.PollCommentList
import com.threedollar.domain.data.PollId
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.PollList
import com.threedollar.domain.data.PopularStores
import com.threedollar.domain.data.UserPollItemList
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun createPoll(pollCreateApiRequest: PollCreateApiRequest): Flow<BaseResponse<PollId>>
    fun getPollId(id: String): Flow<BaseResponse<PollItem>>
    fun putPollChoice(id: String, optionId: String): Flow<BaseResponse<String>>
    fun deletePollChoice(id: String): Flow<BaseResponse<String>>
    fun reportPoll(id: String, reason: String, reasonDetail: String?): Flow<BaseResponse<String>>
    fun getPollCategories(): Flow<BaseResponse<List<Category>>>
    fun getPollList(categoryId: String, sortType: String, cursor: String): Flow<BaseResponse<PollList>>
    fun getPollPolicy(): Flow<BaseResponse<CreatePolicy>>
    fun getUserPollList(cursor: Int?): Flow<BaseResponse<UserPollItemList>>
    fun createPollComment(id: String, content: String): Flow<BaseResponse<CommentId>>
    fun deletePollComment(pollId: String, commentId: String): Flow<BaseResponse<String>>
    fun editPollComment(pollId: String, commentId: String, content: String): Flow<BaseResponse<String>>
    fun reportPollComment(pollId: String, commentId: String, reason: String, reasonDetail: String?): Flow<BaseResponse<String>>
    fun getPollCommentList(id: String, cursor: String?): Flow<BaseResponse<PollCommentList>>
    fun getNeighborhoods(): Flow<BaseResponse<Neighborhoods>>
    fun getPopularStores(criteria: String, district: String, cursor: String): Flow<BaseResponse<PopularStores>>
    fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<BaseResponse<ReportReasonsModel>>
    fun getAdvertisements(position: String): Flow<BaseResponse<List<AdvertisementModelV2>>>
}