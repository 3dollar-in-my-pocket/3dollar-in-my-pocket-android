package zion830.com.common.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.internal.ViewUtils.dpToPx

@BindingAdapter("visibleIf")
fun View.visibleIf(value: Boolean) {
    isVisible = value
}

@BindingAdapter("loadImage")
fun ImageView.loadDrawableImg(drawableResId: Int) {
    Glide.with(context)
        .load(ContextCompat.getDrawable(context, drawableResId))
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

@BindingAdapter("loadImage")
fun ImageView.loadUrlImg(url: String?) {
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

@SuppressLint("RestrictedApi")
@BindingAdapter("loadRoundImage")
fun ImageView.loadRoundUrlImg(url: String?) {
    if (url.isNullOrBlank()) {
        return
    }

    val requestOptions =
        RequestOptions().transform(CenterCrop(), RoundedCorners(dpToPx(context, 8).toInt()))
    setPadding(0, 0, 0, 0)

    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(requestOptions)
        .into(this)
}

@BindingAdapter("onSingleClick")
fun View.onSingleClick(listener: () -> Unit) {
    setOnClickListener {
        listener()
        this.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({ this.isEnabled = true }, 500)
    }
}