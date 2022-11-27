package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.request.NewVisitHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class StoreCertificationViewModel @Inject constructor(private val storeDataSource: StoreDataSource) :
    BaseViewModel() {

    private val _addVisitHistoryResult = SingleLiveEvent<Int>()
    val addVisitHistoryResult: LiveData<Int> get() = _addVisitHistoryResult

    private val _needUpdate = SingleLiveEvent<Boolean>()
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
            val result = storeDataSource.addVisitHistory(
                NewVisitHistory(storeId, if (isExist) "EXISTS" else "NOT_EXISTS")
            )
            _addVisitHistoryResult.postValue(result.code())
            hideLoading()
        }
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _addVisitHistoryResult.postValue(499)
    }
}