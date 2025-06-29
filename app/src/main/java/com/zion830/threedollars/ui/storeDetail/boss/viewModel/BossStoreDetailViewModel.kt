package com.zion830.threedollars.ui.storeDetail.boss.viewModel

import androidx.lifecycle.viewModelScope
import com.home.domain.data.store.BossStoreDetailModel
import com.home.domain.data.store.FavoriteModel
import com.home.domain.data.store.ReasonModel
import com.home.domain.data.store.SaveImagesModel
import com.home.domain.data.store.UploadFileModel
import com.home.domain.repository.HomeRepository
import com.home.domain.request.ReportReviewModelRequest
import com.home.domain.request.ReportReasonsGroupType
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.home.domain.data.store.ReviewContentModel
import com.zion830.threedollars.R
import com.zion830.threedollars.utils.FileTypeConstants
import com.zion830.threedollars.utils.ImageUtils
import com.zion830.threedollars.utils.StringUtils.getString
import com.zion830.threedollars.utils.showCustomBlackToast
import com.zion830.threedollars.utils.showToast
import android.content.Context
import android.net.Uri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BossStoreDetailViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : BaseViewModel() {

    private val _bossStoreDetailModel = MutableStateFlow(BossStoreDetailModel())
    val bossStoreDetailModel: StateFlow<BossStoreDetailModel> get() = _bossStoreDetailModel

    private val _postFeedback: MutableStateFlow<BaseResponse<String>?> = MutableStateFlow(null)
    val postFeedback: StateFlow<BaseResponse<String>?> get() = _postFeedback

    private val _postStoreReview: MutableStateFlow<BaseResponse<ReviewContentModel>?> = MutableStateFlow(null)
    val postStoreReview: StateFlow<BaseResponse<ReviewContentModel>?> get() = _postStoreReview

    private val _favoriteModel: MutableStateFlow<FavoriteModel> = MutableStateFlow(FavoriteModel())
    val favoriteModel: StateFlow<FavoriteModel> get() = _favoriteModel

    private val _reportReasons = MutableStateFlow<List<ReasonModel>?>(null)
    val reportReasons: StateFlow<List<ReasonModel>?> get() = _reportReasons

    private val _uploadImageStatus = MutableStateFlow(false)
    val uploadImageStatus: StateFlow<Boolean> get() = _uploadImageStatus

    private val _uploadProgress = MutableStateFlow<Pair<Int, Int>?>(null)
    val uploadProgress: StateFlow<Pair<Int, Int>?> get() = _uploadProgress

    private val _uploadedImages = MutableStateFlow<List<UploadFileModel>?>(null)
    val uploadedImages: StateFlow<List<UploadFileModel>?> get() = _uploadedImages

    init {
        getReportReasons()
    }

    fun getFoodTruckStoreDetail(
        bossStoreId: String,
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getBossStoreDetail(bossStoreId = bossStoreId, deviceLatitude = latitude, deviceLongitude = longitude).collect {
                if (it.ok) {
                    _bossStoreDetailModel.value = it.data!!
                    _favoriteModel.value = it.data!!.favoriteModel
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun postBossStoreFeedback(bossStoreId: String, bossStoreFeedbackRequest: List<String>) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postFeedback(BOSS_STORE, bossStoreId, bossStoreFeedbackRequest).collect {
                if (it.ok) {
                    _postFeedback.value = it
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun uploadImages(context: Context, originalUris: List<Uri>) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _uploadImageStatus.value = true
            _uploadProgress.value = Pair(0, originalUris.size)
            
            try {
                val uploadedFiles = mutableListOf<UploadFileModel>()
                
                for ((index, uri) in originalUris.withIndex()) {
                    _uploadProgress.value = Pair(index, originalUris.size)
                    
                    val multipartBody = ImageUtils.uriToMultipartBody(context, uri)
                    if (multipartBody != null) {
                        val result = homeRepository.uploadFile(
                            fileType = FileTypeConstants.STORE_REVIEW_IMAGE,
                            file = multipartBody
                        )
                        
                        if (result?.ok == true && result.data != null) {
                            var uploadFile = result.data!!
                            
                            // 이미지 크기 보정
                            if (uploadFile.width <= 0 || uploadFile.height <= 0) {
                                val dimensions = ImageUtils.getImageDimensions(context, uri)
                                if (dimensions != null) {
                                    val (width, height) = dimensions
                                    uploadFile = uploadFile.copy(
                                        width = width,
                                        height = height,
                                        ratio = if (height > 0) width.toDouble() / height else 0.0
                                    )
                                }
                            }
                            
                            uploadedFiles.add(uploadFile)
                        } else {
                            _serverError.emit(result?.message ?: getString(R.string.boss_review_image_upload_failed))
                            return@launch
                        }
                    } else {
                        _serverError.emit(getString(R.string.boss_review_image_upload_error))
                        return@launch
                    }
                }
                
                _uploadProgress.value = Pair(originalUris.size, originalUris.size)
                _uploadedImages.value = uploadedFiles
            } catch (e: Exception) {
                _serverError.emit(getString(R.string.boss_review_image_upload_error))
            } finally {
                _uploadImageStatus.value = false
                _uploadProgress.value = null
            }
        }
    }

    fun postStoreReview(
        storeId: String,
        contents: String,
        rating: Int,
        images: List<UploadFileModel>,
        feedbacks: List<String>
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postBossStoreReview(storeId, contents, rating, images, feedbacks).collect {
                if (it.ok) {
                    _postStoreReview.value = it
                    showCustomBlackToast(getString(R.string.boss_review_success))
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun putFavorite(storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putFavorite(storeId).collect { model ->
                if (model.ok) {
                    showCustomBlackToast(getString(R.string.toast_favorite_add))
                    _favoriteModel.update {
                        it.copy(isFavorite = true, totalSubscribersCount = it.totalSubscribersCount + 1)
                    }
                } else {
                    model.message?.let { message -> showCustomBlackToast(message) }
                }
            }
        }
    }

    fun deleteFavorite(storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteFavorite(storeId).collect { model ->
                if (model.ok) {
                    showCustomBlackToast(getString(R.string.toast_favorite_delete))
                    _favoriteModel.update {
                        it.copy(isFavorite = false, totalSubscribersCount = it.totalSubscribersCount - 1)
                    }
                } else {
                    model.message?.let { message -> showCustomBlackToast(message) }
                }
            }
        }
    }

    fun putLike(storeId: String, reviewId: String, sticker: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putStickers(storeId, reviewId, listOf(sticker)).collect {
                if (it.ok) {
                    _bossStoreDetailModel.update { storeDetail ->
                        storeDetail.copy(
                            reviews = storeDetail.reviews.map { reviewContent ->
                                if (reviewContent.review.reviewId.toString() == reviewId) {
                                    reviewContent.copy(
                                        stickers = run {
                                            val current = reviewContent.stickers
                                            if (current.any { it.stickerId == sticker && it.reactedByMe }) {
                                                current.filterNot { it.stickerId == sticker && it.reactedByMe }
                                            } else {
                                                val template = current.find { it.stickerId == sticker }
                                                if (template != null) {
                                                    current + template.copy(reactedByMe = true)
                                                } else current
                                            }
                                        }
                                    )
                                } else reviewContent
                            }
                        )
                    }
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun reportReview(storeId: Int, reviewId: Int, reportReviewModelRequest: ReportReviewModelRequest) {
        viewModelScope.launch {
            homeRepository.reportStoreReview(storeId, reviewId, reportReviewModelRequest).collect {
                if (it.ok) {
                    showToast("신고 완료!")
                    _bossStoreDetailModel.update { storeDetail ->
                        storeDetail.copy(
                            reviews = storeDetail.reviews.filter { reviewContent ->
                                reviewContent.review.reviewId != reviewId
                            }
                        )
                    }
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    private fun getReportReasons() {
        viewModelScope.launch {
            homeRepository.getReportReasons(ReportReasonsGroupType.REVIEW).collect {
                if (it.ok) {
                    _reportReasons.value = it.data?.reasonModels
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }
}
