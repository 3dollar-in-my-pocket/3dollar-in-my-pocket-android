package com.zion830.threedollars.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.request.FavoriteInfoRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@HiltViewModel
class FavoriteMyInfoEditViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {
    override val screenName: ScreenName = ScreenName.EDIT_BOOKMARK_LIST
    private val _isSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    fun updateFavoriteInfo(favoriteInfoRequest: FavoriteInfoRequest) {
        viewModelScope.launch {
            val response = userDataSource.updateFavoriteInfo(favoriteInfoRequest)
            if (response.isSuccessful) {
                _isSuccess.value = true
            } else {
                _msgTextId.postValue(CommonR.string.connection_failed)
            }
        }
    }
}