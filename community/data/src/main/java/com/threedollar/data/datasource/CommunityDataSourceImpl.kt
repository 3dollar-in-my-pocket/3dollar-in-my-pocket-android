package com.threedollar.data.datasource

import com.home.domain.request.ReportReasonsGroupType
import com.threedollar.common.base.BaseResponse
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.ReportReasonsResponse
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
import com.threedollar.network.util.apiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommunityDataSourceImpl @Inject constructor(private val serverApi: ServerApi) : CommunityDataSource {
    override fun createPoll(pollCreateApiRequest: PollCreateApiRequest): Flow<BaseResponse<PollCreateApiResponse>> = flow {
        emit(serverApi.createPoll(pollCreateApiRequest))
    }

    override fun getPollId(id: String): Flow<BaseResponse<GetPollResponse>> = flow {
        emit(serverApi.getPollId(id))
    }

    override fun putPollChoice(id: String, pollChoiceApiRequest: PollChoiceApiRequest): Flow<BaseResponse<String>> = flow {
        emit(serverApi.putPollChoice(id, pollChoiceApiRequest))
    }

    override fun deletePollChoice(id: String): Flow<BaseResponse<String>> = flow {
        emit(serverApi.deletePollChoice(id))
    }

    override fun reportPoll(id: String, pollReportCreateApiRequest: PollReportCreateApiRequest): Flow<BaseResponse<String>> = flow {
        emit(serverApi.reportPoll(id, pollReportCreateApiRequest))
    }

    override fun getPollCategories(): Flow<BaseResponse<PollCategoryApiResponse>> = flow {
        emit(serverApi.getPollCategories())
    }

    override fun getPollPolicy(): Flow<BaseResponse<PollPolicyApiResponse>> = flow {
        emit(serverApi.getPollPolicy())
    }

    override fun getPollList(categoryId: String, sortType: String, cursor: String): Flow<BaseResponse<GetPollListResponse>> = flow{
        emit(serverApi.getPollList(categoryId, sortType, cursor))
    }

    override fun getUserPollList(cursor: Int?, size: Int): Flow<BaseResponse<GetUserPollListResponse>> = flow {
        emit(serverApi.getUserPollList(cursor, size))
    }

    override fun createPollComment(id: String, pollCommentApiRequest: PollCommentApiRequest): Flow<BaseResponse<PollCommentCreateApiResponse>> =
        flow {
            emit(serverApi.createPollComment(id, pollCommentApiRequest))
        }

    override fun deletePollComment(pollId: String, commentId: String): Flow<BaseResponse<String>> = flow {
        emit(serverApi.deletePollComment(pollId, commentId))
    }

    override fun editPollComment(pollId: String, commentId: String, pollCommentApiRequest: PollCommentApiRequest): Flow<BaseResponse<String>> = flow {
        emit(serverApi.editPollComment(pollId, commentId, pollCommentApiRequest))
    }

    override fun reportPollComment(
        pollId: String,
        commentId: String,
        pollReportCreateApiRequest: PollReportCreateApiRequest
    ): Flow<BaseResponse<String>> = flow {
        emit(serverApi.reportPollComment(pollId, commentId, pollReportCreateApiRequest))
    }

    override fun getPollCommentList(id: String, cursor: String?, size: Int): Flow<BaseResponse<GetPollCommentListResponse>> = flow {
        emit(serverApi.getPollCommentList(id, cursor, size))
    }

    override fun getPopularStores(criteria: String, district: String, cursor: String): Flow<BaseResponse<GetPopularStoresResponse>> = flow {
        emit(serverApi.getPopularStores(criteria = criteria, district = district, cursor = cursor))
    }

    override fun getNeighborhoods(): Flow<BaseResponse<GetNeighborhoodsResponse>> = flow {
        emit(serverApi.getNeighborhoods())
    }

    override fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<BaseResponse<ReportReasonsResponse>> = flow {
        emit(apiResult(serverApi.getReportReasons(reportReasonsGroupType.name)))
    }
}