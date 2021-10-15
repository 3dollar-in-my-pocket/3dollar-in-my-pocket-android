package com.zion830.threedollars.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.UserRepository
import com.zion830.threedollars.repository.model.v2.request.LoginRequest
import com.zion830.threedollars.repository.model.v2.request.SignUpRequest
import com.zion830.threedollars.repository.model.v2.response.my.SignUser
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.ResultWrapper
import java.net.ConnectException

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

    private val _isNameUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isNameUpdated: LiveData<Boolean>
        get() = _isNameUpdated

    private val _isAlreadyUsed: MutableLiveData<Boolean> = MutableLiveData()
    val isAlreadyUsed: LiveData<Boolean>
        get() = _isAlreadyUsed

    fun tryLogin(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val loginResult = userRepository.login(LoginRequest(token = accessToken))
            _loginResult.postValue(safeApiCall(loginResult))
        }
    }

    fun updateName() {
        if (userName.value.isNullOrBlank()) {
            _msgTextId.value = R.string.name_empty
            return
        }

        val updateNameHandler = CoroutineExceptionHandler { _, t ->
            when (t) {
                is HttpException -> {
                    _isAvailable.postValue(false)
                }
                is ConnectException -> {
                    _msgTextId.postValue(R.string.set_name_failed)
                }
                else -> {
                    _isNameUpdated.postValue(true)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO + updateNameHandler) {
            val request = SignUpRequest(name = userName.value!!, token = SharedPrefUtils.getKakaoAccessToken().toString())
            val signUpResult = userRepository.signUp(request)
            if (signUpResult.isSuccessful) {
                SharedPrefUtils.saveAccessToken(signUpResult.body()?.data?.token ?: "")
                _isNameUpdated.postValue(true)
                _msgTextId.postValue(R.string.success_signup)
            } else {
                if (signUpResult.code() == 409) {
                    _isAlreadyUsed.postValue(true)
                } else {
                    _isAlreadyUsed.postValue(false)
                    _msgTextId.postValue(R.string.connection_failed)
                }
            }
        }
    }
}