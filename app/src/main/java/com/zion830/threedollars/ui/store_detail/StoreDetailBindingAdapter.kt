package com.zion830.threedollars.ui.store_detail

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.internal.ViewUtils
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.model.response.Image
import com.zion830.threedollars.ui.addstore.StoreImage


@BindingAdapter("setDistance")
fun TextView.setDistance(distance: Int) {
    text = when {
        distance <= 50 -> "50${context.getString(R.string.store_distance)}"
        distance <= 500 -> "500${context.getString(R.string.store_distance)}"
        distance <= 1000 -> context.getString(R.string.store_distance_1km)
        else -> context.getString(R.string.store_distance_long)
    }
}

@SuppressLint("RestrictedApi")
@BindingAdapter("loadFirstImage")
fun ImageView.loadFirstImage(image: List<Image>?) {
    if (image.isNullOrEmpty()) {
        return
    }

    val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(ViewUtils.dpToPx(context, 8).toInt()))
    setPadding(0, 0, 0, 0)

    Glide.with(context)
        .load(image.first().url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(requestOptions)
        .into(this)
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