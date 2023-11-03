package com.zion830.threedollars.ui.login.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.SignUpRequest
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputNameViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    val userName: MutableLiveData<String> = MutableLiveData("")
    private val latestSocialType: MutableLiveData<LoginType> =
        MutableLiveData(LoginType.of(LegacySharedPrefUtils.getLoginType()))
    val isNameEmpty: LiveData<Boolean> = Transformations.map(userName) {
        it.isNullOrBlank()
    }
    private val _isAlreadyUsed: MutableLiveData<Int> = MutableLiveData()
    val isAlreadyUsed: LiveData<Int>
        get() = _isAlreadyUsed
    private val _isNameUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isNameUpdated: LiveData<Boolean>
        get() = _isNameUpdated
    private val _isAvailable: MutableLiveData<Boolean> = MutableLiveData(true)
    val isAvailable: LiveData<Boolean>
        get() = _isAvailable

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
            LegacySharedPrefUtils.getKakaoAccessToken()
        } else {
            LegacySharedPrefUtils.getGoogleToken()
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val request = SignUpRequest(
                userName.value!!,
                latestSocialType.value!!.socialName,
                token.toString()
            )
            val signUpResult = userDataSource.signUp(request)
            if (signUpResult.isSuccessful) {
                LegacySharedPrefUtils.saveAccessToken(signUpResult.body()?.data?.token ?: "")
                _isNameUpdated.postValue(true)
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