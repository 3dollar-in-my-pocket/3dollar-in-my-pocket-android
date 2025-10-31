package com.zion830.threedollars.ui.storeDetail.boss.binding

import android.content.res.ColorStateList
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@BindingAdapter("settingReviewBackground")
fun View.settingReviewBackground(index: Int) {
    backgroundTintList =
        ColorStateList.valueOf(ContextCompat.getColor(context, if (index % 2 == 1) DesignSystemR.color.color_white else DesignSystemR.color.pink_100))
}

@BindingAdapter("settingReviewComponentBackground")
fun View.settingReviewComponentBackground(index: Int) {
    backgroundTintList =
        ColorStateList.valueOf(ContextCompat.getColor(context, if (index % 2 != 1) DesignSystemR.color.color_white else DesignSystemR.color.pink_100))
}

@BindingAdapter("settingReplyBackground")
fun View.settingReplyBackground(index: Int) {
    backgroundTintList =
        ColorStateList.valueOf(ContextCompat.getColor(context, if (index % 2 == 1) DesignSystemR.color.color_white else DesignSystemR.color.gray10))
}
