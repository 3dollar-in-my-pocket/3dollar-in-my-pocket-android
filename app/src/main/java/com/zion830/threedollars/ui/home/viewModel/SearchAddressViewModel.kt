package com.zion830.threedollars.ui.home.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.home.domain.data.place.PlaceModel
import com.home.domain.repository.HomeRepository
import com.home.domain.request.PlaceRequest
import com.home.domain.request.PlaceType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.network.data.kakao.SearchAddressResponse
import com.zion830.threedollars.datasource.MapDataSource
import com.zion830.threedollars.utils.NaverMapUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchAddressViewModel @Inject constructor(private val homeRepository: HomeRepository, private val repository: MapDataSource) :
    BaseViewModel() {

    private val _searchResult: MutableStateFlow<SearchAddressResponse?> = MutableStateFlow(null)
    val searchResult: StateFlow<SearchAddressResponse?> get() = _searchResult

    private val _searchResultLocation = MutableStateFlow(NaverMapUtils.DEFAULT_LOCATION)
    val searchResultLocation: StateFlow<LatLng> get() = _searchResultLocation

    private val _searchAddress = MutableStateFlow("")
    val searchAddress: StateFlow<String> get() = _searchAddress

    private val _recentSearchPagingData = MutableStateFlow<PagingData<PlaceModel>?>(null)
    val recentSearchPagingData: StateFlow<PagingData<PlaceModel>?> get() = _recentSearchPagingData

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val searchAddressResponse = repository.searchAddress(query, _searchResultLocation.value)
            _searchResult.value = searchAddressResponse
            if (searchAddressResponse.documents.isNotEmpty()) {
                searchAddressResponse.documents.first()
            }
        }
    }

    fun updateSearchAddress(query: String) {
        _searchAddress.value = query
    }

    fun updateLatLng(latlng: LatLng) {
        _searchResultLocation.value = latlng
    }

    fun clear() {
        _searchResult.value = null
    }

    fun getPlace() {
        viewModelScope.launch {
            homeRepository.getPlace(placeType = PlaceType.RECENT_SEARCH).cachedIn(viewModelScope).collect {
                _recentSearchPagingData.value = it
            }
        }
    }

    fun postPlace(placeRequest: PlaceRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.postPlace(placeRequest = placeRequest, placeType = PlaceType.RECENT_SEARCH).collect {
                Log.d("postPlace", it.data.toString())
            }
        }
    }

    fun deletePlace(placeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deletePlace(placeType = PlaceType.RECENT_SEARCH, placeId = placeId).collect {
                getPlace()
            }
        }
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _searchResult.value = null
    }
}