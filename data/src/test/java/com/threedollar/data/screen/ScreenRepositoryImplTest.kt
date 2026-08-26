package com.threedollar.data.screen

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.data.screen.datasource.ScreenRemoteDataSource
import com.threedollar.data.screen.repository.ScreenRepositoryImpl
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import com.threedollar.network.data.screen.StoreContributorHistoriesResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScreenRepositoryImplTest {

    @Test
    fun getStoreDetailScreenPassesOptionalLocationAndMapsRawScreenAtRepositoryBoundary() = runBlocking {
        val remote = FakeScreenRemoteDataSource(validStoreDetailResponse())
        val repository = ScreenRepositoryImpl(remote)

        val response = repository.getStoreDetailScreen(
            storeId = 100186L,
            deviceLatitude = null,
            deviceLongitude = null,
        ).first()

        assertEquals(100186L, remote.storeId)
        assertNull(remote.deviceLatitude)
        assertNull(remote.deviceLongitude)
        assertEquals(true, response.ok)
        assertEquals("CTA", (response.data?.sections?.single() as StoreDetailSectionModel.Cta).type)
    }

    @Test
    fun storeDetailMutationsPassIdentifiersAndExposeSuccessWithoutNetworkPayload() = runBlocking {
        val remote = FakeScreenRemoteDataSource(validStoreDetailResponse())
        val repository = ScreenRepositoryImpl(remote)

        assertEquals(true, repository.putStorePostStickers(1L, 2L, listOf("like")).first().data)
        assertEquals(true, repository.issueStoreCoupon(1L, "coupon").first().data)
        assertEquals(true, repository.useIssuedCoupon("issued").first().data)
        assertEquals(true, repository.deleteStoreReview(9L).first().data)
        assertEquals(listOf("like"), remote.stickers)
        assertEquals("coupon", remote.couponId)
        assertEquals("issued", remote.issuedKey)
        assertEquals(9L, remote.reviewId)
    }

    private fun validStoreDetailResponse(): StoreDetailScreenResponse = Gson().fromJson(
        """
        {
          "sections":[{"type":"CTA","content":{"title":{"text":"열기","isHtml":false,"fontColor":"#111111"}}}],
          "viewLog":{"screenName":"store_detail","objectType":"screen","objectId":"detail","extraParameters":{}}
        }
        """.trimIndent(),
        StoreDetailScreenResponse::class.java,
    )
}

private class FakeScreenRemoteDataSource(
    private val storeDetailResponse: StoreDetailScreenResponse,
) : ScreenRemoteDataSource {
    var storeId: Long? = null
    var deviceLatitude: Double? = null
    var deviceLongitude: Double? = null
    var stickers: List<String>? = null
    var couponId: String? = null
    var issuedKey: String? = null
    var reviewId: Long? = null

    override fun getStoreDetailScreen(
        storeId: Long,
        deviceLatitude: Double?,
        deviceLongitude: Double?,
    ): Flow<BaseResponse<StoreDetailScreenResponse>> {
        this.storeId = storeId
        this.deviceLatitude = deviceLatitude
        this.deviceLongitude = deviceLongitude
        return flowOf(BaseResponse(ok = true, data = storeDetailResponse))
    }

    override fun putStorePostStickers(
        storeId: Long,
        postId: Long,
        stickers: List<String>,
    ): Flow<BaseResponse<JsonElement>> {
        this.stickers = stickers
        return successfulMutation()
    }

    override fun issueStoreCoupon(storeId: Long, couponId: String): Flow<BaseResponse<JsonElement>> {
        this.couponId = couponId
        return successfulMutation()
    }

    override fun useIssuedCoupon(issuedKey: String): Flow<BaseResponse<JsonElement>> {
        this.issuedKey = issuedKey
        return successfulMutation()
    }

    override fun deleteStoreReview(reviewId: Long): Flow<BaseResponse<JsonElement>> {
        this.reviewId = reviewId
        return successfulMutation()
    }

    private fun successfulMutation(): Flow<BaseResponse<JsonElement>> =
        flowOf(BaseResponse(ok = true, data = JsonPrimitive("ok")))

    override fun getHomeListSection(
        distanceM: Double,
        categoryIds: Array<String>?,
        targetStores: Array<String>?,
        mapLatitude: Double,
        mapLongitude: Double,
        deviceLatitude: Double,
        deviceLongitude: Double,
        dynamicParams: Map<String, String>,
        cursor: String?,
    ): Flow<BaseResponse<HomeListSectionResponse>> = error("unused")

    override fun getStoreContributorScreen(storeId: String): Flow<BaseResponse<StoreContributorScreenResponse>> =
        error("unused")

    override fun getStoreContributorHistories(
        storeId: String,
        cursor: String?,
    ): Flow<BaseResponse<StoreContributorHistoriesResponse>> = error("unused")

    override fun getHomeFilterScreen(): Flow<BaseResponse<HomeFilterScreenResponse>> = error("unused")
}
