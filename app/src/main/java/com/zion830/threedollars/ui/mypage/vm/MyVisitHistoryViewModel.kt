package com.zion830.threedollars.ui.mypage.vm

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.zion830.threedollars.datasource.MyVisitHistoryDataSourceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MyVisitHistoryViewModel @Inject constructor(private val myVisitHistoryDataSourceImpl: MyVisitHistoryDataSourceImpl) :
    BaseViewModel() {

    val myHistoryPager =
        Pager(PagingConfig(MyVisitHistoryDataSourceImpl.LOAD_SIZE)) { myVisitHistoryDataSourceImpl }.flow
}