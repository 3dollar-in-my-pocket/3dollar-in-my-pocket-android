package com.zion830.threedollars.ui.mypage.vm

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.datasource.MyVisitHistoryDataSourceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyVisitHistoryViewModel @Inject constructor() :
    BaseViewModel() {

    val myHistoryPager =
        Pager(PagingConfig(MyVisitHistoryDataSourceImpl.LOAD_SIZE)) { MyVisitHistoryDataSourceImpl() }.flow
}