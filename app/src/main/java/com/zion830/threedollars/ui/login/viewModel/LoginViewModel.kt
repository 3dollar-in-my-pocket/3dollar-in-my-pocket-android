package com.zion830.threedollars.ui.login.viewModel

import androidx.lifecycle.viewModelScope
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.base.ResultWrapper
import com.threedollar.network.request.PushInformationRequest
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.LoginRequest
import com.zion830.threedollars.datasource.model.v2.response.my.SignUser
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    private val _loginResult: MutableSharedFlow<ResultWrapper<SignUser?>> = MutableSharedFlow()
    val loginResult: SharedFlow<ResultWrapper<SignUser?>> get() = _loginResult

    private val latestSocialType: MutableStateFlow<LoginType> = MutableStateFlow(LoginType.of(LegacySharedPrefUtils.getLoginType()))

    private val _isNameUpdated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNameUpdated: StateFlow<Boolean> = _isNameUpdated.asStateFlow()

    fun tryLogin(socialType: LoginType, accessToken: String) {
        latestSocialType.value = socialType
        LegacySharedPrefUtils.saveLoginType(socialType)
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val loginResult = userDataSource.login(LoginRequest(socialType.socialName, accessToken))
            _loginResult.emit(safeApiCall(loginResult))
        }
    }

    fun putPushInformation(informationRequest: PushInformationRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userDataSource.putPushInformation(informationRequest)
        }
    }

}