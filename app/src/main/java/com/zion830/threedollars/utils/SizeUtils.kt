package com.zion830.threedollars.utils

import android.util.TypedValue
import com.zion830.threedollars.GlobalApplication

object SizeUtils {

    fun dpToPx(value: Float): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value, GlobalApplication.getContext().resources.displayMetrics
        )
        return px.toInt()
    }
}