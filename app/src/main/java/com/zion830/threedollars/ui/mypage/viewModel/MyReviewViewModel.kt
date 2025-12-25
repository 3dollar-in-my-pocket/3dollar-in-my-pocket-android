package com.zion830.threedollars.ui.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.api.ServerApi
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.my.data.datasource.MyFeedbacksDataSourceImpl
import com.my.data.datasource.MyReviewDataSourceImpl
import com.zion830.threedollars.datasource.StoreDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val serverApi: ServerApi,
    sharedPrefUtils: SharedPrefUtils
) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.MY_REVIEW

    val myReviewPager = Pager(PagingConfig(MyReviewDataSourceImpl.LOAD_SIZE)) {
        MyReviewDataSourceImpl(serverApi)
    }.flow

    val userStoreReviewPager = Pager(PagingConfig(MyReviewDataSourceImpl.LOAD_SIZE)) {
        MyReviewDataSourceImpl(serverApi)
    }.flow

    val myFeedbacksPager = Pager(PagingConfig(MyReviewDataSourceImpl.LOAD_SIZE)) {
        MyFeedbacksDataSourceImpl(serverApi)
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