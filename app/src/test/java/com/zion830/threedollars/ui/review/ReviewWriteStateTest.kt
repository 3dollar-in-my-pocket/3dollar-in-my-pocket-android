package com.zion830.threedollars.ui.review

import com.threedollar.domain.home.data.store.UploadFileModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewWriteStateTest {

    private val existingPhoto = ReviewPhoto.Uploaded(UploadFileModel("https://storage.threedollars.co.kr/review/1.png", 300, 400, 0.75))

    private fun uris(count: Int, prefix: String = "content://media/picker/") = (1..count).map { "$prefix$it" }

    // TH-1422 TC3
    @Test
    fun `TH1422_TC3_별점만_또는_내용만_있으면_등록할수_없다`() {
        // Given
        val empty = ReviewWriteState(storeId = 1)

        // When
        val ratingOnly = empty.withRating(4)
        val contentsOnly = empty.withContents("맛있어요")
        val blankContents = empty.withRating(4).withContents("   ")

        // Then
        assertFalse(ratingOnly.canSubmit)
        assertFalse(contentsOnly.canSubmit)
        assertFalse(blankContents.canSubmit)
    }

    // TH-1422 TC4
    @Test
    fun `TH1422_TC4_별점과_내용이_있으면_사진이_없어도_등록할수_있다`() {
        // Given
        val state = ReviewWriteState(storeId = 1)

        // When
        val filled = state.withRating(5).withContents("맛있어요")

        // Then
        assertTrue(filled.photos.isEmpty())
        assertTrue(filled.canSubmit)
        assertFalse(filled.copy(isSubmitting = true).canSubmit)
    }

    // TH-1422 TC5
    @Test
    fun `TH1422_TC5_사진은_10장까지만_붙고_10장이면_더_추가할수_없다`() {
        // Given
        val state = ReviewWriteState(storeId = 1, photos = listOf(existingPhoto))

        // When
        val added = state.addPickedPhotos(uris(12))

        // Then
        assertEquals(ReviewWriteState.MAX_PHOTO_COUNT, added.photos.size)
        assertEquals(existingPhoto, added.photos.first())
        assertFalse(added.canAddPhoto)
        assertEquals(added, added.addPickedPhotos(uris(1, prefix = "content://media/other/")))
    }

    // TH-1422 TC5
    @Test
    fun `TH1422_TC5_같은_사진을_다시_골라도_한번만_붙는다`() {
        // Given
        val state = ReviewWriteState(storeId = 1).addPickedPhotos(uris(2))

        // When
        val added = state.addPickedPhotos(uris(3))

        // Then
        assertEquals(uris(3), added.photos.map { it.key })
        assertEquals(7, added.remainingPhotoCount)
    }

    // TH-1422 TC6
    @Test
    fun `TH1422_TC6_사진을_지우면_그사진만_빠진다`() {
        // Given
        val state = ReviewWriteState(storeId = 1, photos = listOf(existingPhoto)).addPickedPhotos(uris(2))

        // When
        val removed = state.removePhoto(existingPhoto.key).removePhoto(uris(2)[0])

        // Then
        assertEquals(listOf(uris(2)[1]), removed.photos.map { it.key })
    }

    // TH-1422 TC11
    @Test
    fun `TH1422_TC11_내용은_300자까지만_들어간다`() {
        // Given
        val longText = "가".repeat(350)

        // When
        val state = ReviewWriteState(storeId = 1).withContents(longText)

        // Then
        assertEquals(ReviewWriteState.MAX_CONTENTS_LENGTH, state.contents.length)
    }
}
