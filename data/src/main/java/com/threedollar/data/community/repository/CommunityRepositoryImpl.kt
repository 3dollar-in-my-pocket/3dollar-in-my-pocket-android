package com.threedollar.data.community.repository


import com.threedollar.common.base.BaseResponse
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.data.community.datasource.CommunityDataSource
import com.threedollar.data.community.mapper.asModel
import com.threedollar.data.community.mapper.toCommentIdMapper
import com.threedollar.data.community.mapper.toCreatePolicyMapper
import com.threedollar.data.community.mapper.toDomainModel
import com.threedollar.data.community.mapper.toListCategoryMapper
import com.threedollar.data.community.mapper.toNeighborhoodsMapper
import com.threedollar.data.community.mapper.toNetworkGroupType
import com.threedollar.data.community.mapper.toNetworkRequest
import com.threedollar.data.community.mapper.toPollCommentListMapper
import com.threedollar.data.community.mapper.toPollIdMapper
import com.threedollar.data.community.mapper.toPollItemMapper
import com.threedollar.data.community.mapper.toPollListMapper
import com.threedollar.data.community.mapper.toPopularStoresMapper
import com.threedollar.data.community.mapper.toUserPollItemListMapper
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
import com.threedollar.domain.community.model.ReportReasonsGroupType
import com.threedollar.domain.community.repository.CommunityRepository
import com.threedollar.network.data.poll.request.PollChoiceApiRequest
import com.threedollar.network.data.poll.request.PollCommentApiRequest
import com.threedollar.network.data.poll.request.PollReportCreateApiRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.threedollar.domain.community.model.ReportReasonsModel as DomainReportReasonsModel

class CommunityRepositoryImpl @Inject constructor(private val communityDataSource: CommunityDataSource) :
    CommunityRepository {
    override fun createPoll(pollCreateModel: PollCreateModel): Flow<BaseResponse<PollId>> =
        communityDataSource.createPoll(pollCreateModel.toNetworkRequest()).map {
            BaseResponse(
                ok = it.ok,
                data = it.data.toPollIdMapper(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun getPollId(id: String): Flow<BaseResponse<PollItem>> = communityDataSource.getPollId(id).map {
        BaseResponse(
            ok = it.ok,
            data = it.data.toPollItemMapper(),
            message = it.message,
            resultCode = it.resultCode,
            error = it.error
        )
    }

    override fun putPollChoice(id: String, optionId: String): Flow<BaseResponse<String>> =
        communityDataSource.putPollChoice(id, PollChoiceApiRequest(listOf(PollChoiceApiRequest.Option(optionId))))
            .map {
                BaseResponse(
                    ok = it.ok,
                    data = it.data,
                    message = it.message,
                    resultCode = it.resultCode,
                    error = it.error
                )
            }

    override fun deletePollChoice(id: String): Flow<BaseResponse<String>> = communityDataSource.deletePollChoice(id).map {
        BaseResponse(
            ok = it.ok,
            data = it.data,
            message = it.message,
            resultCode = it.resultCode,
            error = it.error
        )
    }

    override fun reportPoll(id: String, reason: String, reasonDetail: String?): Flow<BaseResponse<String>> = communityDataSource.reportPoll(
        id,
        PollReportCreateApiRequest(reason, reasonDetail)
    ).map {
        BaseResponse(
            ok = it.ok,
            data = it.data,
            message = it.message,
            resultCode = it.resultCode,
            error = it.error
        )
    }

    override fun getPollCategories(): Flow<BaseResponse<List<Category>>> = communityDataSource.getPollCategories().map {
        BaseResponse(
            ok = it.ok,
            data = it.data.toListCategoryMapper(),
            message = it.message,
            resultCode = it.resultCode,
            error = it.error
        )
    }

    override fun getPollList(categoryId: String, sortType: String, cursor: String): Flow<BaseResponse<PollList>> =
        communityDataSource.getPollList(categoryId, sortType, cursor).map {
            BaseResponse(
                ok = it.ok,
                data = it.data.toPollListMapper(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun getPollPolicy(): Flow<BaseResponse<CreatePolicy>> = communityDataSource.getPollPolicy().map {
        BaseResponse(
            ok = it.ok,
            data = it.data.toCreatePolicyMapper(),
            message = it.message,
            resultCode = it.resultCode,
            error = it.error
        )
    }

    override fun getUserPollList(cursor: Int?): Flow<BaseResponse<UserPollItemList>> =
        communityDataSource.getUserPollList(cursor).map {
            BaseResponse(
                ok = it.ok,
                data = it.data.toUserPollItemListMapper(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun createPollComment(id: String, content: String): Flow<BaseResponse<CommentId>> =
        communityDataSource.createPollComment(id, PollCommentApiRequest(content)).map {
            BaseResponse(
                ok = it.ok,
                data = it.data.toCommentIdMapper(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun deletePollComment(pollId: String, commentId: String): Flow<BaseResponse<String>> =
        communityDataSource.deletePollComment(pollId, commentId).map {
            BaseResponse(
                ok = it.ok,
                data = it.data,
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun editPollComment(pollId: String, commentId: String, content: String): Flow<BaseResponse<String>> =
        communityDataSource.editPollComment(pollId, commentId, PollCommentApiRequest(content)).map {
            BaseResponse(
                ok = it.ok,
                data = it.data,
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun reportPollComment(pollId: String, commentId: String, reason: String, reasonDetail: String?): Flow<BaseResponse<String>> =
        communityDataSource.reportPollComment(pollId, commentId, PollReportCreateApiRequest(reason, reasonDetail))
            .map {
                BaseResponse(
                    ok = it.ok,
                    data = it.data,
                    message = it.message,
                    resultCode = it.resultCode,
                    error = it.error
                )
            }

    override fun getPollCommentList(id: String, cursor: String?): Flow<BaseResponse<PollCommentList>> =
        communityDataSource.getPollCommentList(id, cursor).map {
            BaseResponse(
                ok = it.ok,
                data = it.data.toPollCommentListMapper(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun getNeighborhoods(): Flow<BaseResponse<Neighborhoods>> =
        communityDataSource.getNeighborhoods().map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.toNeighborhoodsMapper(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun getPopularStores(criteria: String, district: String, cursor: String): Flow<BaseResponse<PopularStores>> =
        communityDataSource.getPopularStores(criteria, district, cursor).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.toPopularStoresMapper(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun getReportReasons(reportReasonsGroupType: ReportReasonsGroupType): Flow<BaseResponse<DomainReportReasonsModel>> =
        communityDataSource.getReportReasons(reportReasonsGroupType.toNetworkGroupType()).map {
            BaseResponse(
                ok = it.ok,
                data = it.data?.asModel()?.toDomainModel(),
                message = it.message,
                resultCode = it.resultCode,
                error = it.error
            )
        }

    override fun getAdvertisements(
        position: AdvertisementsPosition,
        deviceLatitude: Double,
        deviceLongitude: Double,
    ): Flow<BaseResponse<List<AdvertisementModelV2>>> = communityDataSource.getAdvertisements(
        position = position,
        deviceLatitude = deviceLatitude,
        deviceLongitude = deviceLongitude,
    ).map {
        BaseResponse(
            ok = it.ok,
            data = it.data?.advertisements.orEmpty().map { it.asModel() },
            message = it.message,
            resultCode = it.resultCode,
            error = it.error
        )
    }

}