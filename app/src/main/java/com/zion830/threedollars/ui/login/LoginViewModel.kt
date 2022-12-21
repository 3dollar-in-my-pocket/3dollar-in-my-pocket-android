package com.zion830.threedollars.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.*
import com.zion830.threedollars.datasource.model.v2.response.my.SignUser
import com.zion830.threedollars.utils.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.ResultWrapper
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userDataSource: UserDataSource) :
    BaseViewModel() {

    val userName: MutableLiveData<String> = MutableLiveData("")

    private val _loginResult: MutableLiveData<ResultWrapper<SignUser?>> = MutableLiveData()
    val loginResult: MutableLiveData<ResultWrapper<SignUser?>>
        get() = _loginResult

    private val _isAvailable: MutableLiveData<Boolean> = MutableLiveData(true)
    val isAvailable: LiveData<Boolean>
        get() = _isAvailable

    private val _isPostDevice: MutableLiveData<Boolean> = MutableLiveData()
    val isPostDevice: LiveData<Boolean>
        get() = _isPostDevice

    private val latestSocialType: MutableLiveData<LoginType> =
        MutableLiveData(LoginType.of(SharedPrefUtils.getLoginType()))

    private val _isNameUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isNameUpdated: LiveData<Boolean>
        get() = _isNameUpdated

    private val _isAlreadyUsed: MutableLiveData<Int> = MutableLiveData()
    val isAlreadyUsed: LiveData<Int>
        get() = _isAlreadyUsed

    fun tryLogin(socialType: LoginType, accessToken: String) {
        latestSocialType.postValue(socialType)
        SharedPrefUtils.saveLoginType(socialType)
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val loginResult = userDataSource.login(LoginRequest(socialType.socialName, accessToken))
            _loginResult.postValue(safeApiCall(loginResult))
        }
    }

    fun trySignUp(informationRequest: PushInformationRequest, isMarketing: Boolean) {
        if (userName.value.isNullOrBlank()) {
            _msgTextId.value = R.string.name_empty
            return
        }

        if (latestSocialType.value == null) {
            _msgTextId.postValue(R.string.connection_failed)
            return
        }

        val token = if (latestSocialType.value!!.socialName == LoginType.KAKAO.socialName) {
            SharedPrefUtils.getKakaoAccessToken()
        } else {
            SharedPrefUtils.getGoogleToken()
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val request = SignUpRequest(
                userName.value!!,
                latestSocialType.value!!.socialName,
                token.toString()
            )
            val signUpResult = userDataSource.signUp(request)
            if (signUpResult.isSuccessful) {
                SharedPrefUtils.saveAccessToken(signUpResult.body()?.data?.token ?: "")
                postPushInformation(informationRequest, isMarketing)

            } else {
                when (signUpResult.code()) {
                    409 -> _isAlreadyUsed.postValue(R.string.login_name_already_exist)
                    400 -> _isAlreadyUsed.postValue(R.string.invalidate_name)
                    else -> _msgTextId.postValue(R.string.connection_failed)
                }
            }
        }
    }

    fun postPushInformation(informationRequest: PushInformationRequest, isMarketing: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (userDataSource.postPushInformation(informationRequest).isSuccessful) {
                putMarketingConsent(MarketingConsentRequest(if (isMarketing) "APPROVE" else "DENY"))
            }
        }
    }

    fun putPushInformationToken(informationRequest: PushInformationTokenRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userDataSource.putPushInformationToken(informationRequest)
        }
    }

    private fun putMarketingConsent(marketingConsentRequest: MarketingConsentRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (userDataSource.putMarketingConsent(marketingConsentRequest).isSuccessful) {
                _isNameUpdated.postValue(true)
                _msgTextId.postValue(R.string.success_signup)
                _isAlreadyUsed.postValue(-1)
            }
        }
    }
}