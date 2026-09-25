package com.threedollar.domain.store.repository

import com.threedollar.common.sdui.model.screen.SDScreenModel
import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.domain.store.model.StoreDisplayItemType
import com.threedollar.domain.store.model.StoreDisplayItemsModel

interface StoreRepository {
    suspend fun getScreenStore(
        storeId: Int,
        lat: Double,
        lng: Double
    ): Result<SDScreenModel>

    suspend fun getStoreDisplayItems(
        storeId: Int,
        lat: Double,
        lng: Double,
        itemTypes: List<StoreDisplayItemType>,
    ): Result<StoreDisplayItemsModel>

    suspend fun postStoreDisplayItemImpression(
        storeId: Int,
        itemTypes: List<StoreDisplayItemType>,
    ): Result<String>

    /**
     * 가게 상세 v2 화면. 제보 가게·사장님 가게 모두 이 화면 하나로 그린다.
     * 삭제된 가게면 [com.threedollar.domain.store.model.StoreNotExistsException]으로 실패한다.
     */
    suspend fun getStoreScreenV2(
        storeId: String,
        lat: Double?,
        lng: Double?,
    ): Result<SDStoreScreenModel>

    suspend fun getStorePreviewScreen(
        storeId: String,
        lat: Double?,
        lng: Double?,
    ): Result<SDStoreScreenModel>

    suspend fun issueStoreCoupon(storeId: String, couponId: String): Result<Unit>

    suspend fun useIssuedCoupon(issuedKey: String): Result<Unit>

    /** [stickerId]가 null 이면 스티커(좋아요)를 취소한다. */
    suspend fun putStorePostSticker(storeId: String, postId: String, stickerId: String?): Result<Unit>

    /** [stickerId]가 null 이면 스티커(좋아요)를 취소한다. */
    suspend fun putStoreReviewSticker(storeId: String, reviewId: String, stickerId: String?): Result<Unit>

    suspend fun deleteStoreReview(reviewId: String): Result<Unit>
}
