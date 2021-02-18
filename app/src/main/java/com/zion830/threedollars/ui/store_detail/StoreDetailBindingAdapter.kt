package com.zion830.threedollars.ui.store_detail

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.toSpannable
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.internal.ViewUtils
import com.willy.ratingbar.ScaleRatingBar
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.model.response.Image
import com.zion830.threedollars.ui.addstore.StoreImage
import com.zion830.threedollars.utils.StringUtils.getString

@BindingAdapter("bindRating")
fun ScaleRatingBar.bindRating(value: Float) {
    rating = value
}

@BindingAdapter("setNameBold")
fun TextView.setNameBold(name: String?) {
    text = buildSpannedString {
        bold { append(name ?: "?") }
        append(getString(R.string.writer))
    }
}

@BindingAdapter("setDistance")
fun TextView.setDistance(distance: Int) {
    val spannableText = when {
        distance <= 50 -> "50m${context.getString(R.string.store_distance)}"
        distance <= 500 -> "500m${context.getString(R.string.store_distance)}"
        distance <= 1000 -> "1km${context.getString(R.string.store_distance)}"
        else -> context.getString(R.string.store_distance_long)
    }.toSpannable()

    val endIndex = spannableText.indexOfFirst { it == '에' }
    spannableText.setSpan(StyleSpan(Typeface.BOLD), 0, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
    text = spannableText
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