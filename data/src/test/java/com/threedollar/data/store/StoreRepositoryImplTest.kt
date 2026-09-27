package com.threedollar.data.store

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.threedollar.common.base.BaseResponse
import com.threedollar.data.fake.FakeStoreApi
import com.threedollar.data.store.repository.StoreRepositoryImpl
import com.threedollar.domain.store.model.IssuedCouponStatus
import com.threedollar.domain.store.model.StoreNotExistsException
import com.threedollar.network.data.store.ContentsWithCursorWithTotalCountResponse
import com.threedollar.network.data.store.IssuedCouponResponse
import com.threedollar.network.data.store.StoreV5Response
import com.threedollar.network.result.ApiException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class StoreRepositoryImplTest {

    private val storeApi = FakeStoreApi()
    private val repository = StoreRepositoryImpl(storeApi)

    // TH-1375
    @Test
    fun `TH1375_사장님가게도_v5_가게정보로_방문인증에_필요한_이름_위치_카테고리를_채운다`() = runBlocking {
        // Given
        val json = requireNotNull(javaClass.classLoader?.getResource("store/StoreV5BossStore.json")).readText()
        val type = object : TypeToken<BaseResponse<StoreV5Response>>() {}.type
        storeApi.storeResponse = Response.success(Gson().fromJson<BaseResponse<StoreV5Response>>(json, type))

        // When
        val store = repository.getStore(storeId = "120009", lat = null, lng = null).getOrThrow()

        // Then
        assertEquals(120009, store.storeId)
        assertEquals("뽀미네 두쫀쿠 붕어빵", store.name)
        assertEquals(37.36954969792162, store.location.latitude, 0.0)
        assertTrue(store.categories.first().imageUrl.isNotBlank())
    }

    // TH-1226 TC15
    @Test
    fun `TH1226_TC15_삭제된가게_응답이면_서버메시지를_담은_StoreNotExistsException으로_실패한다`() = runBlocking {
        // Given
        storeApi.givenStoreScreenV2Error(
            httpCode = 404,
            errorBody = """{"ok":false,"resultCode":"NF002","error":"not_exists_store","message":"삭제된 가게입니다"}"""
        )

        // When
        val result = repository.getStoreScreenV2(storeId = "106775", lat = null, lng = null)

        // Then
        val exception = result.exceptionOrNull()
        assertTrue(exception is StoreNotExistsException)
        assertEquals("삭제된 가게입니다", exception?.message)
    }

    // TH-1226 TC22
    @Test
    fun `TH1226_TC22_삭제된가게가_아닌_서버에러는_원래_예외를_그대로_돌려준다`() = runBlocking {
        // Given
        storeApi.givenStoreScreenV2Error(
            httpCode = 500,
            errorBody = """{"ok":false,"resultCode":"IS000","error":"internal_server","message":"일시적인 문제가 발생했어요"}"""
        )

        // When
        val result = repository.getStoreScreenV2(storeId = "1", lat = 37.0, lng = 127.0)

        // Then
        val exception = result.exceptionOrNull()
        assertTrue(exception is ApiException)
        assertEquals("일시적인 문제가 발생했어요", exception?.message)
    }

    @Test
    fun `스티커를_null로_보내면_빈_스티커_목록으로_좋아요를_취소한다`() = runBlocking {
        // Given
        val stickerId: String? = null

        // When
        repository.putStoreReviewSticker(storeId = "1", reviewId = "2", stickerId = stickerId)
        repository.putStorePostSticker(storeId = "1", postId = "3", stickerId = "LIKE")

        // Then
        assertTrue(storeApi.putReviewStickerRequests.single().stickers.isEmpty())
        assertEquals("LIKE", storeApi.putPostStickerRequests.single().stickers.single().stickerId)
    }

    // TH-717 TC6
    @Test
    fun `TH717_TC6_탭별_상태를_statuses_쿼리로_보내고_지난쿠폰은_가게정보와_함께_옮긴다`() = runBlocking {
        // Given
        storeApi.issuedCouponsResponse = issuedCouponsResponse("store/MyIssuedCouponsUsed.json")

        // When
        repository.getMyIssuedCoupons(listOf(IssuedCouponStatus.ISSUED), cursor = null, size = 20)
        val page = repository.getMyIssuedCoupons(
            listOf(IssuedCouponStatus.USED, IssuedCouponStatus.EXPIRED),
            cursor = null,
            size = 20,
        ).getOrThrow()

        // Then
        assertEquals(listOf(listOf("ISSUED"), listOf("USED", "EXPIRED")), storeApi.issuedCouponStatuses)
        val coupon = page.coupons.single()
        assertEquals("891123447434936320", coupon.issuedKey)
        assertEquals("테스트용 쿠폰", coupon.name)
        assertEquals(IssuedCouponStatus.USED, coupon.status)
        assertEquals("12804906", coupon.storeId)
        assertEquals("현식 테스트", coupon.storeName)
        assertEquals("2026-10-14T23:59:59", coupon.endDateTime)
        assertEquals(false, page.hasMore)
    }

    // TH-717 TC3
    @Test
    fun `TH717_TC3_hasMore가_true면_다음커서를_넘기고_알수없는_상태는_지난쿠폰으로_발급정보가_없으면_제외한다`() = runBlocking {
        // Given
        storeApi.issuedCouponsResponse = issuedCouponsResponse("store/MyIssuedCouponsVariants.json")

        // When
        val page = repository.getMyIssuedCoupons(listOf(IssuedCouponStatus.ISSUED), cursor = null, size = 20).getOrThrow()

        // Then
        assertEquals(true, page.hasMore)
        assertEquals("MjA=", page.nextCursor)
        assertEquals(listOf(IssuedCouponStatus.ISSUED, IssuedCouponStatus.EXPIRED), page.coupons.map { it.status })
    }

    private fun issuedCouponsResponse(path: String): Response<BaseResponse<ContentsWithCursorWithTotalCountResponse<IssuedCouponResponse>>> {
        val json = requireNotNull(javaClass.classLoader?.getResource(path)).readText()
        val type = object : TypeToken<ContentsWithCursorWithTotalCountResponse<IssuedCouponResponse>>() {}.type
        return Response.success(BaseResponse(ok = true, data = Gson().fromJson(json, type)))
    }
}
