package com.threedollar.common.ext

import android.content.Context
import android.util.Base64
import com.threedollar.common.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun String.base64Encoding(): String = Base64.encodeToString(toByteArray(), Base64.NO_WRAP)

fun String?.isNotNullOrBlank() = !isNullOrBlank()

fun String?.isNotNullOrEmpty() = !isNullOrEmpty()

fun String?.toStringDefault(default: String = "") = this ?: default

fun String?.toFormattedNumber(): String = if (this.isNullOrBlank()) "" else DecimalFormat("#,###").format(this.toLong())

fun Int.toFormattedNumber(): String = DecimalFormat("#,###").format(this.toLong())

fun String.convertUpdateAt(context: Context): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    return try {
        val date = inputFormat.parse(this)
        val formattedDateStr = outputFormat.format(date)
        context.getString(R.string.update_at, formattedDateStr)
    } catch (e: Exception) {
        ""
    }
}

fun String.convertCreatedAt(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return try {
        val date = inputFormat.parse(this)
        if (date != null) {
            outputFormat.format(date)
        } else {
            this
        }
    } catch (e: Exception) {
        ""
    }
}

fun getMonthFirstDate(): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    return dateFormat.format(calendar.time)
}