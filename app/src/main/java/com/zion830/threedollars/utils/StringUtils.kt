package com.zion830.threedollars.utils

import androidx.annotation.StringRes
import com.zion830.threedollars.GlobalApplication

object StringUtils {

    @JvmStatic
    fun getString(@StringRes resId: Int) = GlobalApplication.getContext().getString(resId)

    fun toReadableString(value: Float) = if (value.toInt().toFloat() == value) {
        value.toInt().toString()
    } else {
        value.toString()
    }

    fun getBearerTokenString(accessToken: String) = "Bearer $accessToken"
}