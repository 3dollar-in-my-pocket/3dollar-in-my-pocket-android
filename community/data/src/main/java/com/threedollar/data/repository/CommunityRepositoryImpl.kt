package com.threedollar.data.repository

import com.home.domain.data.store.ReportReasonsModel
import com.home.domain.request.ReportReasonsGroupType
import com.threedollar.data.datasource.CommunityDataSource
import com.threedollar.data.mapper.asModel
import com.threedollar.data.mapper.toCommentIdMapper
import com.threedollar.data.mapper.toCreatePolicyMapper
import com.threedollar.data.mapper.toDefaultResponseMapper
import com.threedollar.data.mapper.toListCategoryMapper
import com.threedollar.data.mapper.toNeighborhoodsMapper
import com.threedollar.data.mapper.toPollCommentListMapper
import com.threedollar.data.mapper.toPollIdMapper
import com.threedollar.data.mapper.toPollItemMapper
import com.threedollar.data.mapper.toPollListMapper
import com.threedollar.data.mapper.toPopularStoresMapper
import com.threedollar.data.mapper.toUserPollItemListMapper
import com.threedollar.domain.data.Category
import com.threedollar.domain.data.CommentId
import com.threedollar.domain.data.CreatePolicy
import com.threedollar.domain.data.DefaultResponse
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.domain.data.PollCommentList
import com.threedollar.domain.data.PollId
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.PollList
import com.threedollar.domain.data.PopularStores
import com.threedollar.domain.data.UserPollItemList
import com.threedollar.domain.repository.CommunityRepository
import com.threedollar.network.data.poll.request.PollChoiceApiRequest
import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import com.threedollar.network.data.poll.request.PollReportCreateApiRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(private val communityDataSource: CommunityDataSource) :
    CommunityRepository {
    override fun createPoll(pollCreateApiRequest: PollCreateApiRequest): Flow<PollId> =
        communityDataSource.createPoll(pollCreateApiRequest).map { it.data.toPollIdMapper() }

    override fun getPollId(id: String): Flow<PollItem> = communityDataSource.getPollId(id).map { it.data.toPollItemMapper() }

    override fun putPollChoice(id: String, optionId: String): Flow<DefaultResponse> =
        communityDataSource.putPollChoice(id, PollChoiceApiRequest(listOf(PollChoiceApiRequest.Option(optionId))))
            .map { it.toDefaultResponseMapper() }

    override fun deletePollChoice(id: String): Flow<DefaultResponse> = communityDataSource.deletePollChoice(id).map { it.toDefaultResponseMapper() }

    override fun reportPoll(id: String, reason: String, reasonDetail: String?): Flow<DefaultResponse> = communityDataSource.reportPoll(
        id,
        PollReportCreateApiRequest(reason, reasonDetail)
    ).map { it.toDefaultResponseMapper() }

    override fun getPollCategories(): Flow<List<Category>> = communityDataSource.getPollCategories().map { it.data.toListCategoryMapper() }
    override fun getPollList(categoryId: String, sortType: String, cursor: String): Flow<PollList> =
        communityDataSource.getPollList(categoryId, sortType, cursor).map { it.data.toPollListMapper() }

    override fun getPollPolicy(): Flow<CreatePolicy> = communityDataSource.getPollPolicy().map { it.data.toCreatePolicyMapper() }

    override fun getUserPollList(cursor: Int?): Flow<UserPollItemList> =
        communityDataSource.getUserPollList(cursor).map { it.data.toUserPollItemListMapper() }

    override fun createPollComment(id: String, content: String): Flow<CommentId> =
        communityDataSource.createPollComment(id, PollCommentApiRequest(content)).map { it.data.toCommentIdMapper() }

    override fun deletePollComment(pollId: String, commentId: String): Flow<DefaultResponse> =
        communityDataSource.deletePollComment(pollId, commentId).map { it.toDefaultResponseMapper() }

    override fun editPollComment(pollId: String, commentId: String, content: String): Flow<DefaultResponse> =
        communityDataSource.editPollComment(pollId, commentId, PollCommentApiRequest(content)).map { it.toDefaultResponseMapper() }

    override fun reportPollComment(pollId: String, commentId: String, reason: String, reasonDetail: String?): Flow<DefaultResponse> =
        communityDataSource.reportPollComment(pollId, commentId, PollReportCreateApiRequest(reason, reasonDetail))
            .map { it.toDefaultResponseMapper() }

    override fun getPollCommentList(id: String, cursor: String?): Flow<PollCommentList> =
        communityDataSource.getPollCommentList(id, cursor).map { it.data.toPollCommentListMapper() }

    override fun getNeighborhoods(): Flow<Neighborhoods> = communityDataSource.getNeighborhoods().map { it.data.toNeighborhoodsMapper() }
    override fun getPopularStores(criteria: String, district: String, cursor: String): Flow<PopularStores> =
        communityDataSource.getPopularStores(criteria, district, cursor).map { it.data.toPopularStoresMapper() }

    override fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<ReportReasonsModel> =
        communityDataSource.getReportReasons(reportReasonsGroupType).map { it.data.asModel() }

}