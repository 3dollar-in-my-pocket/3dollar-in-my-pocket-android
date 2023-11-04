package com.zion830.threedollars.ui.login

import androidx.lifecycle.viewModelScope
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.base.ResultWrapper
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.LoginRequest
import com.zion830.threedollars.datasource.model.v2.request.PushInformationTokenRequest
import com.zion830.threedollars.datasource.model.v2.response.my.SignUser
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    private val _loginResult: MutableStateFlow<ResultWrapper<SignUser?>?> = MutableStateFlow(null)
    val loginResult: StateFlow<ResultWrapper<SignUser?>?> = _loginResult.asStateFlow()

    private val latestSocialType: MutableStateFlow<LoginType> = MutableStateFlow(LoginType.of(LegacySharedPrefUtils.getLoginType()))

    private val _isNameUpdated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNameUpdated: StateFlow<Boolean> = _isNameUpdated.asStateFlow()

    fun tryLogin(socialType: LoginType, accessToken: String) {
        latestSocialType.value = socialType
        LegacySharedPrefUtils.saveLoginType(socialType)
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val loginResult = userDataSource.login(LoginRequest(socialType.socialName, accessToken))
            _loginResult.value = (safeApiCall(loginResult))
        }
    }

    fun putPushInformationToken(informationRequest: PushInformationTokenRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userDataSource.putPushInformationToken(informationRequest)
        }
    }

}