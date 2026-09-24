package com.threedollar.data.store

import com.threedollar.data.fake.FakeStoreApi
import com.threedollar.data.store.repository.StoreRepositoryImpl
import com.threedollar.domain.store.model.StoreNotExistsException
import com.threedollar.network.result.ApiException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreRepositoryImplTest {

    private val storeApi = FakeStoreApi()
    private val repository = StoreRepositoryImpl(storeApi)

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
}
