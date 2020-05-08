package zion830.com.common.ext

import android.util.Base64


fun String.base64Encoding(): String = Base64.encodeToString(toByteArray(), Base64.NO_WRAP)

fun String?.isNotNullOrBlank() = !isNullOrBlank()

fun String?.isNotNullOrEmpty() = !isNullOrEmpty()