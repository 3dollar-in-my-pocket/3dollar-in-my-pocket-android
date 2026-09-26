package com.threedollar.data.fake

import com.threedollar.common.base.BaseResponse
import com.threedollar.common.sdui.model.screen.SDScreenModel
import com.threedollar.common.sdui.model.screen.SDStoreScreenModel
import com.threedollar.network.api.StoreApi
import com.threedollar.network.data.store.StoreDisplayItemsResponse
import com.threedollar.network.data.store.StoreV5Response
import com.threedollar.network.request.StickerRequest
import com.threedollar.network.request.StoreDisplayItemsRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

/**
 * 가게 상세 v2 응답만 주입할 수 있는 [StoreApi] 대역. 나머지 호출은 기록만 하고 성공을 돌려준다.
 */
class FakeStoreApi : StoreApi {

    var storeScreenV2Response: Response<BaseResponse<SDStoreScreenModel>> =
        Response.success(BaseResponse(ok = true, data = SDStoreScreenModel(sections = emptyList(), viewLog = null)))

    val putPostStickerRequests = mutableListOf<StickerRequest>()
    val putReviewStickerRequests = mutableListOf<StickerRequest>()

    fun givenStoreScreenV2Error(httpCode: Int, errorBody: String) {
        storeScreenV2Response = Response.error(httpCode, errorBody.toResponseBody("application/json".toMediaType()))
    }

    override suspend fun getScreenStore(storeId: Int, lat: Double, lng: Double): Response<BaseResponse<SDScreenModel>> =
        Response.success(BaseResponse(ok = true, data = SDScreenModel(sections = emptyList())))

    override suspend fun getStoreDisplayItems(
        storeId: Int,
        itemTypes: List<String>,
        lat: Double,
        lng: Double,
    ): Response<BaseResponse<StoreDisplayItemsResponse>> = Response.success(BaseResponse(ok = true, data = null))

    override suspend fun postStoreDisplayItemImpression(
        storeId: Int,
        request: StoreDisplayItemsRequest,
    ): Response<BaseResponse<String>> = ok()

    override suspend fun getStoreScreenV2(
        storeId: String,
        lat: Double?,
        lng: Double?,
    ): Response<BaseResponse<SDStoreScreenModel>> = storeScreenV2Response

    var storePreviewResponse: Response<BaseResponse<SDStoreScreenModel>> = storeScreenV2Response

    var storeResponse: Response<BaseResponse<StoreV5Response>> = Response.success(BaseResponse(ok = true, data = StoreV5Response()))

    override suspend fun getStore(storeId: String, lat: Double?, lng: Double?): Response<BaseResponse<StoreV5Response>> =
        storeResponse

    override suspend fun getStorePreviewScreen(
        storeId: String,
        lat: Double?,
        lng: Double?,
    ): Response<BaseResponse<SDStoreScreenModel>> = storePreviewResponse

    override suspend fun issueStoreCoupon(storeId: String, couponId: String): Response<BaseResponse<Any>> =
        Response.success(BaseResponse(ok = true, data = Any()))

    override suspend fun useIssuedCoupon(issuedKey: String): Response<BaseResponse<String>> = ok()

    override suspend fun putStorePostStickers(
        storeId: String,
        postId: String,
        request: StickerRequest,
    ): Response<BaseResponse<String>> {
        putPostStickerRequests += request
        return ok()
    }

    override suspend fun putStoreReviewStickers(
        storeId: String,
        reviewId: String,
        request: StickerRequest,
    ): Response<BaseResponse<String>> {
        putReviewStickerRequests += request
        return ok()
    }

    override suspend fun deleteStoreReview(reviewId: String): Response<BaseResponse<String>> = ok()

    private fun ok(): Response<BaseResponse<String>> = Response.success(BaseResponse(ok = true, data = "OK"))
}
