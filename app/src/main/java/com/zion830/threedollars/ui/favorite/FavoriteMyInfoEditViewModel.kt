package com.zion830.threedollars.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v4.favorite.request.FavoriteInfoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteMyInfoEditViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {
    private val _isSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    fun updateFavoriteInfo(favoriteInfoRequest: FavoriteInfoRequest) {
        viewModelScope.launch {
            val response = userDataSource.updateFavoriteInfo(favoriteInfoRequest)
            if (response.isSuccessful) {
                _isSuccess.value = true
            } else {
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }
}