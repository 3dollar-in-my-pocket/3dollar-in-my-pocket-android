package com.zion830.threedollars.ui.map

import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.datasource.UserDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class NaverMapViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    fun eventClick(targetType: String, targetId: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            userDataSource.eventClick(targetType, targetId)
        }
    }
}