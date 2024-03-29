package com.zion830.threedollars.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.network.data.kakao.SearchAddressResponse
import com.zion830.threedollars.datasource.MapDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchAddressViewModel @Inject constructor(private val repository: MapDataSource) : BaseViewModel() {

    private val _searchResult: MutableLiveData<SearchAddressResponse?> = MutableLiveData()
    val searchResult: LiveData<SearchAddressResponse?> = _searchResult

    private val _searchResultLocation: MutableLiveData<LatLng> = MutableLiveData()
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