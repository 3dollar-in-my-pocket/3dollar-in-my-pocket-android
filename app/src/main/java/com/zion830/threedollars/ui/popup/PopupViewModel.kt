package com.zion830.threedollars.ui.popup

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.datasource.PopupDataSource
import com.zion830.threedollars.datasource.model.v2.response.Popups
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class PopupViewModel @Inject constructor(private val popupDataSource: PopupDataSource) :
    BaseViewModel() {

    private val _popups = SingleLiveEvent<List<Popups>>()
    val popups: LiveData<List<Popups>> get() = _popups

    val popupInitialImage = popups.map { it.firstOrNull()?.imageUrl }

    fun getPopups(position: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            popupDataSource.getPopups(position = position).body()?.data?.apply {
                _popups.postValue(this)
            }
        }
    }
}