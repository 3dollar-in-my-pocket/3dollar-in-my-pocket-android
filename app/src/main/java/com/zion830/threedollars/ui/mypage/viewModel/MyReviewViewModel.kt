package com.zion830.threedollars.ui.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.MyReviewDataSourceImpl
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.request.EditReviewRequest
import com.zion830.threedollars.datasource.model.v2.request.NewReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(private val storeDataSource: StoreDataSource) : BaseViewModel() {

    val myReviewPager = Pager(PagingConfig(MyReviewDataSourceImpl.LOAD_SIZE)) {
        MyReviewDataSourceImpl()
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
            storeDataSource.editReview(reviewId, request)
            _msgTextId.postValue(R.string.success_edit_review)
            _updateReview.postValue(true)
        }
    }

    fun deleteReview(reviewId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            storeDataSource.deleteReview(reviewId)
            _msgTextId.postValue(R.string.success_delete_review)
            _updateReview.postValue(true)
        }
    }
}