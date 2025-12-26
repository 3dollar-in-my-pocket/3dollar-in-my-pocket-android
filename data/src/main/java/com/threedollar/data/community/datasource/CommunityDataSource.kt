package com.threedollar.data.community.datasource

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.network.data.ReportReasonsResponse
import com.threedollar.network.data.advertisement.AdvertisementResponse
import com.threedollar.network.data.neighborhood.GetNeighborhoodsResponse
import com.threedollar.network.data.neighborhood.GetPopularStoresResponse
import com.threedollar.network.data.poll.request.PollChoiceApiRequest
import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import com.threedollar.network.data.poll.request.PollReportCreateApiRequest
import com.threedollar.network.data.poll.response.GetPollCommentListResponse
import com.threedollar.network.data.poll.response.GetPollListResponse
import com.threedollar.network.data.poll.response.GetPollResponse
import com.threedollar.network.data.poll.response.GetUserPollListResponse
import com.threedollar.network.data.poll.response.PollCategoryApiResponse
import com.threedollar.network.data.poll.response.PollCommentCreateApiResponse
import com.threedollar.network.data.poll.response.PollCreateApiResponse
import com.threedollar.network.data.poll.response.PollPolicyApiResponse
import kotlinx.coroutines.flow.Flow


interface CommunityDataSource {
    fun createPoll(pollCreateApiRequest: PollCreateApiRequest): Flow<BaseResponse<PollCreateApiResponse>>
    fun getPollId(id: String): Flow<BaseResponse<GetPollResponse>>
    fun putPollChoice(id: String, pollChoiceApiRequest: PollChoiceApiRequest): Flow<BaseResponse<String>>
    fun deletePollChoice(id: String): Flow<BaseResponse<String>>
    fun reportPoll(id: String, pollReportCreateApiRequest: PollReportCreateApiRequest): Flow<BaseResponse<String>>
    fun getPollCategories(): Flow<BaseResponse<PollCategoryApiResponse>>
    fun getPollPolicy(): Flow<BaseResponse<PollPolicyApiResponse>>
    fun getPollList(categoryId: String, sortType: String, cursor: String): Flow<BaseResponse<GetPollListResponse>>
    fun getUserPollList(cursor: Int?, size: Int = 20): Flow<BaseResponse<GetUserPollListResponse>>
    fun createPollComment(id: String, pollCommentApiRequest: PollCommentApiRequest): Flow<BaseResponse<PollCommentCreateApiResponse>>
    fun deletePollComment(pollId: String, commentId: String): Flow<BaseResponse<String>>
    fun editPollComment(pollId: String, commentId: String, pollCommentApiRequest: PollCommentApiRequest): Flow<BaseResponse<String>>
    fun reportPollComment(pollId: String, commentId: String, pollReportCreateApiRequest: PollReportCreateApiRequest): Flow<BaseResponse<String>>
    fun getPollCommentList(id: String, cursor: String?, size: Int = 20): Flow<BaseResponse<GetPollCommentListResponse>>
    fun getPopularStores(criteria: String, district: String, cursor: String): Flow<BaseResponse<GetPopularStoresResponse>>
    fun getNeighborhoods(): Flow<BaseResponse<GetNeighborhoodsResponse>>
    fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<BaseResponse<ReportReasonsResponse>>
    fun getAdvertisements(
        position: AdvertisementsPosition,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<AdvertisementResponse>>
}