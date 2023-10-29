package com.zion830.threedollars.ui.addstore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.model.v2.request.NewStoreRequest
import com.zion830.threedollars.datasource.model.v2.response.store.StoreDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditStoreViewModel @Inject constructor(private val repository: StoreDataSource) :
    BaseViewModel() {

    private val _storeInfo: MutableLiveData<StoreDetail?> = MutableLiveData()
    val storeInfo: LiveData<StoreDetail?>
        get() = _storeInfo

    val storeName = Transformations.map(_storeInfo) {
        it?.storeName
    }

    private val _editStoreResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val editStoreResult: LiveData<Boolean>
        get() = _editStoreResult

    val storeLocation: LiveData<LatLng> = Transformations.map(_storeInfo) {
        if (it != null) {
            LatLng(it.latitude, it.longitude)
        } else {
            null
        }
    }


    fun editStore(
        storeId: Int,
        newStore: NewStoreRequest
    ) {
        showLoading()

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = repository.updateStore(storeId, newStore)
            hideLoading()
            _editStoreResult.postValue(result.isSuccessful)
        }
    }
}