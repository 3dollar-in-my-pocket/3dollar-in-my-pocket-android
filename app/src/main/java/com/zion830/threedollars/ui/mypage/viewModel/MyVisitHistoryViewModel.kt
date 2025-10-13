package com.zion830.threedollars.ui.mypage.viewModel

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.threedollar.common.base.BaseViewModel
import com.threedollar.network.api.ServerApi
import com.my.data.datasource.MyVisitHistoryDataSourceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyVisitHistoryViewModel @Inject constructor(
    private val serverApi: ServerApi
) : BaseViewModel() {

    val myHistoryPager =
        Pager(PagingConfig(MyVisitHistoryDataSourceImpl.LOAD_SIZE)) {
            MyVisitHistoryDataSourceImpl(serverApi)
        }.flow
}