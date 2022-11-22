package com.zion830.threedollars.ui.mypage.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.zion830.threedollars.datasource.MyStoreDataSourceImpl
import com.zion830.threedollars.datasource.UserDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MyStoreViewModel @Inject constructor(private val userDataSource: UserDataSource) :
    BaseViewModel() {

    val myStorePager =
        Pager(PagingConfig(MyStoreDataSourceImpl.LOAD_SIZE)) { MyStoreDataSourceImpl() }.flow

    private val _totalCount: MutableLiveData<Int> = MutableLiveData()
    val totalCount: LiveData<Int> = _totalCount

    init {
        requestTotalCount()
    }

    fun requestTotalCount() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = userDataSource.getMyStore(null, 1)
            _totalCount.postValue(
                if (response.isSuccessful) response.body()?.data?.totalElements ?: 0 else 0
            )
        }
    }
}