package com.zion830.threedollars.ui.storeDetail.boss.listener

import com.threedollar.domain.home.data.store.ImageModel

interface OnReviewImageClickListener {
    fun onImageClick(clickedImage: ImageModel, allImages: List<ImageModel>, clickedIndex: Int)
}