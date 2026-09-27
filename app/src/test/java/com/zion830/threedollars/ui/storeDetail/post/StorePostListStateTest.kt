package com.zion830.threedollars.ui.storeDetail.post

import com.threedollar.domain.home.data.store.CursorModel
import com.threedollar.domain.home.data.store.NewsPostModel
import com.threedollar.domain.home.data.store.SectionModel
import com.threedollar.domain.home.data.store.SectionTypeModel
import com.threedollar.domain.home.data.store.StickerModel
import com.threedollar.domain.store.model.NewsPostPageModel
import com.zion830.threedollars.ui.storeDetail.post.model.StorePostListDeepLink
import com.zion830.threedollars.ui.storeDetail.post.model.StorePostListUiState
import com.zion830.threedollars.ui.storeDetail.post.model.imageSections
import com.zion830.threedollars.ui.storeDetail.post.model.likeRequestStickerId
import com.zion830.threedollars.ui.storeDetail.post.model.likeSticker
import com.zion830.threedollars.ui.storeDetail.post.model.toggledLike
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StorePostListStateTest {

    private fun post(
        id: String,
        reacted: Boolean = false,
        count: Int = 3,
        sections: List<SectionModel> = emptyList(),
    ) = NewsPostModel(
        postId = id,
        body = "본문",
        sections = sections,
        isOwner = false,
        stickers = listOf(StickerModel(stickerId = "LIKE", emoji = "", count = count, reactedByMe = reacted)),
        createdAt = "2026-07-24T20:55:31",
        updatedAt = "2026-07-24T20:55:31",
        storeName = "뽀미네 두쫀쿠 붕어빵",
    )

    private fun page(ids: IntRange, hasMore: Boolean, nextCursor: String?) = NewsPostPageModel(
        posts = ids.map { post(it.toString()) },
        cursor = CursorModel(hasMore = hasMore, nextCursor = nextCursor),
    )

    // TH-197 TC3
    @Test
    fun `TH197_TC3_storeId가_없거나_숫자가_아니면_소식목록을_열지_않는다`() {
        // Given
        val inputs = listOf(null, "", "abc", "12a", "120009")

        // When
        val results = inputs.map { StorePostListDeepLink.validStoreId(it) }

        // Then
        assertEquals(listOf(null, null, null, null, "120009"), results)
    }

    // TH-197 TC4
    @Test
    fun `TH197_TC4_다음페이지를_이어붙이고_hasMore가_false면_더_요청하지_않는다`() {
        // Given
        val first = StorePostListUiState().appendPage(page(1..20, hasMore = true, nextCursor = "Ng=="))

        // When
        val second = first.appendPage(page(21..25, hasMore = false, nextCursor = null))

        // Then
        assertEquals(20, first.posts.size)
        assertEquals("Ng==", first.nextCursor)
        assertTrue(first.canLoadMore)
        assertEquals(25, second.posts.size)
        assertEquals("21", second.posts[20].postId)
        assertFalse(second.canLoadMore)
    }

    // TH-197 TC4
    @Test
    fun `TH197_TC4_조회중에는_중복으로_다음페이지를_요청하지_않는다`() {
        // Given
        val state = StorePostListUiState().appendPage(page(1..20, hasMore = true, nextCursor = "Ng=="))

        // When
        val loading = state.copy(isLoading = true)

        // Then
        assertFalse(loading.canLoadMore)
    }

    // TH-197 TC5
    @Test
    fun `TH197_TC5_이미지섹션만_가로스크롤_대상이고_없으면_비어있다`() {
        // Given
        val withImages = post(
            id = "1",
            sections = listOf(
                SectionModel(SectionTypeModel.IMAGE, "https://a.png", 1.0f),
                SectionModel(SectionTypeModel.UNKNOWN, "https://b.mp4", 1.0f),
                SectionModel(SectionTypeModel.IMAGE, "https://c.png", 1.5f),
            ),
        )
        val withoutImages = post(id = "2")

        // When
        val images = withImages.imageSections
        val empty = withoutImages.imageSections

        // Then
        assertEquals(listOf(1.0f, 1.5f), images.map { it.ratio })
        assertTrue(empty.isEmpty())
    }

    // TH-197 TC7
    @Test
    fun `TH197_TC7_좋아요를_누르면_채워지고_개수가_늘고_다시_누르면_원래대로_돌아온다`() {
        // Given
        val initial = post(id = "1", reacted = false, count = 3)

        // When
        val liked = initial.toggledLike()
        val unliked = liked.toggledLike()

        // Then
        assertEquals("LIKE", initial.likeRequestStickerId())
        assertTrue(liked.likeSticker.reactedByMe)
        assertEquals(4, liked.likeSticker.count)
        assertNull(liked.likeRequestStickerId())
        assertFalse(unliked.likeSticker.reactedByMe)
        assertEquals(3, unliked.likeSticker.count)
    }

    // TH-197 TC7
    @Test
    fun `TH197_TC7_좋아요_반영은_해당_소식만_바꾼다`() {
        // Given
        val state = StorePostListUiState().appendPage(page(1..3, hasMore = false, nextCursor = null))

        // When
        val updated = state.replacePost(state.posts[1].toggledLike())

        // Then
        assertEquals(listOf(false, true, false), updated.posts.map { it.likeSticker.reactedByMe })
    }

    // TH-197 TC7
    @Test
    fun `TH197_TC7_스티커가_없는_소식도_기본_좋아요로_그리고_누를수_있다`() {
        // Given
        val noSticker = post(id = "1").copy(stickers = emptyList())

        // When
        val liked = noSticker.toggledLike()

        // Then
        assertEquals(0, noSticker.likeSticker.count)
        assertEquals("LIKE", noSticker.likeRequestStickerId())
        assertEquals(1, liked.likeSticker.count)
    }

    @Test
    fun `소식이_없으면_타이틀용_가게명이_비어있다`() {
        // Given
        val empty = StorePostListUiState()

        // When
        val loaded = empty.appendPage(page(1..1, hasMore = false, nextCursor = null))

        // Then
        assertEquals("", empty.storeName)
        assertEquals("뽀미네 두쫀쿠 붕어빵", loaded.storeName)
    }
}
