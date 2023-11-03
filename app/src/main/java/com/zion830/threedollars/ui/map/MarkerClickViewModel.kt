package com.zion830.threedollars.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.repository.HomeRepository
import com.threedollar.common.base.BaseViewModel
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

    private val _popupsResponse: MutableStateFlow<AdvertisementModel?> = MutableStateFlow(null)
    val popupsResponse: StateFlow<AdvertisementModel?> get() = _popupsResponse

    fun eventClick(targetType: String, targetId: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            userDataSource.eventClick(targetType, targetId)
        }
    }

    fun getPopups() {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.getAdvertisements("STORE_MARKER_POPUP").collect {
                if (it.ok) {
                    _popupsResponse.value = it.data?.first()
                }
            }
        }
    }
}