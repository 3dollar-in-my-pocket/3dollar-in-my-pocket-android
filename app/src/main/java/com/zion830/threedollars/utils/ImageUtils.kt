package com.zion830.threedollars.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.libraries.maps.model.BitmapDescriptor
import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.material.internal.ViewUtils
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.repository.model.response.Image

@SuppressLint("RestrictedApi")
@BindingAdapter("loadRoundImage")
fun ImageView.loadRoundUriImg(list: List<Image>?) {
    if (list.isNullOrEmpty()) {
        return
    }

    val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(ViewUtils.dpToPx(context, 8).toInt()))
    setPadding(0, 0, 0, 0)

    Glide.with(context)
        .load(list.first())
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(requestOptions)
        .into(this)
}

fun getBitmapDescriptor(@DrawableRes resId: Int): BitmapDescriptor? {
    val vectorDrawable = ContextCompat.getDrawable(GlobalApplication.getContext(), resId)
    val size = SizeUtils.dpToPx(19f)
    vectorDrawable?.setBounds(0, 0, size, size)
    val bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    vectorDrawable?.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}