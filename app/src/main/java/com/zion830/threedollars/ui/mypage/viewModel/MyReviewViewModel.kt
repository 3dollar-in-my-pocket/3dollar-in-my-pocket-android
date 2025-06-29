package com.zion830.threedollars.ui.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.zion830.threedollars.datasource.MyFeedbacksDataSourceImpl
import com.zion830.threedollars.datasource.MyReviewDataSourceImpl
import com.zion830.threedollars.datasource.StoreDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val serviceApi: ServerApi,
    sharedPrefUtils: SharedPrefUtils
) : BaseViewModel() {

    val myReviewPager = Pager(PagingConfig(MyReviewDataSourceImpl.LOAD_SIZE)) {
        MyReviewDataSourceImpl(serviceApi)
    }.flow

    val userStoreReviewPager = Pager(PagingConfig(MyReviewDataSourceImpl.LOAD_SIZE)) {
        MyReviewDataSourceImpl(serviceApi, "USER_STORE")
    }.flow

    val myFeedbacksPager = Pager(PagingConfig(MyReviewDataSourceImpl.LOAD_SIZE)) {
        MyFeedbacksDataSourceImpl(serviceApi)
    }.flow

    private val _updateReview: MutableLiveData<Boolean> = MutableLiveData()
    val updateReview: LiveData<Boolean>
        get() = _updateReview

    private val _feedbacks = MutableLiveData<List<FeedbackTypeResponse>>()
    val feedbacks: LiveData<List<FeedbackTypeResponse>>
        get() = _feedbacks

    init {
        _feedbacks.value = sharedPrefUtils.getList<FeedbackTypeResponse>(SharedPrefUtils.BOSS_FEED_BACK_LIST)
    }

}