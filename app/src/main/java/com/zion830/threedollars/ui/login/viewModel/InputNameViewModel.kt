package com.zion830.threedollars.ui.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.login.domain.repository.LoginRepository
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.base.ResultWrapper
import com.threedollar.network.data.auth.SignUpRequest
import com.threedollar.network.data.auth.SignUser
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@HiltViewModel
class InputNameViewModel @Inject constructor(private val loginRepository: LoginRepository, private val userDataSource: UserDataSource) :
    BaseViewModel() {

    val userName: MutableLiveData<String> = MutableLiveData("")
    private val latestSocialType: MutableLiveData<LoginType> =
        MutableLiveData(LoginType.of(LegacySharedPrefUtils.getLoginType()))
    val isNameEmpty: LiveData<Boolean> = userName.map {
        it.isNullOrBlank()
    }
    private val _isAlreadyUsed: MutableLiveData<Int> = MutableLiveData()
    val isAlreadyUsed: LiveData<Int>
        get() = _isAlreadyUsed
    private val _isAvailable: MutableLiveData<Boolean> = MutableLiveData(true)
    val isAvailable: LiveData<Boolean>
        get() = _isAvailable

    private val _isMarketing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isMarketing: StateFlow<Boolean> get() = _isMarketing

    private val _signUpResult: MutableSharedFlow<ResultWrapper<SignUser?>> = MutableSharedFlow()
    val signUpResult: SharedFlow<ResultWrapper<SignUser?>> get() = _signUpResult

    fun trySignUp(token: String) {
        if (userName.value.isNullOrBlank()) {
            _msgTextId.value = CommonR.string.name_empty
            return
        }

        if (latestSocialType.value == null) {
            _msgTextId.postValue(CommonR.string.connection_failed)
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val request = SignUpRequest(
                userName.value!!,
                latestSocialType.value!!.socialName,
                token
            )
            val response = loginRepository.signUp(request)
            val result = safeApiCall(response)
            _signUpResult.emit(result)
        }
    }

    fun putPushInformation(pushToken: String, isMarketing: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            loginRepository.putPushInformation(pushToken).collect {
                if (it.ok) {
                    putMarketingConsent((if (isMarketing) "APPROVE" else "DENY"))
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }

    private fun putMarketingConsent(marketingConsent: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            loginRepository.putMarketingConsent(marketingConsent).collect {
                if (it.ok) {
                    _isMarketing.value = true
                } else {
                    _serverError.emit(it.message)
                }
            }
        }
    }
}