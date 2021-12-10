package com.zion830.threedollars.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.MyReviewDataSource
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.v2.request.EditReviewRequest
import com.zion830.threedollars.repository.model.v2.request.NewReview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class MyReviewViewModel : BaseViewModel() {

    private val repository = StoreRepository()

    val myReviewPager = Pager(PagingConfig(MyReviewDataSource.LOAD_SIZE)) {
        MyReviewDataSource()
    }.flow

    private val _updateReview: MutableLiveData<Boolean> = MutableLiveData()
    val updateReview: LiveData<Boolean>
        get() = _updateReview

    fun editReview(reviewId: Int, newReview: NewReview) {
        if (newReview.contents.isBlank()) {
            _updateReview.postValue(false)
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val request = EditReviewRequest(newReview.contents, newReview.rating)
            repository.editReview(reviewId, request)
            _msgTextId.postValue(R.string.success_edit_review)
            _updateReview.postValue(true)
        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            repository.deleteReview(reviewId)
            _msgTextId.postValue(R.string.success_delete_review)
            _updateReview.postValue(true)
        }
    }
}