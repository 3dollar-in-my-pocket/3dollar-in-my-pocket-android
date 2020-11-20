package com.zion830.threedollars.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.repository.UserRepository
import com.zion830.threedollars.repository.model.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel

class LoginViewModel : BaseViewModel() {

    val userName: MutableLiveData<String> = MutableLiveData("")

    private val userRepository = UserRepository()

    private val _loginResult: MutableLiveData<LoginResponse?> = MutableLiveData()
    val loginResult: LiveData<LoginResponse?>
        get() = _loginResult

    private val _isAvailable: MutableLiveData<Boolean> = MutableLiveData(true)
    val isAvailable: LiveData<Boolean>
        get() = _isAvailable

    val isNameEmpty: LiveData<Boolean> = Transformations.map(userName) {
        it.isNullOrBlank()
    }

    val isFinishing: LiveData<Boolean> = Transformations.map(_loginResult) {
        it != null
    }

    fun tryLogin() {
        if (userName.value.isNullOrBlank()) {
            Log.w(javaClass.name, "userName이 없습니다.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val loginResult = userRepository.tryLogin(userName.value!!)
            _loginResult.postValue(loginResult)
        }
    }
}