package com.zion830.threedollars.ui.review

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.home.repository.HomeRepository
import com.zion830.threedollars.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface ReviewWriteEffect {
    data class Submitted(val isEdit: Boolean) : ReviewWriteEffect
    data class ShowError(val message: String?) : ReviewWriteEffect
}

@HiltViewModel
class ReviewWriteViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.REVIEW_BOTTOM_SHEET

    private val _state = MutableStateFlow(ReviewWriteState())
    val state: StateFlow<ReviewWriteState> = _state.asStateFlow()

    private val _effect = Channel<ReviewWriteEffect>(Channel.BUFFERED)
    val effect: Flow<ReviewWriteEffect> = _effect.receiveAsFlow()

    private var isInitialized = false

    private val submitter = ReviewSubmitter(
        uploadPhotos = ::uploadPhotos,
        postReview = { contents, rating, storeId, images ->
            homeRepository.postStoreReview(contents, rating, storeId, images).first().toSubmitResult()
        },
        putReview = { reviewId, contents, rating, images ->
            homeRepository.putStoreReview(reviewId, contents, rating, images).first().toSubmitResult()
        },
    )

    fun initialize(initialState: ReviewWriteState) {
        if (isInitialized) return
        isInitialized = true
        _state.value = initialState
    }

    fun updateRating(rating: Int) = _state.update { it.withRating(rating) }

    fun updateContents(contents: String) = _state.update { it.withContents(contents) }

    fun addPhotos(uris: List<String>) = _state.update { it.addPickedPhotos(uris) }

    fun removePhoto(key: String) = _state.update { it.removePhoto(key) }

    fun submit() {
        val current = _state.value
        if (!current.canSubmit) return
        sendClickSubmit(current.rating)
        _state.update { it.copy(isSubmitting = true) }
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = runCatching { submitter.submit(current) }.getOrElse { ReviewSubmitResult.Failure(it.message) }
            _state.update { it.copy(isSubmitting = false) }
            val effect = when (result) {
                ReviewSubmitResult.Success -> ReviewWriteEffect.Submitted(isEdit = current.isEdit)
                is ReviewSubmitResult.Failure -> ReviewWriteEffect.ShowError(result.message)
            }
            _effect.send(effect)
        }
    }

    private suspend fun uploadPhotos(uris: List<String>) = withContext(Dispatchers.IO) {
        val parts = FileUtils.toImageParts(uris.map { it.toUri() }, FileUtils.UPLOAD_FILES_PART_NAME) ?: return@withContext null
        homeRepository.uploadFilesBulk(STORE_REVIEW_IMAGE, parts)?.takeIf { it.ok }?.data
    }

    private fun BaseResponse<*>.toSubmitResult(): ReviewSubmitResult =
        if (ok) ReviewSubmitResult.Success else ReviewSubmitResult.Failure(message)

    private fun sendClickSubmit(rating: Int) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.REVIEW_BOTTOM_SHEET,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.WRITE_REVIEW,
                additionalParams = mapOf(ParameterName.VALUE to rating.toString()),
            )
        )
    }

    companion object {
        private const val STORE_REVIEW_IMAGE = "STORE_REVIEW_IMAGE"
    }
}
