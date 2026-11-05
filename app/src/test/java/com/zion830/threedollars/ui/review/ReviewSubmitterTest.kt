package com.zion830.threedollars.ui.review

import com.threedollar.domain.home.data.store.UploadFileModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewSubmitterTest {

    private val existing = UploadFileModel("https://storage.threedollars.co.kr/review/old.png", 300, 400, 0.75)

    private class Recorder {
        var uploadedUris: List<String>? = null
        var postedImages: List<UploadFileModel>? = null
        var postedStoreId: Int? = null
        var putReviewId: Long? = null
        var putImages: List<UploadFileModel>? = null
    }

    private fun submitter(
        recorder: Recorder,
        uploadFails: Boolean = false,
        uploadedSize: Int = 100,
        measured: PhotoPixelSize? = null,
    ) = ReviewSubmitter(
        uploadPhotos = { uris ->
            recorder.uploadedUris = uris
            if (uploadFails) {
                null
            } else {
                uris.map { UploadFileModel("https://storage.threedollars.co.kr/review/${it.substringAfterLast('/')}.png", uploadedSize, uploadedSize, 1.0) }
            }
        },
        measurePhoto = { measured },
        postReview = { _, _, storeId, images ->
            recorder.postedStoreId = storeId
            recorder.postedImages = images
            ReviewSubmitResult.Success
        },
        putReview = { reviewId, _, _, images ->
            recorder.putReviewId = reviewId
            recorder.putImages = images
            ReviewSubmitResult.Success
        },
    )

    // TH-1422 TC7
    @Test
    fun `TH1422_TC7_고른사진을_올린뒤_올린순서대로_리뷰작성에_넘긴다`() = runBlocking {
        // Given
        val recorder = Recorder()
        val state = ReviewWriteState(storeId = 12806244).withRating(4).withContents("맛있어요").addPickedPhotos(listOf("content://picker/a", "content://picker/b"))

        // When
        val result = submitter(recorder).submit(state)

        // Then
        assertEquals(ReviewSubmitResult.Success, result)
        assertEquals(listOf("content://picker/a", "content://picker/b"), recorder.uploadedUris)
        assertEquals(12806244, recorder.postedStoreId)
        assertEquals(
            listOf("https://storage.threedollars.co.kr/review/a.png", "https://storage.threedollars.co.kr/review/b.png"),
            recorder.postedImages?.map { it.imageUrl },
        )
        assertNull(recorder.putReviewId)
    }

    // TH-1422 TC17
    @Test
    fun `TH1422_TC17_업로드응답에_크기가없으면_올린사진의_실제픽셀크기를_넘긴다`() = runBlocking {
        // Given
        val recorder = Recorder()
        val state = ReviewWriteState(storeId = 1).withRating(4).withContents("바삭해요").addPickedPhotos(listOf("content://picker/a"))

        // When
        submitter(recorder, uploadedSize = 0, measured = PhotoPixelSize(width = 400, height = 300)).submit(state)

        // Then
        val image = recorder.postedImages?.single()
        assertEquals(400, image?.width)
        assertEquals(300, image?.height)
    }

    // TH-1422 TC17
    @Test
    fun `TH1422_TC17_크기를_잴수없고_업로드응답에도_없으면_500으로_넘긴다`() = runBlocking {
        // Given
        val recorder = Recorder()
        val state = ReviewWriteState(storeId = 1).withRating(4).withContents("바삭해요").addPickedPhotos(listOf("content://picker/a"))

        // When
        submitter(recorder, uploadedSize = 0, measured = null).submit(state)

        // Then
        val image = recorder.postedImages?.single()
        assertEquals(500, image?.width)
        assertEquals(500, image?.height)
    }

    // TH-1422 TC10
    @Test
    fun `TH1422_TC10_수정은_남은기존사진과_새사진을_화면순서대로_넘기고_기존사진은_다시_올리지_않는다`() = runBlocking {
        // Given
        val recorder = Recorder()
        val state = ReviewWriteState(storeId = 1, reviewId = 77L, rating = 3, contents = "수정", photos = listOf(ReviewPhoto.Uploaded(existing)))
            .addPickedPhotos(listOf("content://picker/new"))

        // When
        submitter(recorder).submit(state)

        // Then
        assertEquals(77L, recorder.putReviewId)
        assertEquals(listOf("content://picker/new"), recorder.uploadedUris)
        assertEquals(
            listOf(existing.imageUrl, "https://storage.threedollars.co.kr/review/new.png"),
            recorder.putImages?.map { it.imageUrl },
        )
        assertNull(recorder.postedImages)
    }

    // TH-1422 TC10
    @Test
    fun `TH1422_TC10_기존사진을_모두_지우고_저장하면_빈사진목록으로_수정한다`() = runBlocking {
        // Given
        val recorder = Recorder()
        val state = ReviewWriteState(storeId = 1, reviewId = 77L, rating = 3, contents = "수정", photos = listOf(ReviewPhoto.Uploaded(existing)))
            .removePhoto(existing.imageUrl)

        // When
        submitter(recorder).submit(state)

        // Then
        assertNull(recorder.uploadedUris)
        assertEquals(emptyList<UploadFileModel>(), recorder.putImages)
    }

    // TH-1422 TC15
    @Test
    fun `TH1422_TC15_사진업로드가_실패하면_리뷰를_보내지_않고_실패를_돌려준다`() = runBlocking {
        // Given
        val recorder = Recorder()
        val state = ReviewWriteState(storeId = 1).withRating(5).withContents("맛있어요").addPickedPhotos(listOf("content://picker/a"))

        // When
        val result = submitter(recorder, uploadFails = true).submit(state)

        // Then
        assertTrue(result is ReviewSubmitResult.Failure)
        assertNull(recorder.postedImages)
        assertNull(recorder.putImages)
    }
}
