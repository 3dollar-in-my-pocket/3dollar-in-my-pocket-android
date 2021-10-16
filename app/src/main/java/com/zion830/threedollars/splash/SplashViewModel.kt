package com.zion830.threedollars.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.request.KakaoRefreshTokenRequest
import com.zion830.threedollars.repository.model.v2.request.LoginRequest
import com.zion830.threedollars.repository.model.v2.response.my.SignUser
import com.zion830.threedollars.utils.SharedPrefUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.ResultWrapper

class SplashViewModel : BaseViewModel() {
    private val newServiceApi = RetrofitBuilder.newServiceApi
    private val kakaoLoginApi = RetrofitBuilder.kakaoLoginApi
    private val storeRepository = StoreRepository()

    private val _loginResult: MutableLiveData<ResultWrapper<SignUser?>> = MutableLiveData()
    val loginResult: LiveData<ResultWrapper<SignUser?>> = _loginResult

    init {
        viewModelScope.launch {
            val result = storeRepository.getCategories()
            if (result.isSuccessful) {
                withContext(Dispatchers.Main) {
                    SharedPrefUtils.saveCategories(result.body()?.data ?: emptyList())
                }
            }
        }
    }

    fun tryLogin(token: String) {
        viewModelScope.launch {
            val call = newServiceApi.login(LoginRequest(token = token))
            val result = safeApiCall(call)
            _loginResult.postValue(result)
        }
    }

    fun refreshToken() {
        viewModelScope.launch {
            val request = kakaoLoginApi.refreshToken(KakaoRefreshTokenRequest(refreshToken = SharedPrefUtils.getKakaoRefreshToken().toString()))

            if (request.isSuccessful && request.body() != null) {
                SharedPrefUtils.saveKakaoToken(request.body()!!.accessToken, request.body()!!.refreshToken)
                tryLogin(request.body()!!.accessToken)
            } else {
                _loginResult.postValue(ResultWrapper.GenericError(401))
            }
        }
    }
}