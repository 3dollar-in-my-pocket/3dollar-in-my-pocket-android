package com.zion830.threedollars.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.datasource.PopupDataSource
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.response.Popups
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MarkerClickViewModel @Inject constructor(private val userDataSource: UserDataSource,private val popupDataSource: PopupDataSource) : BaseViewModel() {

    private val _popupsResponse : MutableLiveData<Popups> = MutableLiveData()
    val popupsResponse : LiveData<Popups> get() = _popupsResponse

    fun eventClick(targetType: String, targetId: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            userDataSource.eventClick(targetType, targetId)
        }
    }

    fun getPopups(){
        viewModelScope.launch(coroutineExceptionHandler) {
            val popups = popupDataSource.getPopups("STORE_MARKER_POPUP")
            if (popups.isSuccessful) {
                _popupsResponse.value = popups.body()?.data?.first()
            }
        }
    }
}