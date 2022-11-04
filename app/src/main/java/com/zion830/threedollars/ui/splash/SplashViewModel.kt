package com.zion830.threedollars.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.network.RetrofitBuilder
import com.zion830.threedollars.repository.StoreRepository
import com.zion830.threedollars.repository.model.LoginType
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

    private val _refreshResult: MutableLiveData<ResultWrapper<SignUser?>> = MutableLiveData()
    val refreshResult: LiveData<ResultWrapper<SignUser?>> = _refreshResult

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = storeRepository.getCategories()
            if (result.isSuccessful) {
                withContext(Dispatchers.Main) {
                    SharedPrefUtils.saveCategories(result.body()?.data ?: emptyList())
                }
            }

            val bossCategory = storeRepository.getBossCategory()
            if (bossCategory.isSuccessful) {
                withContext(Dispatchers.Main) {
                    SharedPrefUtils.saveTruckCategories(bossCategory.body()?.data ?: emptyList())
                }
            }

            val bossStoreFeedbackTypeResponse = storeRepository.getBossStoreFeedbackType()
            if (bossStoreFeedbackTypeResponse.isSuccessful) {
                withContext(Dispatchers.Main) {
                    SharedPrefUtils.saveFeedbackType(
                        bossStoreFeedbackTypeResponse.body()?.data ?: emptyList()
                    )
                }
            }
        }
    }

    fun tryLogin() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val loginType = SharedPrefUtils.getLoginType() ?: ""
            val token =
                if (loginType == LoginType.KAKAO.socialName) SharedPrefUtils.getKakaoAccessToken() else SharedPrefUtils.getGoogleToken()

            if (token.isNullOrBlank()) {
                _loginResult.postValue(ResultWrapper.GenericError(400))
                return@launch
            }

            val call = newServiceApi.login(LoginRequest(loginType, token))
            val result = safeApiCall(call)
            _loginResult.postValue(result)
        }
    }

    fun refreshGoogleToken(account: GoogleSignInAccount) {
        val token = GoogleAuthUtil.getToken(
            GlobalApplication.getContext(),
            account.account!!,
            "oauth2:https://www.googleapis.com/auth/plus.me"
        )
        SharedPrefUtils.saveGoogleToken(token)
        SharedPrefUtils.saveLoginType(LoginType.GOOGLE)
        tryLogin()
    }

    fun refreshKakaoToken() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val response =
                kakaoLoginApi.refreshToken(SharedPrefUtils.getKakaoRefreshToken().toString())

            if (response.isSuccessful && response.body() != null) {
                SharedPrefUtils.saveLoginType(LoginType.KAKAO)
                SharedPrefUtils.saveKakaoToken(
                    response.body()?.accessToken ?: "",
                    response.body()?.refreshToken ?: ""
                )
                tryLogin()
            } else {
                _loginResult.postValue(ResultWrapper.GenericError(response.code()))
            }
        }
    }
}