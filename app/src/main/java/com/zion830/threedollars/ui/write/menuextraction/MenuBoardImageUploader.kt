package com.zion830.threedollars.ui.write.menuextraction

import com.threedollar.domain.home.repository.HomeRepository
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import javax.inject.Inject

class MenuBoardImageUploader @Inject constructor(
    private val homeRepository: HomeRepository,
) {

    suspend fun upload(storeId: Int, menuBoardImage: MenuBoardImage) {
        val file = menuBoardImage.consume()?.takeIf { it.exists() } ?: return
        val part = MultipartBody.Part.createFormData(PART_NAME, file.name, file.asRequestBody(JPEG_MEDIA_TYPE.toMediaType()))
        try {
            homeRepository.saveImages(listOf(part), storeId)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
        } finally {
            file.delete()
        }
    }

    private companion object {
        const val PART_NAME = "images"
        const val JPEG_MEDIA_TYPE = "image/jpeg"
    }
}
