package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.datasource.StoreDataSource
import com.threedollar.network.request.PostStoreVisitRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreCertificationViewModel @Inject constructor(private val storeDataSource: StoreDataSource) : BaseViewModel() {

    private val _addVisitHistoryResult = MutableLiveData<Int>()
    val addVisitHistoryResult: LiveData<Int> get() = _addVisitHistoryResult

    private val _needUpdate = MutableLiveData<Boolean>()
    val needUpdate: LiveData<Boolean> get() = _needUpdate

    init {
        viewModelScope.launch {
            while (true) {
                _needUpdate.value = true
                delay(2000L)
            }
        }
    }

    fun addVisitHistory(storeId: Int, isExist: Boolean) {
        showLoading()

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//            val result = storeDataSource.addVisitHistory(
//                PostStoreVisitRequest(storeId, if (isExist) "EXISTS" else "NOT_EXISTS")
//            )
//            _addVisitHistoryResult.postValue(result.code())
            hideLoading()
        }
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _addVisitHistoryResult.postValue(499)
    }
}