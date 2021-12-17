package com.zion830.threedollars.ui.mypage.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.zion830.threedollars.repository.MyStoreDataSource
import com.zion830.threedollars.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class MyStoreViewModel : BaseViewModel() {

    private val service = UserRepository()

    val myStorePager = Pager(PagingConfig(MyStoreDataSource.LOAD_SIZE)) {
        MyStoreDataSource()
    }.flow

    private val _totalCount: MutableLiveData<Int> = MutableLiveData()
    val totalCount: LiveData<Int> = _totalCount

    init {
        requestTotalCount()
    }

    fun requestTotalCount() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = service.getMyStore(null, 1)
            _totalCount.postValue(
                if (response.isSuccessful) response.body()?.data?.totalElements ?: 0 else 0
            )
        }
    }
}