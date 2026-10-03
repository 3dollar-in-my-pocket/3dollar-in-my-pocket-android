package com.threedollar.data.store

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.threedollar.common.base.BaseResponse
import com.threedollar.data.fake.FakeStoreApi
import com.threedollar.data.fake.FakeStoreMenuExtractionApi
import com.threedollar.data.store.repository.StoreRepositoryImpl
import com.threedollar.network.data.store.StoreMenuExtractionListResponse
import com.threedollar.network.data.store.StoreMenuExtractionResponse
import com.threedollar.network.result.ApiException
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class StoreMenuExtractionRepositoryTest {

    private val extractionApi = FakeStoreMenuExtractionApi()
    private val repository = StoreRepositoryImpl(FakeStoreApi(), extractionApi)
    private val imagePart = MultipartBody.Part.createFormData("file", "menu.jpg", "image".toRequestBody("image/jpeg".toMediaType()))

    private fun load(path: String): Response<BaseResponse<StoreMenuExtractionListResponse>> {
        val json = requireNotNull(javaClass.classLoader?.getResource(path)).readText()
        val type = object : TypeToken<BaseResponse<StoreMenuExtractionListResponse>>() {}.type
        return Response.success(Gson().fromJson(json, type))
    }

    // TH-1333 TC7
    @Test
    fun `TH1333_TC7_메뉴판_인식결과를_메뉴별_카테고리와_가격으로_옮긴다`() = runBlocking {
        // Given
        extractionApi.response = load("store/StoreMenuExtractionMenuBoard.json")

        // When
        val menus = repository.extractStoreMenus(imagePart).getOrThrow()

        // Then
        assertEquals(29, menus.size)
        assertEquals("아메리카노", menus.first().name)
        assertEquals(4000, menus.first().price)
        assertEquals("CAFE", menus.first().category.categoryId)
        assertEquals("카페/디저트", menus.first().category.name)
        assertEquals(setOf("CAFE", "ETC"), menus.map { it.category.categoryId }.toSet())
        assertEquals("file", imagePart.headers?.get("Content-Disposition")?.substringAfter("name=\"")?.substringBefore("\""))
    }

    // TH-1333 TC9
    @Test
    fun `TH1333_TC9_수량과_가격을_인식하지_못하면_null로_둔다`() = runBlocking {
        // Given
        extractionApi.response = load("store/StoreMenuExtractionNoPrice.json")

        // When
        val menu = repository.extractStoreMenus(imagePart).getOrThrow().single()

        // Then
        assertEquals("붕어빵", menu.name)
        assertNull(menu.count)
        assertNull(menu.price)
    }

    // TH-1333 TC17
    @Test
    fun `TH1333_TC17_인식된_메뉴가_없으면_빈목록을_돌려준다`() = runBlocking {
        // Given
        extractionApi.response = load("store/StoreMenuExtractionEmpty.json")

        // When
        val menus = repository.extractStoreMenus(imagePart).getOrThrow()

        // Then
        assertTrue(menus.isEmpty())
    }

    // TH-1333 TC17
    @Test
    fun `TH1333_TC17_서버에러면_서버메시지를_담은_ApiException으로_실패한다`() = runBlocking {
        // Given
        val errorBody = """{"ok":false,"resultCode":"BR000","message":"사진을 다시 선택해주세요"}"""
        extractionApi.response = Response.error(400, errorBody.toResponseBody("application/json".toMediaType()))

        // When
        val error = repository.extractStoreMenus(imagePart).exceptionOrNull()

        // Then
        assertEquals("사진을 다시 선택해주세요", (error as ApiException).message)
    }

    @Test
    fun `이름이나_카테고리가_비어있는_메뉴는_버린다`() = runBlocking {
        // Given
        extractionApi.response = Response.success(
            BaseResponse(
                ok = true,
                data = StoreMenuExtractionListResponse(
                    menus = listOf(
                        StoreMenuExtractionResponse(name = " ", category = null),
                        StoreMenuExtractionResponse(name = "호떡", category = null),
                    ),
                ),
            ),
        )

        // When
        val menus = repository.extractStoreMenus(imagePart).getOrThrow()

        // Then
        assertTrue(menus.isEmpty())
    }
}
