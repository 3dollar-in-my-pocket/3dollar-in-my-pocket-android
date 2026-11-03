package com.zion830.threedollars.ui.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.data.my.datasource.MyReviewDataSourceImpl
import com.threedollar.network.api.ServerApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val serverApi: ServerApi,
) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.MY_REVIEW

    val myReviewPager = Pager(PagingConfig(MyReviewDataSourceImpl.LOAD_SIZE)) {
        MyReviewDataSourceImpl(serverApi)
    }.flow

    val userStoreReviewPager = Pager(PagingConfig(MyReviewDataSourceImpl.LOAD_SIZE)) {
        MyReviewDataSourceImpl(serverApi)
    }.flow

    private val _updateReview: MutableLiveData<Boolean> = MutableLiveData()
    val updateReview: LiveData<Boolean>
        get() = _updateReview

}