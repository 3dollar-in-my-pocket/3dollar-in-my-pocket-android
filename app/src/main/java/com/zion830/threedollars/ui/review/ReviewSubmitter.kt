package com.zion830.threedollars.ui.review

import com.threedollar.domain.home.data.store.UploadFileModel

sealed interface ReviewSubmitResult {
    data object Success : ReviewSubmitResult
    data class Failure(val message: String?) : ReviewSubmitResult
}

data class PhotoPixelSize(val width: Int, val height: Int)

/**
 * 고른 사진만 먼저 올리고, 화면에 붙은 순서대로 기존 사진과 합쳐 리뷰를 작성(POST)하거나 수정(PUT)한다.
 * 사진 업로드가 실패하면 리뷰는 보내지 않는다.
 */
class ReviewSubmitter(
    private val uploadPhotos: suspend (uris: List<String>) -> List<UploadFileModel>?,
    private val measurePhoto: suspend (uri: String) -> PhotoPixelSize?,
    private val postReview: suspend (contents: String, rating: Int, storeId: Int, images: List<UploadFileModel>) -> ReviewSubmitResult,
    private val putReview: suspend (reviewId: Long, contents: String, rating: Int, images: List<UploadFileModel>) -> ReviewSubmitResult,
) {

    suspend fun submit(state: ReviewWriteState): ReviewSubmitResult {
        val images = resolveImages(state.photos) ?: return ReviewSubmitResult.Failure(message = null)
        val reviewId = state.reviewId
        return if (reviewId == null) {
            postReview(state.contents, state.rating, state.storeId, images)
        } else {
            putReview(reviewId, state.contents, state.rating, images)
        }
    }

    private suspend fun resolveImages(photos: List<ReviewPhoto>): List<UploadFileModel>? {
        val pickedUris = photos.filterIsInstance<ReviewPhoto.Picked>().map { it.uri }
        val uploaded = if (pickedUris.isEmpty()) emptyList() else uploadPhotos(pickedUris) ?: return null
        if (uploaded.size != pickedUris.size) return null
        val uploadedByUri = pickedUris.zip(uploaded) { uri, image -> uri to image.withSize(measurePhoto(uri)) }.toMap()
        return photos.map { photo ->
            when (photo) {
                is ReviewPhoto.Uploaded -> photo.image
                is ReviewPhoto.Picked -> uploadedByUri.getValue(photo.uri)
            }
        }
    }

    private fun UploadFileModel.withSize(measured: PhotoPixelSize?): UploadFileModel {
        val size = measured
            ?: PhotoPixelSize(width, height).takeIf { width > 0 && height > 0 }
            ?: PhotoPixelSize(DEFAULT_IMAGE_LENGTH, DEFAULT_IMAGE_LENGTH)
        return copy(width = size.width, height = size.height, ratio = size.width.toDouble() / size.height)
    }

    private companion object {
        const val DEFAULT_IMAGE_LENGTH = 500
    }
}
