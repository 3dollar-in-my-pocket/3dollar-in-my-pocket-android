package com.zion830.threedollars.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.datasource.KakaoLoginDataSource
import com.zion830.threedollars.datasource.PopupDataSource
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.LoginRequest
import com.zion830.threedollars.datasource.model.v2.request.PushInformationTokenRequest
import com.zion830.threedollars.datasource.model.v2.response.my.SignUser
import com.zion830.threedollars.utils.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.ResultWrapper
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val storeDataSource: StoreDataSource,
    private val userDataSource: UserDataSource,
    private val popupDataSource: PopupDataSource,
    private val kakaoLoginDataSource: KakaoLoginDataSource
) : BaseViewModel() {

    private val _loginResult: MutableStateFlow<ResultWrapper<SignUser?>?> = MutableStateFlow(null)
    val loginResult: StateFlow<ResultWrapper<SignUser?>?> = _loginResult.asStateFlow()

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = storeDataSource.getCategories()
            if (result.isSuccessful) {
                withContext(Dispatchers.Main) {
                    SharedPrefUtils.saveCategories(result.body()?.data ?: emptyList())
                }
            }

            val bossCategory = storeDataSource.getBossCategory()
            if (bossCategory.isSuccessful) {
                withContext(Dispatchers.Main) {
                    SharedPrefUtils.saveTruckCategories(bossCategory.body()?.data ?: emptyList())
                }
            }

            val bossStoreFeedbackTypeResponse = storeDataSource.getBossStoreFeedbackType()
            if (bossStoreFeedbackTypeResponse.isSuccessful) {
                withContext(Dispatchers.Main) {
                    SharedPrefUtils.saveFeedbackType(
                        bossStoreFeedbackTypeResponse.body()?.data ?: emptyList()
                    )
                }
            }

            val storeMarkerAdvertisements = popupDataSource.getPopups("STORE_MARKER")
            if(storeMarkerAdvertisements.isSuccessful){
                val advertisements = storeMarkerAdvertisements.body()?.data.orEmpty()
                if(advertisements.isNotEmpty()){
                    GlobalApplication.storeMarker = advertisements.first()
                }
            }
        }
    }

    private fun tryLogin() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val loginType = SharedPrefUtils.getLoginType() ?: ""
            val token =
                if (loginType == LoginType.KAKAO.socialName) SharedPrefUtils.getKakaoAccessToken() else SharedPrefUtils.getGoogleToken()

            if (token.isNullOrBlank()) {
                _loginResult.value = ResultWrapper.GenericError(400)
                return@launch
            }

            val call = userDataSource.login(LoginRequest(loginType, token))
            val result = safeApiCall(call)
            _loginResult.value = result
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
                kakaoLoginDataSource.refreshToken(SharedPrefUtils.getKakaoRefreshToken().toString())

            if (response.isSuccessful && response.body() != null) {
                SharedPrefUtils.saveLoginType(LoginType.KAKAO)
                SharedPrefUtils.saveKakaoToken(
                    response.body()?.accessToken ?: "",
                    response.body()?.refreshToken ?: ""
                )
                tryLogin()
            } else {
                _loginResult.value = ResultWrapper.GenericError(response.code())
            }
        }
    }

    fun putPushInformationToken(informationRequest: PushInformationTokenRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userDataSource.putPushInformationToken(informationRequest)
        }
    }

}