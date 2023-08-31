package com.zion830.threedollars.ui.popup

import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.datasource.PopupDataSource
import com.zion830.threedollars.datasource.model.v2.AdType
import com.zion830.threedollars.datasource.model.v4.ad.AdResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.ext.toStringDefault
import javax.inject.Inject

@HiltViewModel
class PopupViewModel @Inject constructor(private val popupDataSource: PopupDataSource) :
    BaseViewModel() {

    private val _adResponse: MutableStateFlow<List<AdResponse>> = MutableStateFlow(emptyList())
    val adResponse: MutableStateFlow<List<AdResponse>> = MutableStateFlow(emptyList())

    fun getPopups(position: AdType, size: Int?) {
        viewModelScope.launch(coroutineExceptionHandler) {
            popupDataSource.getPopups(position, size).collect { popupList ->
                _adResponse.value = popupList.body()?.data?.map {
                    it.copy(
                        bgColor = it.bgColor.toStringDefault(),
                        fontColor = it.fontColor.toStringDefault(),
                        linkUrl = it.linkUrl.toStringDefault(),
                        subTitle = it.subTitle.toStringDefault(),
                        title = it.title.toStringDefault()
                    )
                } ?: listOf()
            }
        }
    }
}