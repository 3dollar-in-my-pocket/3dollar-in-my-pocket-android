package com.threedollar.data.datasource

import com.threedollar.network.data.neighborhood.GetNeighborhoodsResponse
import com.threedollar.network.data.neighborhood.GetPopularStoresResponse
import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import com.threedollar.network.data.poll.response.GetPollCommentListResponse
import com.threedollar.network.data.poll.response.GetPollListResponse
import com.threedollar.network.data.poll.response.GetPollResponse
import com.threedollar.network.data.poll.response.GetUserPollListResponse
import com.threedollar.network.data.poll.response.PollCategoryApiResponse
import com.threedollar.network.data.poll.response.PollCommentCreateApiResponse
import com.threedollar.network.data.poll.response.PollCreateApiResponse
import com.threedollar.network.data.poll.response.PollPolicyApiResponse
import kotlinx.coroutines.flow.Flow
import zion830.com.common.base.BaseResponse

interface CommunityDataSource {
    fun createPoll(pollCreateApiRequest: PollCreateApiRequest): Flow<BaseResponse<PollCreateApiResponse>>
    fun getPollId(id: String): Flow<BaseResponse<GetPollResponse>>
    fun putPollChoice(id: String): Flow<BaseResponse<String>>
    fun deletePollChoice(id: String): Flow<BaseResponse<String>>
    fun reportPoll(id: String): Flow<BaseResponse<String>>
    fun getPollCategories(): Flow<BaseResponse<PollCategoryApiResponse>>
    fun getPollList(categoryId: String, sortType: String?, cursor: Int?, size: Int = 20): Flow<BaseResponse<GetPollListResponse>>
    fun getPollPolicy(): Flow<BaseResponse<PollPolicyApiResponse>>
    fun getUserPollList(cursor: Int?, size: Int = 20): Flow<BaseResponse<GetUserPollListResponse>>
    fun createPollComment(id: String, pollCommentApiRequest: PollCommentApiRequest): Flow<BaseResponse<PollCommentCreateApiResponse>>
    fun deletePollComment(pollId: String, commentId: String): Flow<BaseResponse<String>>
    fun editPollComment(pollId: String, commentId: String): Flow<BaseResponse<String>>
    fun reportPollComment(pollId: String, commentId: String): Flow<BaseResponse<String>>
    fun getPollCommentList(id: String, cursor: Int?, size: Int = 20): Flow<BaseResponse<GetPollCommentListResponse>>

    fun getNeighborhoods(): Flow<BaseResponse<GetNeighborhoodsResponse>>
    fun getPopularStores(criteria: String, district: String, cursor: String, size: Int = 20): Flow<BaseResponse<GetPopularStoresResponse>>
}