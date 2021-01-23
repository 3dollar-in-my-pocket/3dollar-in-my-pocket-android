package com.zion830.threedollars.utils

import android.net.Uri
import com.google.android.gms.common.util.IOUtils
import com.zion830.threedollars.GlobalApplication
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun uriToFile(uri: Uri?): File? {
        if (uri == null) {
            return null
        }

        val resolver = GlobalApplication.getContext().contentResolver
        val tempFile = File.createTempFile("image${System.currentTimeMillis()}", ".png")
        resolver.openInputStream(uri).use { stream ->
            val outputStream = FileOutputStream(tempFile)
            IOUtils.copyStream(stream, outputStream)
            outputStream.close()
        }
        return tempFile
    }
}