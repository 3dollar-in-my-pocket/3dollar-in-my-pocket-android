package com.threedollar.data.datasource

import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.neighborhood.GetNeighborhoodsResponse
import com.threedollar.network.data.poll.request.PollChoiceApiRequest
import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import com.threedollar.network.data.poll.response.GetPollCommentListResponse
import com.threedollar.network.data.poll.response.GetPollResponse
import com.threedollar.network.data.poll.response.GetUserPollListResponse
import com.threedollar.network.data.poll.response.PollCategoryApiResponse
import com.threedollar.network.data.poll.response.PollCommentCreateApiResponse
import com.threedollar.network.data.poll.response.PollCreateApiResponse
import com.threedollar.network.data.poll.response.PollPolicyApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import zion830.com.common.base.BaseResponse
import javax.inject.Inject

class CommunityDataSourceImpl(@Inject private val serverApi: ServerApi) : CommunityDataSource {
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

    override fun reportPoll(id: String): Flow<BaseResponse<String>> = flow {
        emit(serverApi.reportPoll(id))
    }

    override fun getPollCategories(): Flow<BaseResponse<PollCategoryApiResponse>> = flow {
        emit(serverApi.getPollCategories())
    }

    override fun getPollPolicy(): Flow<BaseResponse<PollPolicyApiResponse>> = flow {
        emit(serverApi.getPollPolicy())
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

    override fun editPollComment(pollId: String, commentId: String): Flow<BaseResponse<String>> = flow {
        emit(serverApi.editPollComment(pollId, commentId))
    }

    override fun reportPollComment(pollId: String, commentId: String): Flow<BaseResponse<String>> = flow {
        emit(serverApi.reportPollComment(pollId, commentId))
    }

    override fun getPollCommentList(id: String, cursor: Int?, size: Int): Flow<BaseResponse<GetPollCommentListResponse>> = flow {
        emit(serverApi.getPollCommentList(id, cursor, size))
    }

    override fun getNeighborhoods(): Flow<BaseResponse<GetNeighborhoodsResponse>> = flow {
        emit(serverApi.getNeighborhoods())
    }
}