package com.zion830.threedollars.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    
    // 이미지 크기 제한을 더 현실적으로 설정 (10MB)
    const val IMAGE_MAX_SIZE_BYTES = 10 * 1024 * 1024  // 10MB
    
    // 압축된 이미지의 최대 크기 (2MB)
    const val COMPRESSED_MAX_SIZE_BYTES = 2 * 1024 * 1024  // 2MB
    
    // 이미지 품질 설정
    const val COMPRESS_QUALITY = 80
    
    // 최대 이미지 해상도
    const val MAX_IMAGE_WIDTH = 1920
    const val MAX_IMAGE_HEIGHT = 1920

    /**
     * URI를 압축된 파일로 변환 (EXIF 회전 정보 적용)
     */
    fun uriToCompressedFile(context: Context, uri: Uri?): File? {
        if (uri == null) return null
        
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            if (originalBitmap == null) return null
            
            // EXIF 회전 정보 적용
            val rotatedBitmap = applyExifRotation(context, uri, originalBitmap)
            
            // 이미지 크기 조정
            val resizedBitmap = resizeBitmap(rotatedBitmap, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT)
            
            // 압축된 파일 생성
            val compressedFile = File.createTempFile("compressed_image_${System.currentTimeMillis()}", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(compressedFile)
            
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, outputStream)
            outputStream.close()
            
            // 메모리 정리
            if (originalBitmap != rotatedBitmap) {
                originalBitmap.recycle()
            }
            if (rotatedBitmap != resizedBitmap) {
                rotatedBitmap.recycle()
            }
            resizedBitmap.recycle()
            
            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 비트맵 크기 조정
     */
    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        // 이미 적절한 크기라면 그대로 반환
        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }
        
        // 비율 계산
        val aspectRatio = width.toFloat() / height.toFloat()
        
        val newWidth: Int
        val newHeight: Int
        
        if (aspectRatio > 1) {
            // 가로가 더 긴 경우
            newWidth = maxWidth
            newHeight = (maxWidth / aspectRatio).toInt()
        } else {
            // 세로가 더 긴 경우
            newWidth = (maxHeight * aspectRatio).toInt()
            newHeight = maxHeight
        }
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * 파일 크기 확인 (압축 전 원본 체크)
     */
    fun isFileSizeValid(context: Context, uri: Uri?): Boolean {
        if (uri == null) return false
        
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val size = inputStream?.available() ?: 0
            inputStream?.close()
            size <= IMAGE_MAX_SIZE_BYTES
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 단일 URI를 MultipartBody.Part로 변환
     */
    fun uriToMultipartBody(context: Context, uri: Uri): MultipartBody.Part? {
        if (!isFileSizeValid(context, uri)) {
            return null // 크기 제한 초과
        }
        
        val compressedFile = uriToCompressedFile(context, uri) ?: return null
        val requestBody = compressedFile.asRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData("file", compressedFile.name, requestBody)
    }

    /**
     * URI 리스트를 MultipartBody.Part 리스트로 변환 (하위 호환성)
     */
    fun urisToMultipartBodies(context: Context, uris: List<Uri>): List<MultipartBody.Part>? {
        val parts = mutableListOf<MultipartBody.Part>()
        
        for (uri in uris) {
            if (!isFileSizeValid(context, uri)) {
                return null // 크기 제한 초과한 파일이 있으면 전체 실패
            }
            
            val compressedFile = uriToCompressedFile(context, uri) ?: return null
            val requestBody = compressedFile.asRequestBody("image/jpeg".toMediaType())
            val part = MultipartBody.Part.createFormData("files", compressedFile.name, requestBody)
            parts.add(part)
        }
        
        return parts
    }

    /**
     * EXIF 회전 정보를 적용하여 이미지 회전
     */
    private fun applyExifRotation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = ExifInterface(inputStream!!)
            inputStream.close()
            
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            
            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                ExifInterface.ORIENTATION_TRANSPOSE -> {
                    matrix.preScale(-1f, 1f)
                    matrix.postRotate(90f)
                }
                ExifInterface.ORIENTATION_TRANSVERSE -> {
                    matrix.preScale(-1f, 1f)
                    matrix.postRotate(270f)
                }
                else -> return bitmap // ORIENTATION_NORMAL
            }
            
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap // 실패 시 원본 반환
        }
    }

    /**
     * URI에서 이미지 크기 정보 추출 (EXIF 회전 정보 적용)
     */
    fun getImageDimensions(context: Context, uri: Uri?): Pair<Int, Int>? {
        if (uri == null) return null
        
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true // 실제 비트맵을 메모리에 로드하지 않고 크기만 얻음
            
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            
            if (options.outWidth > 0 && options.outHeight > 0) {
                // EXIF 회전 정보 확인
                val exifInputStream = context.contentResolver.openInputStream(uri)
                val exif = ExifInterface(exifInputStream!!)
                exifInputStream.close()
                
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                
                // 90도 또는 270도 회전인 경우 width와 height를 바꿔서 반환
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || 
                    orientation == ExifInterface.ORIENTATION_ROTATE_270 ||
                    orientation == ExifInterface.ORIENTATION_TRANSPOSE ||
                    orientation == ExifInterface.ORIENTATION_TRANSVERSE) {
                    Pair(options.outHeight, options.outWidth) // width와 height 바꿈
                } else {
                    Pair(options.outWidth, options.outHeight)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 임시 파일들 정리
     */
    fun cleanupTempFiles(context: Context) {
        try {
            val cacheDir = context.cacheDir
            cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("compressed_image_") || file.name.startsWith("image")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}