package com.zion830.threedollars.ui.map.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.home.domain.data.advertisement.AdvertisementModelV2
import com.home.domain.repository.HomeRepository
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.AdvertisementsPosition
import com.zion830.threedollars.datasource.UserDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarkerClickViewModel @Inject constructor(private val userDataSource: UserDataSource, private val homeRepository: HomeRepository) :
    BaseViewModel() {

    private val _popupsResponse: MutableStateFlow<AdvertisementModelV2?> = MutableStateFlow(null)
    val popupsResponse: StateFlow<AdvertisementModelV2?> get() = _popupsResponse

    fun eventClick(targetType: String, targetId: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            userDataSource.eventClick(targetType, targetId)
        }
    }

    fun getPopups(latLng: LatLng) {
        viewModelScope.launch(coroutineExceptionHandler) {
            Log.e("getPopups", "latitude : ${latLng.latitude} ++++++++ longitude : ${latLng.longitude}")
            homeRepository.getAdvertisements(
                position = AdvertisementsPosition.STORE_MARKER_POPUP,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect {
                if (it.ok) {
                    _popupsResponse.value = it.data?.firstOrNull()
                }
            }
        }
    }
}