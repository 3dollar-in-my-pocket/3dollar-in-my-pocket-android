package com.zion830.threedollars.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.UserRepository
import com.zion830.threedollars.repository.model.LoginType
import com.zion830.threedollars.repository.model.v2.request.LoginRequest
import com.zion830.threedollars.repository.model.v2.request.SignUpRequest
import com.zion830.threedollars.repository.model.v2.response.my.SignUser
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.ResultWrapper

class LoginViewModel : BaseViewModel() {

    val userName: MutableLiveData<String> = MutableLiveData("")

    private val userRepository = UserRepository()

    private val _loginResult: MutableLiveData<ResultWrapper<SignUser?>> = MutableLiveData()
    val loginResult: MutableLiveData<ResultWrapper<SignUser?>>
        get() = _loginResult

    private val _isAvailable: MutableLiveData<Boolean> = MutableLiveData(true)
    val isAvailable: LiveData<Boolean>
        get() = _isAvailable

    val isNameEmpty: LiveData<Boolean> = Transformations.map(userName) {
        it.isNullOrBlank()
    }

    private val latestSocialType: MutableLiveData<LoginType> = MutableLiveData(LoginType.of(SharedPrefUtils.getLoginType()))

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
            val loginResult = userRepository.login(LoginRequest(socialType.socialName, accessToken))
            _loginResult.postValue(safeApiCall(loginResult))
        }
    }

    fun trySignUp() {
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
            val request = SignUpRequest(userName.value!!, latestSocialType.value!!.socialName, token.toString())
            val signUpResult = userRepository.signUp(request)
            if (signUpResult.isSuccessful) {
                SharedPrefUtils.saveAccessToken(signUpResult.body()?.data?.token ?: "")
                _isNameUpdated.postValue(true)
                _msgTextId.postValue(R.string.success_signup)
                _isAlreadyUsed.postValue(-1)
            } else {
                when (signUpResult.code()) {
                    409 -> _isAlreadyUsed.postValue(R.string.login_name_already_exist)
                    400 -> _isAlreadyUsed.postValue(R.string.invalidate_name)
                    else -> _msgTextId.postValue(R.string.connection_failed)
                }
            }
        }
    }
}