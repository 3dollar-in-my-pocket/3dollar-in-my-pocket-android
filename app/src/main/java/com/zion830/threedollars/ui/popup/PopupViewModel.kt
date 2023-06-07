package com.zion830.threedollars.ui.popup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.datasource.PopupDataSource
import com.zion830.threedollars.datasource.model.v2.response.Popups
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.ext.toStringDefault
import javax.inject.Inject

@HiltViewModel
class PopupViewModel @Inject constructor(private val popupDataSource: PopupDataSource) :
    BaseViewModel() {

    val popups: MutableLiveData<List<Popups>> = MutableLiveData()

    val popupInitialImage = popups.map { it.firstOrNull()?.imageUrl }

    fun getPopups(position: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val popupList = popupDataSource.getPopups(position = position).body()?.data?.map {
                it.copy(
                    bgColor = it.bgColor.toStringDefault(),
                    fontColor = it.fontColor.toStringDefault(),
                    linkUrl = it.linkUrl.toStringDefault(),
                    subTitle = it.subTitle.toStringDefault(),
                    title = it.title.toStringDefault()
                )
            } ?: listOf()
            popups.postValue(popupList)
        }
    }
}