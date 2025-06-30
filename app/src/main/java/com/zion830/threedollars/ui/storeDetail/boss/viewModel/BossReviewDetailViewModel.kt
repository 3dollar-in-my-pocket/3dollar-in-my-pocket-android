package com.zion830.threedollars.ui.storeDetail.boss.viewModel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import androidx.paging.cachedIn
import com.home.domain.data.store.ReasonModel
import com.home.domain.data.store.ReviewContentModel
import com.home.domain.data.store.ReviewSortType
import com.home.domain.repository.HomeRepository
import com.home.domain.request.ReportReasonsGroupType
import com.home.domain.request.ReportReviewModelRequest
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.Constants.STORE
import com.threedollar.network.data.feedback.FeedbackExistsResponse
import com.zion830.threedollars.R
import com.zion830.threedollars.utils.StringUtils.getString
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun putStoreReview(reviewId: Int, content: String, rating: Int) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.putStoreReview(reviewId, content, rating).collect {
                if (it.ok) {
                    showToast("리뷰가 수정되었습니다.")
                } else {
                    _serverError.emit(it.message)
                }
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
            homeRepository.putStickers(storeId.toString(), reviewId, listOf(sticker)).collect { res ->
                if (res.ok) {
                    _reviewPagingData.value = _reviewPagingData.value?.map { content ->
                        if (content.review.reviewId.toString() == reviewId) {
                            val updatedStickers = run {
                                val current = content.stickers
                                if (current.any { it.stickerId == sticker && it.reactedByMe }) {
                                    current.filterNot { it.stickerId == sticker && it.reactedByMe }
                                } else {
                                    current.find { it.stickerId == sticker }
                                        ?.let { tpl -> current + tpl.copy(reactedByMe = true) }
                                        ?: current
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
                    showToast(getString(R.string.report_completed))
                    _reviewPagingData.value = _reviewPagingData.value
                        ?.filter { it.review.reviewId != reviewId }
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
