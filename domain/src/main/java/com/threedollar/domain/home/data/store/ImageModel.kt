package com.threedollar.domain.home.data.store

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageModel(
    val imageUrl: String,
    val width: Int,
    val height: Int,
    val ratio: Int
) : Parcelable