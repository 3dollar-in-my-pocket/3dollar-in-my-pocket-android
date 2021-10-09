package com.zion830.threedollars.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.model.v2.request.LoginRequest
import com.zion830.threedollars.repository.model.v2.response.my.SignUser
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.ResultWrapper

class SplashViewModel : BaseViewModel() {
    private val newServiceApi = RetrofitBuilder.newServiceApi

    private val _loginResult: MutableLiveData<ResultWrapper<SignUser?>> = MutableLiveData()
    val loginResult: LiveData<ResultWrapper<SignUser?>> = _loginResult

    fun tryLogin(token: String) {
        viewModelScope.launch {
            val call = newServiceApi.login(LoginRequest(token = token))
            val result = safeApiCall(call)
            _loginResult.postValue(result)
        }
    }
}