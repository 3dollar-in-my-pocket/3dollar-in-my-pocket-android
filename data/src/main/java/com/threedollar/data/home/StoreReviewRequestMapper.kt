package com.threedollar.data.home

import com.threedollar.domain.home.data.store.UploadFileModel
import com.threedollar.network.request.ImageRequest
import com.threedollar.network.request.StoreReviewRequest

/** 리뷰 작성(`POST /api/v3/store/review`). 피드백은 더 받지 않아 항상 빈 배열을 보낸다. */
internal fun storeReviewCreateRequest(contents: String, rating: Int?, storeId: Int, images: List<UploadFileModel>) = StoreReviewRequest(
    contents = contents,
    rating = rating,
    storeId = storeId,
    images = images.map { it.asImageRequest() },
    feedbacks = emptyList(),
)

/** 리뷰 수정(`PUT /api/v2/store/review/{reviewId}`). 남길 기존 사진과 새로 올린 사진을 모두 보낸다. */
internal fun storeReviewUpdateRequest(contents: String, rating: Int, images: List<UploadFileModel>) = StoreReviewRequest(
    contents = contents,
    rating = rating,
    images = images.map { it.asImageRequest() },
)

private fun UploadFileModel.asImageRequest() = ImageRequest(url = imageUrl, width = width, height = height)
