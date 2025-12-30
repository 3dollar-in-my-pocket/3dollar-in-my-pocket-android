package com.zion830.threedollars.ui.storeDetail.boss.viewModel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.threedollar.domain.home.data.store.ReasonModel
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.threedollar.domain.home.data.store.ReviewSortType
import com.threedollar.domain.home.data.store.ReviewStatusType
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.domain.home.request.ReportReasonsGroupType
import com.threedollar.domain.home.request.ReportReviewModelRequest
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.Constants.STORE
import com.zion830.threedollars.utils.StringUtils.getString
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@HiltViewModel
class BossReviewDetailViewModel @Inject constructor(private val homeRepository: HomeRepository) :
    BaseViewModel() {
    private val _reportReasons = MutableStateFlow<List<ReasonModel>?>(null)
    val reportReasons: StateFlow<List<ReasonModel>?> get() = _reportReasons
    private val _reviewPagingData = MutableStateFlow<PagingData<ReviewContentModel>?>(null)
    val reviewPagingData get() = _reviewPagingData

    private val _feedbackExists = MutableStateFlow<Boolean?>(null)
    val feedbackExists: StateFlow<Boolean?> get() = _feedbackExists

    init {
        getReportReasons()
    }

    fun getReviewList(bossStoreId: Int, reviewSortType: ReviewSortType) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getStoreReview(bossStoreId, reviewSortType)
                .cachedIn(viewModelScope)
                .collect {
                    _reviewPagingData.value = it
                }
        }
    }

    fun updateReviewInPagingData(updatedReview: ReviewContentModel) {
        _reviewPagingData.value = _reviewPagingData.value?.map { content ->
            if (content.review.reviewId == updatedReview.review.reviewId) {
                updatedReview
            } else {
                content
            }
        }
    }

    fun putLike(storeId: Int, reviewId: String, sticker: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putStickers(storeId.toString(), reviewId, if (sticker.isEmpty()) emptyList() else listOf(sticker)).collect { res ->
                if (res.ok) {
                    _reviewPagingData.value = _reviewPagingData.value?.map { content ->
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
                } else {
                    _serverError.emit(res.message)
                }
            }
        }
    }

    fun reportReview(storeId: Int, reviewId: Int, request: ReportReviewModelRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.reportStoreReview(storeId, reviewId, request).collect { res ->
                if (res.ok) {
                    showToast(getString(CommonR.string.report_completed))
                    _reviewPagingData.value = _reviewPagingData.value?.map {
                        if (it.review.reviewId == reviewId) {
                            it.copy(review = it.review.copy(status = ReviewStatusType.FILTERED))
                        } else it
                    }
                } else {
                    _serverError.emit(res.message)
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
}
