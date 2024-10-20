package com.zion830.threedollars.ui.popup

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.home.domain.data.advertisement.AdvertisementModelV2
import com.home.domain.repository.HomeRepository
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.AdvertisementsPosition
import com.zion830.threedollars.GlobalApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopupViewModel @Inject constructor(private val homeRepository: HomeRepository) : BaseViewModel() {

    private val _popups: MutableStateFlow<List<AdvertisementModelV2>> = MutableStateFlow(listOf())
    val popups: StateFlow<List<AdvertisementModelV2>> get() = _popups

    fun getPopups(
        position: AdvertisementsPosition,
        latLng: LatLng
    ) {
        viewModelScope.launch {
            Log.e("getPopups", "latitude : ${latLng.latitude} ++++++++ longitude : ${latLng.longitude}")
            homeRepository.getAdvertisements(
                position = position,
                deviceLatitude = latLng.latitude,
                deviceLongitude = latLng.longitude
            ).collect {
                if (it.ok) {
                    _popups.value = it.data ?: listOf()
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }
}