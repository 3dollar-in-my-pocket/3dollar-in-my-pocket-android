package com.zion830.threedollars.ui.store_detail.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.model.v2.request.NewVisitHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class StoreCertificationViewModel : BaseViewModel() {

    val service = RetrofitBuilder.newServiceApi

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
            val result = service.addVisitHistory(
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