package com.threedollar.data.feed

import com.threedollar.data.fake.FakeFeedApi
import com.threedollar.data.feed.repository.FeedRepositoryImpl
import com.threedollar.domain.feed.model.FeedBodyType
import com.threedollar.domain.feed.model.FeedHeaderType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedRepositoryImplTest {

    private val feedApi = FakeFeedApi()
    private val repository = FeedRepositoryImpl(feedApi)

    private fun load(path: String) = runBlocking {
        feedApi.givenFixture(path)
        repository.getLocalNewsFeeds(cursor = null, mapLatitude = 37.36, mapLongitude = 126.93, deviceLatitude = 37.36, deviceLongitude = 126.93)
            .getOrThrow()
    }

    // TH-646 TC2
    @Test
    fun `TH646_TC2_카테고리_배지와_가게헤더를_서버값대로_옮긴다`() {
        // Given
        val path = "feed/LocalNewsFeeds.json"

        // When
        val feed = load(path).feeds.first { it.feedId == "6ab7616420134fcca11e43a6" }

        // Then
        assertEquals("❤️ 새로운 리뷰가 올라왔어요!", feed.category?.name?.text)
        assertEquals("#FF858F", feed.category?.style?.backgroundColor)
        val header = requireNotNull(feed.header)
        assertEquals(FeedHeaderType.GENERAL, header.type)
        assertEquals("뽀미네 두쫀쿠 붕어빵", header.content?.text)
        assertEquals("#왁뿌 소금빵 #소금빵 #문어빵", header.top?.text)
        assertEquals(listOf("502개", "4.6", "5m"), header.metadata.map { it.content?.text })
        assertEquals("/reviewList?storeType=BOSS_STORE&storeId=120009", feed.link?.link)
    }

    // TH-646 TC3
    @Test
    fun `TH646_TC3_CONTENT_ONLY는_본문과_서버배경색을_그대로_쓴다`() {
        // Given
        val path = "feed/LocalNewsFeeds.json"

        // When
        val body = requireNotNull(load(path).feeds.first { it.feedId == "6aad851ff0a03114d46a48fa" }.body)

        // Then
        assertEquals(FeedBodyType.CONTENT_ONLY, body.type)
        assertEquals("#F4F4F4", body.style?.backgroundColor)
        assertEquals(true, body.content?.isHtml)
    }

    // TH-646 TC4
    @Test
    fun `TH646_TC4_CONTENT_WITH_TITLE은_제목과_별점을_옮긴다`() {
        // Given
        val path = "feed/LocalNewsFeeds.json"

        // When
        val body = requireNotNull(load(path).feeds.first { it.feedId == "6ab7616420134fcca11e43a6" }.body)

        // Then
        assertEquals(FeedBodyType.CONTENT_WITH_TITLE, body.type)
        assertNotNull(body.title)
        val rating = requireNotNull(body.rating)
        assertEquals(5.0f, rating.starRating, 0f)
        assertEquals(5, rating.maxRating)
        assertEquals("#FF858F", rating.filledColor)
    }

    // TH-646 TC5
    @Test
    fun `TH646_TC5_이미지본문은_원본비율을_계산하고_TITLE_CONTENT_IMAGES는_CONTENT_WITH_IMAGES로_그린다`() {
        // Given
        val path = "feed/LocalNewsFeedsUnsupportedTypes.json"

        // When
        val feeds = load(path).feeds

        // Then
        val images = requireNotNull(feeds.first { it.feedId == "images-1" }.body)
        assertEquals(FeedBodyType.CONTENT_WITH_IMAGES, images.type)
        assertEquals(listOf(1.5f, 0.5f), images.images.map { it.ratio })
        val titleImages = requireNotNull(feeds.first { it.feedId == "title-images" }.body)
        assertEquals(FeedBodyType.CONTENT_WITH_TITLE_AND_IMAGES, titleImages.type)
        assertEquals(1, titleImages.images.size)
    }

    // TH-646 TC6
    @Test
    fun `TH646_TC6_hasMore가_true일때만_다음커서를_넘긴다`() {
        // Given
        val hasMore = "feed/LocalNewsFeeds.json"
        val lastPage = "feed/LocalNewsFeedsUnsupportedTypes.json"

        // When
        val first = load(hasMore)
        val last = load(lastPage)

        // Then
        assertEquals("MjA=", first.nextCursor)
        assertNull(last.nextCursor)
        assertTrue(feedApi.requests.all { it.ticketId == "LOCAL_NEWS" && it.size == 20 })
    }

    // TH-646 TC11
    @Test
    fun `TH646_TC11_알수없는_타입은_크래시없이_기본형으로_떨어지고_본문이_없으면_생략한다`() {
        // Given
        val unsupported = "feed/LocalNewsFeedsUnsupportedTypes.json"
        val real = "feed/LocalNewsFeeds.json"

        // When
        val feeds = load(unsupported).feeds
        val noBody = load(real).feeds.first { it.feedId == "6ab8b210dd25a23c2db26a1e" }

        // Then
        assertEquals(FeedBodyType.CONTENT_ONLY, feeds.first { it.feedId == "unknown-body" }.body?.type)
        assertEquals(FeedHeaderType.GENERAL, feeds.first { it.feedId == "unknown-header" }.header?.type)
        assertNull(noBody.body)
        assertNotNull(noBody.header)
    }
}
