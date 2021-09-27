package com.zion830.threedollars.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.MapRepository
import com.zion830.threedollars.repository.model.response.SearchAddressResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class SearchAddressViewModel : BaseViewModel() {

    private val repository: MapRepository = MapRepository(RetrofitBuilder.mapService)

    private val _searchResult: MutableLiveData<SearchAddressResponse> = MutableLiveData()
    val searchResult: LiveData<SearchAddressResponse> = _searchResult

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _searchResult.postValue(repository.searchAddress(query))
        }
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _searchResult.value = null
    }
}