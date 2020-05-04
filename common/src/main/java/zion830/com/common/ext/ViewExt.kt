package zion830.com.common.ext

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import zion830.com.common.R

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
    @ColorRes color: Int? = null
) {
    showSnack(context.getString(resId), length, color)
}

fun View.showSnack(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    @ColorRes color: Int? = null
) {
    Snackbar.make(this, message, length).run {
        color?.let { setActionTextColor(ContextCompat.getColor(context, color)) }
        setAction(context.getString(R.string.ok)) { dismiss() }
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