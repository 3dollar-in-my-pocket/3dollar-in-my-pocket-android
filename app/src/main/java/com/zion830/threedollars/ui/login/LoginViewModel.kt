package com.zion830.threedollars.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.*
import com.zion830.threedollars.datasource.model.v2.response.my.SignUser
import com.zion830.threedollars.utils.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.ResultWrapper
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    private val _loginResult: MutableStateFlow<ResultWrapper<SignUser?>?> = MutableStateFlow(null)
    val loginResult: StateFlow<ResultWrapper<SignUser?>?> = _loginResult.asStateFlow()

    private val latestSocialType: MutableStateFlow<LoginType> = MutableStateFlow(LoginType.of(SharedPrefUtils.getLoginType()))

    private val _isNameUpdated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNameUpdated: StateFlow<Boolean> = _isNameUpdated.asStateFlow()

    fun tryLogin(socialType: LoginType, accessToken: String) {
        latestSocialType.value = socialType
        SharedPrefUtils.saveLoginType(socialType)
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