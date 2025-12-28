package com.threedollar.domain.community.repository

import com.threedollar.domain.community.model.ReportReasonsModel
import com.threedollar.domain.community.model.ReportReasonsGroupType
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.domain.community.data.AdvertisementModelV2
import com.threedollar.domain.community.data.Category
import com.threedollar.domain.community.data.CommentId
import com.threedollar.domain.community.data.CreatePolicy
import com.threedollar.domain.community.data.Neighborhoods
import com.threedollar.domain.community.data.PollCommentList
import com.threedollar.domain.community.data.PollId
import com.threedollar.domain.community.data.PollItem
import com.threedollar.domain.community.data.PollList
import com.threedollar.domain.community.data.PopularStores
import com.threedollar.domain.community.data.UserPollItemList
import com.threedollar.domain.community.model.PollCreateModel
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun createPoll(pollCreateModel: PollCreateModel): Flow<BaseResponse<PollId>>
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
    fun getAdvertisements(
        position: AdvertisementsPosition,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<List<AdvertisementModelV2>>>
}