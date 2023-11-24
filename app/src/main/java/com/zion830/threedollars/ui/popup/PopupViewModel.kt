package com.zion830.threedollars.ui.popup

import androidx.lifecycle.viewModelScope
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.repository.HomeRepository
import com.threedollar.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopupViewModel @Inject constructor(private val homeRepository: HomeRepository) : BaseViewModel() {

    private val _popups: MutableStateFlow<List<AdvertisementModel>> = MutableStateFlow(listOf())
    val popups: StateFlow<List<AdvertisementModel>> get() = _popups

    fun getPopups(position: String) {
        viewModelScope.launch {
            homeRepository.getAdvertisements(position).collect{
                if(it.ok){
                    _popups.value = it.data ?: listOf()
                }
                else{
                    _serverError.emit(it.message)
                }
            }
        }
    }
}