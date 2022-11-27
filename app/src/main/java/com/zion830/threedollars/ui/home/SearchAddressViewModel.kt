package com.zion830.threedollars.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.datasource.MapDataSource
import com.zion830.threedollars.datasource.model.v2.response.kakao.SearchAddressResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class SearchAddressViewModel @Inject constructor(private val repository: MapDataSource) : BaseViewModel() {

    private val _searchResult: SingleLiveEvent<SearchAddressResponse?> = SingleLiveEvent()
    val searchResult: LiveData<SearchAddressResponse?> = _searchResult

    private val _searchResultLocation: SingleLiveEvent<LatLng> = SingleLiveEvent()
    val searchResultLocation: LiveData<LatLng> = _searchResultLocation

    fun search(query: String, latlng: LatLng) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _searchResult.postValue(repository.searchAddress(query, latlng))
        }
    }

    fun updateLatLng(latlng: LatLng) {
        _searchResultLocation.value = latlng
    }

    fun clear() {
        _searchResult.postValue(null)
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _searchResult.postValue(null)
    }
}