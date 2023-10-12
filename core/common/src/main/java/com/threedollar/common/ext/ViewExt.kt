package com.threedollar.common.ext

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar

// control visibility
fun View.setVisibility(visibility: Boolean) {
    setVisibility(if (visibility) View.VISIBLE else View.GONE)
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.isVisible(): Boolean = visibility == View.VISIBLE

// show snackbar
fun View.showSnack(
    @StringRes resId: Int,
    length: Int = Snackbar.LENGTH_LONG,
    @ColorRes color: Int = android.R.color.white,
) {
    showSnack(context.getString(resId), length, color)
}

fun View.showSnack(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    @ColorRes color: Int = android.R.color.white,
) {
    Snackbar.make(this, message, length).run {
        setActionTextColor(ContextCompat.getColor(context, color))
        setAction(context.getString(android.R.string.ok)) { dismiss() }
        show()
    }
}

// control bottom sheet
fun View.expandBottomSheet() {
    try {
        val bottomSheetBehavior = BottomSheetBehavior.from(this)
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }
}

fun View.hideBottomSheet() {
    try {
        val bottomSheetBehavior = BottomSheetBehavior.from(this)
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }
}

fun View.isExpanded() = try {
    BottomSheetBehavior.from(this).state == BottomSheetBehavior.STATE_EXPANDED
} catch (e: IllegalArgumentException) {
    e.printStackTrace()
    false
}

fun TextView.textPartTypeface(changeText: String?, @StyleRes style: Int, isLast: Boolean = false) {
    if (changeText == null)
        return
    val index = if (isLast) {
        text.toString().lastIndexOf(changeText)
    } else {
        text.toString().indexOf(changeText)
    }
    if (index != -1) {
        val spannableString = SpannableString(text)

        spannableString.setSpan(
            StyleSpan(style),
            index,
            index + changeText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text = spannableString
    }
}

fun TextView.textPartColor(changeText: String?, color: Int, isLast: Boolean = false) {
    if (changeText == null)
        return
    val index = if (isLast) {
        text.toString().lastIndexOf(changeText)
    } else {
        text.toString().indexOf(changeText)
    }
    if (index != -1) {
        val spannableString = SpannableString(text)

        spannableString.setSpan(
            ForegroundColorSpan(color),
            index,
            index + changeText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text = spannableString
    }
}

fun ImageView.loadImage(imageUrl: String?) {
    imageUrl?.let {
        Glide.with(this)
            .load(it)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}
fun ImageView.loadImage(imageRes: Int?) {
    imageRes?.let {
        Glide.with(this)
            .load(it)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}

fun ImageView.loadCircleImage(imageUrl: String?) {
    imageUrl?.let {
        Glide.with(this)
            .load(it)
            .circleCrop()
            .into(this)
    }
}

fun ImageView.loadCircleImage(imageRes: Int?) {
    imageRes?.let {
        Glide.with(this)
            .load(it)
            .circleCrop()
            .into(this)
    }
}