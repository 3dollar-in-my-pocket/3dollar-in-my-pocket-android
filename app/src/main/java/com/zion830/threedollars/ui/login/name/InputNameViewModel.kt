package com.zion830.threedollars.ui.login.name

import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.SignUpRequest
import com.zion830.threedollars.utils.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class InputNameViewModel @Inject constructor(private val userDataSource: UserDataSource) :
    BaseViewModel() {

    val userName: MutableStateFlow<String> = MutableStateFlow("")
    private val latestSocialType: MutableStateFlow<LoginType> = MutableStateFlow(LoginType.of(SharedPrefUtils.getLoginType()))

    private val _eventsFlow = MutableSharedFlow<Event>()
    val eventsFlow = _eventsFlow.asSharedFlow()
    fun trySignUp() {
        if (userName.value.isBlank()) {
            _msgTextId.value = R.string.name_empty
            return
        }

        val token = if (latestSocialType.value.socialName == LoginType.KAKAO.socialName) {
            SharedPrefUtils.getKakaoAccessToken()
        } else {
            SharedPrefUtils.getGoogleToken()
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val request = SignUpRequest(userName.value, latestSocialType.value.socialName, token.toString())
            val signUpResult = userDataSource.signUp(request)
            if (signUpResult.isSuccessful) {
                SharedPrefUtils.saveAccessToken(signUpResult.body()?.data?.token ?: "")
                _eventsFlow.emit(Event.NameUpdate)
            } else {
                when (signUpResult.code()) {
                    409 -> _eventsFlow.emit(Event.NameAlready)
                    400 -> _eventsFlow.emit(Event.NameError)
                    else -> _msgTextId.postValue(R.string.connection_failed)
                }
            }
        }
    }

    sealed class Event {
        object NameUpdate : Event()
        object NameAlready : Event()
        object NameError : Event()
    }
}