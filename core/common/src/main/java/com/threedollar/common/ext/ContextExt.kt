package com.threedollar.common.ext

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun Context.openUrl(url: String?) {
    if (url.isNullOrEmpty()) {
        return
    }
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    } catch (_: Exception) {
        // do nothing
    }
}
