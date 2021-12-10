package com.zion830.threedollars.ui.mypage

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.zion830.threedollars.repository.MyVisitHistoryDataSource
import com.zion830.threedollars.repository.UserRepository
import zion830.com.common.base.BaseViewModel

class MyVisitHistoryViewModel : BaseViewModel() {

    private val service = UserRepository()

    val myHistoryPager = Pager(PagingConfig(MyVisitHistoryDataSource.LOAD_SIZE)) {
        MyVisitHistoryDataSource()
    }.flow
}