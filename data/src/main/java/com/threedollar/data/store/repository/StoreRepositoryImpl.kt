package com.threedollar.data.store.repository

import com.threedollar.common.sdui.model.screen.SDScreenModel
import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.data.store.asModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.threedollar.domain.store.model.StoreDisplayItemsModel
import com.threedollar.domain.store.model.StoreNotExistsException
import com.threedollar.domain.store.repository.StoreRepository
import com.threedollar.network.api.StoreApi
import com.threedollar.network.request.StickerRequest
import com.threedollar.network.request.StoreDisplayItemsRequest
import com.threedollar.network.result.ApiError
import com.threedollar.network.result.ApiException
import com.threedollar.network.util.runApi
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val storeApi: StoreApi
) : StoreRepository {
    override suspend fun getScreenStore(
        storeId: Int,
        lat: Double,
        lng: Double
    ): Result<SDScreenModel> = runApi {
        storeApi.getScreenStore(
            storeId = storeId,
            lat = lat,
            lng = lng
        )
    }

    override suspend fun getStoreDisplayItems(
        storeId: Int,
        lat: Double,
        lng: Double,
        itemTypes: List<StoreDisplayItemType>,
    ): Result<StoreDisplayItemsModel> = runApi {
        storeApi.getStoreDisplayItems(
            storeId = storeId,
            itemTypes = itemTypes.map { it.value },
            lat = lat,
            lng = lng,
        )
    }.map { it.asModel() }

    override suspend fun postStoreDisplayItemImpression(
        storeId: Int,
        itemTypes: List<StoreDisplayItemType>,
    ): Result<String> = runApi {
        storeApi.postStoreDisplayItemImpression(
            storeId = storeId,
            request = StoreDisplayItemsRequest(itemTypes.map { it.value }),
        )
    }

    override suspend fun getStoreScreenV2(
        storeId: String,
        lat: Double?,
        lng: Double?,
    ): Result<SDStoreScreenModel> = runApi {
        storeApi.getStoreScreenV2(storeId = storeId, lat = lat, lng = lng)
    }.recoverCatching { throwable ->
        throw throwable.toStoreNotExistsOrSelf()
    }

    override suspend fun issueStoreCoupon(storeId: String, couponId: String): Result<Unit> = runApi {
        storeApi.issueStoreCoupon(storeId = storeId, couponId = couponId)
    }.map { }

    override suspend fun useIssuedCoupon(issuedKey: String): Result<Unit> = runApi {
        storeApi.useIssuedCoupon(issuedKey = issuedKey)
    }.map { }

    override suspend fun putStorePostSticker(
        storeId: String,
        postId: String,
        stickerId: String?,
    ): Result<Unit> = runApi {
        storeApi.putStorePostStickers(storeId = storeId, postId = postId, request = stickerId.toStickerRequest())
    }.map { }

    override suspend fun putStoreReviewSticker(
        storeId: String,
        reviewId: String,
        stickerId: String?,
    ): Result<Unit> = runApi {
        storeApi.putStoreReviewStickers(storeId = storeId, reviewId = reviewId, request = stickerId.toStickerRequest())
    }.map { }

    override suspend fun deleteStoreReview(reviewId: String): Result<Unit> = runApi {
        storeApi.deleteStoreReview(reviewId = reviewId)
    }.map { }

    private fun String?.toStickerRequest(): StickerRequest =
        StickerRequest(stickers = listOfNotNull(this?.let { StickerRequest.Sticker(stickerId = it) }))

    private fun Throwable.toStoreNotExistsOrSelf(): Throwable =
        if (this is ApiException && error == ApiError.NOT_EXISTS_STORE) StoreNotExistsException(message) else this
}
