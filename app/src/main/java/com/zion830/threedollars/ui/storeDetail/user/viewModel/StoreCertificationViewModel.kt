package com.zion830.threedollars.ui.storeDetail.user.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.repository.HomeRepository
import com.threedollar.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreCertificationViewModel @Inject constructor(private val homeRepository: HomeRepository) : BaseViewModel() {

    private val _storeVisitResult = MutableSharedFlow<Boolean>()
    val storeVisitResult: SharedFlow<Boolean> get() = _storeVisitResult

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

    fun postStoreVisit(storeId: Int, isExist: Boolean) {
        showLoading()
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postStoreVisit(storeId, if (isExist) "EXISTS" else "NOT_EXISTS").collect {
                _storeVisitResult.emit(it.ok)
                if (!it.ok) _serverError.emit(it.message)
                hideLoading()
            }
        }
    }
}