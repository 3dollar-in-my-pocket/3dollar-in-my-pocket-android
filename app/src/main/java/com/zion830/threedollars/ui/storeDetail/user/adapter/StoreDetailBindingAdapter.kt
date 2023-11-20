package com.zion830.threedollars.ui.storeDetail.user.adapter

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.internal.ViewUtils
import com.willy.ratingbar.ScaleRatingBar
import com.zion830.threedollars.R
import com.home.domain.data.store.StoreImage
import com.zion830.threedollars.utils.StringUtils.getString

@BindingAdapter("bindRating")
fun ScaleRatingBar.bindRating(value: Float) {
    rating = value
}

@SuppressLint("RestrictedApi")
@BindingAdapter("loadRoundImage")
fun ImageView.loadRoundStoreImage(storeImage: StoreImage?) {
    if (storeImage == null) {
        return
    }

    val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(ViewUtils.dpToPx(context, 8).toInt()))
    setPadding(0, 0, 0, 0)

    Glide.with(context)
        .load(storeImage.uri ?: storeImage.url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(requestOptions)
        .into(this)
}

@BindingAdapter("storeType")
fun TextView.storeType(storeType: String?) {
    text = when (storeType) {
        "STORE" -> getString(R.string.store)
        "CONVENIENCE_STORE" -> getString(R.string.convenience_store)
        "ROAD" -> getString(R.string.road)
        else -> {
            isVisible = false
            ""
        }
    }
}

