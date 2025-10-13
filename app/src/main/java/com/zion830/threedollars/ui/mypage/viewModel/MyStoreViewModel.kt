package com.zion830.threedollars.ui.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.threedollar.common.base.BaseViewModel
import com.threedollar.network.api.ServerApi
import com.my.data.datasource.MyStoreDataSourceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyStoreViewModel @Inject constructor(
    private val serverApi: ServerApi
) : BaseViewModel() {

    val myStorePager =
        Pager(PagingConfig(MyStoreDataSourceImpl.LOAD_SIZE)) {
            MyStoreDataSourceImpl(serverApi)
        }.flow

    private val _totalCount: MutableLiveData<Int> = MutableLiveData()
    val totalCount: LiveData<Int> = _totalCount

    init {
        requestTotalCount()
    }

    private fun requestTotalCount() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = serverApi.getMyStores(0.0, 0.0, 20, null)
            _totalCount.postValue(
                if (response.isSuccessful) response.body()?.data?.cursor?.totalCount ?: 0 else 0
            )
        }
    }
}