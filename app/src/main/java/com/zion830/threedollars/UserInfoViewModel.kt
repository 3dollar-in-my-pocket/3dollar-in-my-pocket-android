package com.zion830.threedollars

import androidx.lifecycle.*
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.request.PushInformationRequest
import com.zion830.threedollars.datasource.model.v2.response.my.MyInfoResponse
import com.zion830.threedollars.ui.login.name.InputNameViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val userDataSource: UserDataSource) :
    BaseViewModel() {

    private val _userInfo: MutableStateFlow<MyInfoResponse?> = MutableStateFlow(MyInfoResponse())

    val userInfo = _userInfo.asStateFlow()

    val userName: MutableStateFlow<String> = MutableStateFlow("")

    private val _eventsFlow = MutableSharedFlow<Event>()
    val eventsFlow = _eventsFlow.asSharedFlow()

    fun updateUserInfo() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _userInfo.value = userDataSource.getMyInfo().body()
        }
    }

    fun updateName() {
        EventTracker.logEvent(Constants.NICKNAME_CHANGE_BTN_CLICKED)

        if (userName.value.isBlank()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val checkName = userDataSource.checkName(userName.value)
            if (checkName.body()?.resultCode?.isNotBlank() == true) {
                return@launch
            }

            val result = userDataSource.updateName(userName.value)
            if (result.isSuccessful) {
                _msgTextId.postValue(R.string.set_name_success)
                _eventsFlow.emit(Event.NameUpdate)
                updateUserInfo()
            }
            _eventsFlow.emit(
                when (result.code()) {
                    400 -> Event.NameError
                    else -> Event.NameAlready
                }
            )
        }
    }

    fun clearName() {
        userName.value = ""
    }

    fun deleteUser(onSuccess: () -> Unit) {
        showLoading()
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val result = userDataSource.signOut()

            withContext(Dispatchers.Main) {
                hideLoading()

                if (result.isSuccessful) {
                    onSuccess()
                } else {
                    _msgTextId.postValue(R.string.failed_delete_account)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val logout = userDataSource.logout()
            if (logout.isSuccessful) {
                _eventsFlow.emit(Event.Logout)
            } else {
                _eventsFlow.emit(Event.LogoutError)
            }
        }
    }

    fun postPushInformation(informationRequest: PushInformationRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userDataSource.postPushInformation(informationRequest)
        }
    }

    fun deletePushInformation() {
        viewModelScope.launch(coroutineExceptionHandler) {
            userDataSource.deletePushInformation()
        }
    }


    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(R.string.connection_failed)
    }

    sealed class Event {
        object Logout : Event()
        object LogoutError : Event()
        object NameAlready : Event()
        object NameError : Event()

        object NameUpdate : Event()
    }
}