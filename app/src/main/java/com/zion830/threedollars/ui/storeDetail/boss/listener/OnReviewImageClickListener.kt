package com.zion830.threedollars.ui.storeDetail.boss.listener

import com.home.domain.data.store.ImageModel

interface OnReviewImageClickListener {
    fun onImageClick(clickedImage: ImageModel, allImages: List<ImageModel>, clickedIndex: Int)
}