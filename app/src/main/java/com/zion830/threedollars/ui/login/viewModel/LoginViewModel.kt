package com.zion830.threedollars.ui.login.viewModel

import androidx.lifecycle.viewModelScope
import com.threedollar.domain.login.repository.LoginRepository
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.base.ResultWrapper
import com.threedollar.network.data.auth.LoginRequest
import com.threedollar.network.request.PushInformationRequest
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.ui.login.model.LoginResultModel
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
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.SIGN_IN

    private val _loginResult: MutableSharedFlow<LoginResultModel> = MutableSharedFlow()
    val loginResult: SharedFlow<LoginResultModel> get() = _loginResult

    private val latestSocialType: MutableStateFlow<LoginType> = MutableStateFlow(LoginType.of(LegacySharedPrefUtils.getLoginType()))

    private val _isNameUpdated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNameUpdated: StateFlow<Boolean> = _isNameUpdated.asStateFlow()

    fun tryLogin(socialType: LoginType, accessToken: String) {
        latestSocialType.value = socialType
        LegacySharedPrefUtils.saveLoginType(socialType)
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = loginRepository.login(LoginRequest(socialType.socialName, accessToken))
            val ret = when (val result = safeApiCall(response)) {
                is ResultWrapper.Success -> {
                    LoginResultModel.Success(
                        userId = result.value?.userId ?: 0,
                        token = result.value?.token.orEmpty()
                    )
                }

                is ResultWrapper.GenericError -> {
                    when (result.code) {
                        404 -> {
                            LoginResultModel.RequireSignUp(socialType, accessToken)
                        }

                        503 -> {
                            LoginResultModel.Maintanance
                        }

                        else -> {
                            LoginResultModel.Error
                        }
                    }
                }

                is ResultWrapper.NetworkError -> {
                    LoginResultModel.Error
                }
            }

            _loginResult.emit(ret)
        }
    }

    fun putPushInformation(informationRequest: PushInformationRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            loginRepository.putPushInformation(informationRequest)
        }
    }

    fun sendClickKakaoLogin() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.SIGN_IN_KAKAO
            )
        )
    }

    fun sendClickGoogleLogin() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.SIGN_IN_GOOGLE
            )
        )
    }
}