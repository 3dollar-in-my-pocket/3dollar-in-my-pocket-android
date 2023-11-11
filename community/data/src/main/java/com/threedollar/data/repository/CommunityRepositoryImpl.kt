package com.threedollar.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.threedollar.data.datasource.CommunityDataSource
import com.threedollar.data.datasource.PollListDataSource
import com.threedollar.data.datasource.PopularStoresDataSource
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
import com.threedollar.domain.data.PollComment
import com.threedollar.domain.data.PollId
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.PollList
import com.threedollar.domain.data.PopularStore
import com.threedollar.domain.data.PopularStores
import com.threedollar.domain.data.UserPollItemList
import com.threedollar.domain.repository.CommunityRepository
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.poll.request.PollChoiceApiRequest
import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(private val serverApi: ServerApi, private val communityDataSource: CommunityDataSource) :
    CommunityRepository {
    override fun createPoll(pollCreateApiRequest: PollCreateApiRequest): Flow<PollId> =
        communityDataSource.createPoll(pollCreateApiRequest).map { it.data.toPollIdMapper() }

    override fun getPollId(id: String): Flow<PollItem> = communityDataSource.getPollId(id).map { it.data.toPollItemMapper() }

    override fun putPollChoice(id: String, optionId: String): Flow<DefaultResponse> =
        communityDataSource.putPollChoice(id, PollChoiceApiRequest(listOf(PollChoiceApiRequest.Option(optionId))))
            .map { it.toDefaultResponseMapper() }

    override fun deletePollChoice(id: String): Flow<DefaultResponse> = communityDataSource.deletePollChoice(id).map { it.toDefaultResponseMapper() }

    override fun reportPoll(id: String): Flow<DefaultResponse> = communityDataSource.reportPoll(id).map { it.toDefaultResponseMapper() }

    override fun getPollCategories(): Flow<List<Category>> = communityDataSource.getPollCategories().map { it.data.toListCategoryMapper() }

    override fun getPollList(categoryId: String, sortType: String): Flow<PagingData<PollItem>> = Pager(PagingConfig(20)) {
        PollListDataSource(categoryId = categoryId, sortType = sortType, serverApi = serverApi)
    }.flow

    override fun getPollListNotPaging(categoryId: String, sortType: String): Flow<PollList> = communityDataSource.getPollListNotPaging(categoryId, sortType).map {
            it.data.toPollListMapper()
        }

    override fun getPollPolicy(): Flow<CreatePolicy> = communityDataSource.getPollPolicy().map { it.data.toCreatePolicyMapper() }

    override fun getUserPollList(cursor: Int?): Flow<UserPollItemList> =
        communityDataSource.getUserPollList(cursor).map { it.data.toUserPollItemListMapper() }

    override fun createPollComment(id: String, pollCommentApiRequest: PollCommentApiRequest): Flow<CommentId> =
        communityDataSource.createPollComment(id, pollCommentApiRequest).map { it.data.toCommentIdMapper() }

    override fun deletePollComment(pollId: String, commentId: String): Flow<DefaultResponse> =
        communityDataSource.deletePollComment(pollId, commentId).map { it.toDefaultResponseMapper() }

    override fun editPollComment(pollId: String, commentId: String): Flow<DefaultResponse> =
        communityDataSource.editPollComment(pollId, commentId).map { it.toDefaultResponseMapper() }

    override fun reportPollComment(pollId: String, commentId: String): Flow<DefaultResponse> =
        communityDataSource.reportPollComment(pollId, commentId).map { it.toDefaultResponseMapper() }

    override fun getPollCommentList(id: String, cursor: Int?): Flow<List<PollComment>> =
        communityDataSource.getPollCommentList(id, cursor).map { it.data.toPollCommentListMapper().pollComments }

    override fun getNeighborhoods(): Flow<Neighborhoods> = communityDataSource.getNeighborhoods().map { it.data.toNeighborhoodsMapper() }

    override fun getPopularStores(criteria: String, district: String): Flow<PagingData<PopularStore>> = Pager(PagingConfig(20)) {
        PopularStoresDataSource(criteria = criteria, district = district, serverApi = serverApi)
    }.flow

    override fun getPopularStoresNotPaging(criteria: String, district: String): Flow<PopularStores> = communityDataSource.getPopularStoresNotPaging(criteria, district).map { it.data.toPopularStoresMapper() }

}