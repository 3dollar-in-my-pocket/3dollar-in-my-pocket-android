package com.zion830.threedollars.utils

import android.net.Uri
import com.google.android.gms.common.util.IOUtils
import com.zion830.threedollars.GlobalApplication
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    const val IMAGE_MAX_SIZE = 10_000

    fun uriToFile(uri: Uri?): File? {
        if (uri == null) {
            return null
        }

        val resolver = GlobalApplication.getContext().contentResolver
        val tempFile = File.createTempFile("image${System.currentTimeMillis()}", ".png")
        resolver.openInputStream(uri)?.use { stream ->
            val outputStream = FileOutputStream(tempFile)
            IOUtils.copyStream(stream, outputStream)
            outputStream.close()
        }
        return tempFile
    }

    fun getFileSize(uri: Uri?) = uriToFile(uri)?.length()?.div(1024) ?: 0

    fun isAvailable(uri: Uri?): Boolean {
        if (uri == null) {
            return false
        }

        return getFileSize(uri) < IMAGE_MAX_SIZE
    }

    /**
     * 사진 업로드용 multipart 목록을 만든다. 용량 제한을 넘는 사진이 하나라도 있으면 null 이다.
     */
    fun toImageParts(uris: List<Uri?>): List<MultipartBody.Part>? {
        val parts = ArrayList<MultipartBody.Part>()
        uris.forEach { uri ->
            if (!isAvailable(uri)) return null
            uriToFile(uri)?.let { file ->
                parts.add(MultipartBody.Part.createFormData(IMAGE_PART_NAME, file.name, file.asRequestBody(IMAGE_MEDIA_TYPE.toMediaType())))
            }
        }
        return parts
    }

    private const val IMAGE_PART_NAME = "images"
    private const val IMAGE_MEDIA_TYPE = "image/*"
}