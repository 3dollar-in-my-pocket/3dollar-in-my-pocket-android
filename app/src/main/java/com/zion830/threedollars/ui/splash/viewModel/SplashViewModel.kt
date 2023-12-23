package com.zion830.threedollars.ui.splash.viewModel

import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.home.domain.repository.HomeRepository
import com.login.domain.repository.LoginRepository
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.base.ResultWrapper
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.common.utils.SharedPrefUtils.Companion.BOSS_FEED_BACK_LIST
import com.zion830.threedollars.Constants.BOSS_STORE
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.KakaoLoginDataSource
import com.zion830.threedollars.datasource.StoreDataSource
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.LoginRequest
import com.zion830.threedollars.datasource.model.v2.request.PushInformationTokenRequest
import com.zion830.threedollars.datasource.model.v2.response.my.SignUser
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPrefUtils: SharedPrefUtils,
    private val loginRepository: LoginRepository,
    private val storeDataSource: StoreDataSource,
    private val userDataSource: UserDataSource,
    private val homeRepository: HomeRepository,
    private val kakaoLoginDataSource: KakaoLoginDataSource,
) : BaseViewModel() {

    private val _loginResult: MutableStateFlow<ResultWrapper<SignUser?>?> = MutableStateFlow(null)
    val loginResult: StateFlow<ResultWrapper<SignUser?>?> = _loginResult.asStateFlow()

    init {
        viewModelScope.launch(coroutineExceptionHandler) {
            storeDataSource.getCategories().collect { categories ->
                if (categories.isSuccessful) {
                    val categoriesModelList = categories.body()?.data ?: emptyList()
                    LegacySharedPrefUtils.saveCategories(categoriesModelList.filter { it.classification.description == "간식류" })
                    LegacySharedPrefUtils.saveTruckCategories(categoriesModelList.filter { it.classification.description == "식사류" })
                } else {
                    _msgTextId.postValue(R.string.connection_failed)
                }
            }
            loginRepository.getFeedbackTypes(targetType = BOSS_STORE).collect {
                if (it.ok) {
                    withContext(Dispatchers.Main) {
                        sharedPrefUtils.saveList(it.data, BOSS_FEED_BACK_LIST)
                    }
                } else {
                    _serverError.emit(it.message)
                }
            }

            homeRepository.getAdvertisements("STORE_MARKER").collect {
                if (it.ok) {
                    val advertisements = it.data.orEmpty()
                    if (advertisements.isNotEmpty()) {
                        GlobalApplication.storeMarker = advertisements.first()
                    }
                }
            }
        }
    }

    private fun tryLogin() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val loginType = LegacySharedPrefUtils.getLoginType() ?: ""
            val token =
                if (loginType == LoginType.KAKAO.socialName) LegacySharedPrefUtils.getKakaoAccessToken() else LegacySharedPrefUtils.getGoogleToken()

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
        if (account.account == null) {
            _msgTextId.postValue(R.string.connection_failed)
            return
        }
        try {
            val token = GoogleAuthUtil.getToken(
                GlobalApplication.getContext(),
                account.account!!,
                "oauth2:https://www.googleapis.com/auth/plus.me",
            )

            LegacySharedPrefUtils.saveGoogleToken(token)
            LegacySharedPrefUtils.saveLoginType(LoginType.GOOGLE)
            tryLogin()
        } catch (e: Exception) {
            _msgTextId.postValue(R.string.connection_failed)
        }
    }

    fun refreshKakaoToken() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val response =
                kakaoLoginDataSource.refreshToken(LegacySharedPrefUtils.getKakaoRefreshToken().toString())

            if (response.isSuccessful && response.body() != null) {
                LegacySharedPrefUtils.saveLoginType(LoginType.KAKAO)
                LegacySharedPrefUtils.saveKakaoToken(
                    response.body()?.accessToken ?: "",
                    response.body()?.refreshToken ?: "",
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
