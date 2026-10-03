package com.zion830.threedollars.ui.write.menuextraction

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

/**
 * 메뉴판 사진을 업로드용 multipart 로 만든다. 큰 사진은 긴 변 [MAX_IMAGE_SIDE_PX] 이하 JPEG 로 줄여
 * 업로드·인식 시간을 줄인다.
 */
class MenuPhotoFileReader @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /** 카메라 앱이 촬영 결과를 쓸 빈 파일의 content uri. */
    fun createCaptureUri(): Uri {
        photoDirectory().listFiles()
            ?.filter { it.name.startsWith(CAPTURE_FILE_PREFIX) }
            ?.forEach { it.delete() }
        val file = File(photoDirectory(), "$CAPTURE_FILE_PREFIX${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}$FILE_PROVIDER_SUFFIX", file)
    }

    /** 이전 업로드·촬영 파일은 지우고 [uri] 사진만 남긴다. 읽기·저장에 실패하면 null 이다. */
    suspend fun readAsPart(uri: Uri): MultipartBody.Part? = withContext(Dispatchers.IO) {
        runCatching {
            val bitmap = decodeScaled(uri) ?: return@runCatching null
            deleteOldUploads()
            val file = File(photoDirectory(), "$UPLOAD_FILE_PREFIX${System.currentTimeMillis()}.jpg")
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it) }
            bitmap.recycle()
            MultipartBody.Part.createFormData(PART_NAME, file.name, file.asRequestBody(JPEG_MEDIA_TYPE.toMediaType()))
        }.getOrNull()
    }

    private fun deleteOldUploads() {
        photoDirectory().listFiles()
            ?.filter { it.name.startsWith(UPLOAD_FILE_PREFIX) }
            ?.forEach { it.delete() }
    }

    private fun decodeScaled(uri: Uri): Bitmap? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, info, _ ->
                val scale = scaleFor(info.size.width, info.size.height)
                decoder.setTargetSize((info.size.width * scale).toInt(), (info.size.height * scale).toInt())
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSizeFor(bounds.outWidth, bounds.outHeight)
            }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        }
    }.getOrNull()

    private fun scaleFor(width: Int, height: Int): Float =
        (MAX_IMAGE_SIDE_PX.toFloat() / maxOf(width, height)).coerceAtMost(1f)

    private fun sampleSizeFor(width: Int, height: Int): Int {
        var sampleSize = 1
        while (maxOf(width, height) / (sampleSize * 2) >= MAX_IMAGE_SIDE_PX) sampleSize *= 2
        return sampleSize
    }

    private fun photoDirectory(): File = File(context.cacheDir, PHOTO_DIRECTORY).apply { mkdirs() }

    private companion object {
        const val MAX_IMAGE_SIDE_PX = 1024
        const val JPEG_QUALITY = 85
        const val PART_NAME = "file"
        const val JPEG_MEDIA_TYPE = "image/jpeg"
        const val PHOTO_DIRECTORY = "menu_extraction"
        const val UPLOAD_FILE_PREFIX = "upload_"
        const val CAPTURE_FILE_PREFIX = "capture_"
        const val FILE_PROVIDER_SUFFIX = ".fileprovider"
    }
}
