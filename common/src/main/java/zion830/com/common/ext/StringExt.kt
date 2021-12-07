package zion830.com.common.ext

import android.util.Base64
import java.text.DecimalFormat

fun String.base64Encoding(): String = Base64.encodeToString(toByteArray(), Base64.NO_WRAP)

fun String?.isNotNullOrBlank() = !isNullOrBlank()

fun String?.isNotNullOrEmpty() = !isNullOrEmpty()

fun String?.toFormattedNumber() = if (this.isNullOrBlank()) "" else DecimalFormat("#,###").format(this.toLong())

fun Int.toFormattedNumber() = DecimalFormat("#,###").format(this.toLong())