package com.zion830.threedollars.ui.webview

import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.util.AppUserAgent
import com.zion830.threedollars.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(
    private val sharedPrefUtils: SharedPrefUtils,
) : BaseViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _currentUrl = MutableStateFlow("")
    val currentUrl: StateFlow<String> = _currentUrl.asStateFlow()

    fun updateTitle(title: String) {
        _title.value = title
    }

    fun updateCurrentUrl(url: String) {
        _currentUrl.value = url
    }

    fun browserRequest(url: String): InAppBrowserRequest = InAppBrowserHeaders.build(
        url = url,
        token = sharedPrefUtils.getAccessToken(),
        userAgent = AppUserAgent.value,
        isDebug = BuildConfig.DEBUG,
    )
}
