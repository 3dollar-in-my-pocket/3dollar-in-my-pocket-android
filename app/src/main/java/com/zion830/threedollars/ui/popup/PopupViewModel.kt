package com.zion830.threedollars.ui.popup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.repository.PopupRepository
import com.zion830.threedollars.repository.model.v2.response.Popups
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class PopupViewModel : BaseViewModel() {

    private val popupRepository = PopupRepository()

    val popups: MutableLiveData<List<Popups>> = MutableLiveData()

    val popupInitialImage = popups.map { it.firstOrNull()?.imageUrl }

    init {
        getPopups()
    }

    private fun getPopups() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            popups.postValue(popupRepository.getPopups().body()?.data)
        }
    }
}