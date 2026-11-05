package com.threedollar.data.home

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.threedollar.domain.home.data.store.UploadFileModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreReviewRequestMapperTest {

    private val gson = Gson()
    private val firstImage = UploadFileModel(imageUrl = "https://storage.threedollars.co.kr/review/1.png", width = 300, height = 400, ratio = 0.75)
    private val secondImage = UploadFileModel(imageUrl = "https://storage.threedollars.co.kr/review/2.png", width = 640, height = 480, ratio = 1.33)

    private fun json(request: Any): JsonObject = gson.toJsonTree(request).asJsonObject

    // TH-1422 TC7
    @Test
    fun `TH1422_TC7_리뷰작성_요청에_feedbacks_빈배열과_사진이_들어간다`() {
        // Given
        val images = listOf(firstImage, secondImage)

        // When
        val body = json(storeReviewCreateRequest(contents = "맛있어요", rating = 4, storeId = 12806244, images = images))

        // Then
        assertEquals(12806244, body["storeId"].asInt)
        assertEquals("맛있어요", body["contents"].asString)
        assertEquals(4, body["rating"].asInt)
        assertTrue(body["feedbacks"].asJsonArray.isEmpty)
        val sentImages = body["images"].asJsonArray.map { it.asJsonObject }
        assertEquals(listOf(firstImage.imageUrl, secondImage.imageUrl), sentImages.map { it["url"].asString })
        assertEquals(listOf(300, 640), sentImages.map { it["width"].asInt })
        assertEquals(listOf(400, 480), sentImages.map { it["height"].asInt })
    }

    // TH-1422 TC8
    @Test
    fun `TH1422_TC8_사진없이_작성해도_feedbacks와_images가_빈배열로_간다`() {
        // Given
        val images = emptyList<UploadFileModel>()

        // When
        val body = json(storeReviewCreateRequest(contents = "사장님 친절해요", rating = 5, storeId = 1, images = images))

        // Then
        assertTrue(body["feedbacks"].asJsonArray.isEmpty)
        assertTrue(body["images"].asJsonArray.isEmpty)
    }

    // TH-1422 TC10
    @Test
    fun `TH1422_TC10_리뷰수정_요청에_남은사진과_새사진이_순서대로_들어가고_피드백은_없다`() {
        // Given
        val images = listOf(secondImage, firstImage)

        // When
        val body = json(storeReviewUpdateRequest(contents = "수정했어요", rating = 3, images = images))

        // Then
        assertEquals(listOf(secondImage.imageUrl, firstImage.imageUrl), body["images"].asJsonArray.map { it.asJsonObject["url"].asString })
        assertEquals(3, body["rating"].asInt)
        assertFalse(body.has("feedbacks"))
        assertFalse(body.has("storeId"))
    }
}
