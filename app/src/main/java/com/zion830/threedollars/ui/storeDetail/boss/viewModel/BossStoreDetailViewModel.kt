package com.zion830.threedollars.ui.storeDetail.boss.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.Constants.STORE
import com.threedollar.domain.home.data.store.BossStoreDetailModel
import com.threedollar.domain.home.data.store.FavoriteModel
import com.threedollar.domain.home.data.store.ReasonModel
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.threedollar.domain.home.data.store.UploadFileModel
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.domain.home.request.ReportReviewModelRequest
import com.zion830.threedollars.utils.FileTypeConstants
import com.zion830.threedollars.utils.ImageUtils
import com.zion830.threedollars.utils.StringUtils.getString
import com.zion830.threedollars.utils.showCustomBlackToast
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@HiltViewModel
class BossStoreDetailViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.BOSS_STORE_DETAIL

    private val _bossStoreDetailModel = MutableStateFlow(BossStoreDetailModel())
    val bossStoreDetailModel: StateFlow<BossStoreDetailModel> get() = _bossStoreDetailModel

    private val _postStoreReview: MutableStateFlow<BaseResponse<ReviewContentModel>?> = MutableStateFlow(null)
    val postStoreReview: StateFlow<BaseResponse<ReviewContentModel>?> get() = _postStoreReview

    private val _favoriteModel: MutableStateFlow<FavoriteModel> = MutableStateFlow(FavoriteModel())
    val favoriteModel: StateFlow<FavoriteModel> get() = _favoriteModel

    private val _reportReasons = MutableStateFlow<List<ReasonModel>?>(null)
    val reportReasons: StateFlow<List<ReasonModel>?> get() = _reportReasons

    private val _reviewEditSuccess = MutableStateFlow<Boolean?>(null)
    val reviewEditSuccess: StateFlow<Boolean?> get() = _reviewEditSuccess

    private val _uploadImageStatus = MutableStateFlow(false)
    val uploadImageStatus: StateFlow<Boolean> get() = _uploadImageStatus

    private val _uploadProgress = MutableStateFlow<Pair<Int, Int>?>(null)
    val uploadProgress: StateFlow<Pair<Int, Int>?> get() = _uploadProgress

    private val _uploadedImages = MutableStateFlow<List<UploadFileModel>?>(null)
    val uploadedImages: StateFlow<List<UploadFileModel>?> get() = _uploadedImages

    private val _feedbackExists = MutableStateFlow<Boolean?>(null)
    val feedbackExists: StateFlow<Boolean?> get() = _feedbackExists

    private val _isInitialLoad = MutableStateFlow(false)
    val isInitialLoad: StateFlow<Boolean> get() = _isInitialLoad

    init {
        getReportReasons()
    }

    fun getFoodTruckStoreDetail(
        bossStoreId: String,
        latitude: Double,
        longitude: Double,
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _isInitialLoad.value = true
            homeRepository.getBossStoreDetail(bossStoreId = bossStoreId, deviceLatitude = latitude, deviceLongitude = longitude).collect {
                if (it.ok) {
                    _bossStoreDetailModel.value = it.data!!
                    _favoriteModel.value = it.data!!.favoriteModel
                } else {
                    _serverError.emit(it.message)
                }
                _isInitialLoad.value = false
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
                            _serverError.emit(result?.message ?: getString(CommonR.string.boss_review_image_upload_failed))
                            return@launch
                        }
                    } else {
                        _serverError.emit(getString(CommonR.string.boss_review_image_upload_error))
                        return@launch
                    }
                }

                _uploadProgress.value = Pair(originalUris.size, originalUris.size)
                _uploadedImages.value = uploadedFiles
            } catch (e: Exception) {
                _serverError.emit(getString(CommonR.string.boss_review_image_upload_error))
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
                    showCustomBlackToast(getString(CommonR.string.boss_review_success))
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
                    showCustomBlackToast(getString(CommonR.string.toast_favorite_add))
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
                    showCustomBlackToast(getString(CommonR.string.toast_favorite_delete))
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
            homeRepository.putStickers(storeId, reviewId, if (sticker.isEmpty()) emptyList() else listOf(sticker)).collect {
                if (it.ok) {
                    _bossStoreDetailModel.update { storeDetail ->
                        storeDetail.copy(
                            reviews = storeDetail.reviews.map { content ->
                                if (content.review.reviewId.toString() == reviewId) {
                                    val updatedStickers = run {
                                        val current = content.stickers.toMutableList()
                                        val index = current.indexOfFirst { it.stickerId == "LIKE" }
                                        if (index > -1) {
                                            current.find { it.stickerId == "LIKE" }?.let { tpl ->
                                                current[index] = tpl.copy(
                                                    reactedByMe = !tpl.reactedByMe,
                                                    count = if (tpl.reactedByMe) tpl.count - 1 else tpl.count + 1
                                                )
                                                current
                                            } ?: current
                                        } else {
                                            current
                                        }

                                    }
                                    content.copy(stickers = updatedStickers)
                                } else content
                            }
                        )
                    }
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun putStoreReview(reviewId: Int, content: String, rating: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putStoreReview(reviewId, content, rating).collect {
                if (it.ok) {
                    _reviewEditSuccess.value = true
                    showCustomBlackToast(getString(CommonR.string.success_edit_review))
                } else {
                    _reviewEditSuccess.value = false
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun updateReviewInList(updatedReview: ReviewContentModel) {
        _bossStoreDetailModel.update { storeDetail ->
            storeDetail.copy(
                reviews = storeDetail.reviews.map { reviewContent ->
                    if (reviewContent.review.reviewId == updatedReview.review.reviewId) {
                        updatedReview
                    } else {
                        reviewContent
                    }
                }
            )
        }
    }

    fun reportReview(storeId: Int, reviewId: Int, reportReviewModelRequest: ReportReviewModelRequest) {
        viewModelScope.launch {
            homeRepository.reportStoreReview(storeId, reviewId, reportReviewModelRequest).collect {
                if (it.ok) {
                    showToast(getString(CommonR.string.report_completed))
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

    fun checkFeedbackExists(storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.checkFeedbackExists(STORE, storeId).collect {
                if (it.ok) {
                    _feedbackExists.value = it.data?.exists ?: false
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    fun resetFeedbackExistsState() {
        _feedbackExists.value = null
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

    // GA Events - Store Detail
    fun sendClickFavorite(isOn: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.FAVORITE,
                additionalParams = mapOf(ParameterName.VALUE to if (isOn) "on" else "off")
            )
        )
    }

    fun sendClickShare() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.SHARE
            )
        )
    }

    fun sendClickWriteReview() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.WRITE_REVIEW
            )
        )
    }

    fun sendClickNavigation() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.NAVIGATION
            )
        )
    }

    fun sendClickSNSLog() {
        LogManager.sendEvent(ClickEvent(
            screen = screenName,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.SNS
        ))
    }

    fun sendClickZoomMap() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.ZOOM_MAP
            )
        )
    }

    fun sendClickCopyAddress() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.COPY_ADDRESS
            )
        )
    }

    fun sendClickCopyAccountLog() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.COPY_ACCOUNT
            )
        )
    }

    fun sendClickLikeReview(isLiked: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.LIKE,
                additionalParams = mapOf(ParameterName.VALUE to isLiked.toString())
            )
        )
    }

    // GA Events - Boss Store Review Write
    fun sendClickWriteReviewSubmit() {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.BOSS_STORE_REVIEW_WRITE,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.WRITE_REVIEW
            )
        )
    }
}
