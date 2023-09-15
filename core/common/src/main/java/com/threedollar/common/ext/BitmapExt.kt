package zion830.com.common.ext

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

const val BITMAP_QUALITY = 100

fun Bitmap.base64Encoding(): String = try {
    val outputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, outputStream)
    Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
} catch (e: Exception) {
    e.printStackTrace()
    ""
}