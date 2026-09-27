package com.threedollar.data.store

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.threedollar.common.base.BaseResponse
import com.threedollar.data.fake.FakeStoreApi
import com.threedollar.data.store.repository.StoreRepositoryImpl
import com.threedollar.domain.home.data.store.SectionTypeModel
import com.threedollar.domain.store.model.StoreNotExistsException
import com.threedollar.network.data.store.ContentsWithCursorWithTotalCountResponse
import com.threedollar.network.data.store.NewsPost
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

    // TH-197 TC4
    @Test
    fun `TH197_TC4_소식목록_응답의_커서와_가게정보를_페이지모델로_옮긴다`() = runBlocking {
        // Given
        storeApi.newsPostsResponse = newsPostsResponse("store/StoreNewsPosts.json")

        // When
        val page = repository.getStoreNewsPosts(storeId = "120009", cursor = null, size = 20).getOrThrow()

        // Then
        assertEquals(3, page.posts.size)
        assertTrue(page.cursor.hasMore)
        assertEquals("Ng==", page.cursor.nextCursor)
        val first = page.posts.first()
        assertEquals("뽀미네 두쫀쿠 붕어빵", first.storeName)
        assertEquals("https://storage.threedollars.co.kr/menu/wakbbu_salt_bread_3x.png", first.storeCategoryImageUrl)
        assertEquals("LIKE", first.stickers.single().stickerId)
    }

    // TH-197 TC5
    @Test
    fun `TH197_TC5_이미지섹션의_비율을_유지하고_알수없는_섹션타입은_UNKNOWN으로_떨어진다`() = runBlocking {
        // Given
        storeApi.newsPostsResponse = newsPostsResponse("store/StoreNewsPosts.json")
        val unknownApi = FakeStoreApi().apply { newsPostsResponse = newsPostsResponse("store/StoreNewsPostsUnknownSection.json") }

        // When
        val posts = repository.getStoreNewsPosts(storeId = "120009", cursor = null, size = 20).getOrThrow().posts
        val unknownPage = StoreRepositoryImpl(unknownApi).getStoreNewsPosts(storeId = "120009", cursor = null, size = 20).getOrThrow()

        // Then
        val multiImagePost = posts.first { it.postId == "176" }
        assertEquals(3, multiImagePost.sections.size)
        assertTrue(multiImagePost.sections.all { it.sectionType == SectionTypeModel.IMAGE && it.ratio > 0f })
        assertTrue(posts.first { it.postId == "173" }.sections.isEmpty())
        assertEquals(SectionTypeModel.UNKNOWN, unknownPage.posts.single().sections.single().sectionType)
        assertEquals(false, unknownPage.cursor.hasMore)
    }

    private fun newsPostsResponse(path: String): Response<BaseResponse<ContentsWithCursorWithTotalCountResponse<NewsPost>>> {
        val json = requireNotNull(javaClass.classLoader?.getResource(path)).readText()
        val type = object : TypeToken<ContentsWithCursorWithTotalCountResponse<NewsPost>>() {}.type
        return Response.success(BaseResponse(ok = true, data = Gson().fromJson(json, type)))
    }
}
