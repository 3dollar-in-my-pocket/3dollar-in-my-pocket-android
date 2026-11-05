package com.zion830.threedollars.ui.review

import com.threedollar.domain.home.data.store.UploadFileModel

/** 리뷰 시트에 붙은 사진. 이미 올라간 사진(수정 시 기존 사진)과 이번에 고른 사진을 구분한다. */
sealed interface ReviewPhoto {
    val key: String

    data class Uploaded(val image: UploadFileModel) : ReviewPhoto {
        override val key: String get() = image.imageUrl
    }

    data class Picked(val uri: String) : ReviewPhoto {
        override val key: String get() = uri
    }
}

/**
 * 리뷰 작성·수정 시트 상태. 제보 가게와 사장님 가게가 같은 규칙을 쓴다.
 * 별점과 내용이 있어야 등록할 수 있고, 사진은 선택이며 [MAX_PHOTO_COUNT] 장까지 붙인다.
 */
data class ReviewWriteState(
    val storeId: Int = 0,
    val storeName: String = "",
    val reviewId: Long? = null,
    val rating: Int = 0,
    val contents: String = "",
    val photos: List<ReviewPhoto> = emptyList(),
    val isSubmitting: Boolean = false,
) {
    val isEdit: Boolean get() = reviewId != null
    val remainingPhotoCount: Int get() = (MAX_PHOTO_COUNT - photos.size).coerceAtLeast(0)
    val canAddPhoto: Boolean get() = remainingPhotoCount > 0
    val canSubmit: Boolean get() = rating > 0 && contents.isNotBlank() && !isSubmitting

    fun withRating(rating: Int): ReviewWriteState = copy(rating = rating.coerceIn(0, MAX_RATING))

    fun withContents(contents: String): ReviewWriteState = copy(contents = contents.take(MAX_CONTENTS_LENGTH))

    fun addPickedPhotos(uris: List<String>): ReviewWriteState {
        val newPhotos = uris.distinct()
            .filterNot { uri -> photos.any { it.key == uri } }
            .take(remainingPhotoCount)
            .map { ReviewPhoto.Picked(it) }
        return copy(photos = photos + newPhotos)
    }

    fun removePhoto(key: String): ReviewWriteState = copy(photos = photos.filterNot { it.key == key })

    companion object {
        const val MAX_PHOTO_COUNT = 10
        const val MAX_CONTENTS_LENGTH = 300
        const val MAX_RATING = 5
    }
}
