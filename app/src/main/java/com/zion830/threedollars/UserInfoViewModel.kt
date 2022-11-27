package com.zion830.threedollars

import androidx.lifecycle.*
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.request.PushInformationRequest
import com.zion830.threedollars.datasource.model.v2.response.my.MyInfoResponse
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseViewModel
import zion830.com.common.base.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val userDataSource: UserDataSource) :
    BaseViewModel() {

    private val _userInfo: SingleLiveEvent<MyInfoResponse> = SingleLiveEvent()

    val userInfo: LiveData<MyInfoResponse>
        get() = _userInfo

    private val _isAlreadyUsed: SingleLiveEvent<Int> = SingleLiveEvent()
    val isAlreadyUsed: LiveData<Int>
        get() = _isAlreadyUsed

    private val _isNameUpdated: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isNameUpdated: LiveData<Boolean>
        get() = _isNameUpdated

    private val _logoutResult: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val logoutResult: LiveData<Boolean>
        get() = _logoutResult

    val userName= SingleLiveEvent<String>()

    val isNameEmpty: LiveData<Boolean> = Transformations.map(userName) {
        it.isNullOrBlank()
    }

    private val _isExistStoreInfo: SingleLiveEvent<Pair<StoreInfo, Boolean>> = SingleLiveEvent()
    val isExistStoreInfo: LiveData<Pair<StoreInfo, Boolean>> get() = _isExistStoreInfo

    fun updateUserInfo() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _userInfo.value = userDataSource.getMyInfo().body()
        }
    }

    fun updateName() {
        EventTracker.logEvent(Constants.NICKNAME_CHANGE_BTN_CLICKED)

        if (userName.value.isNullOrBlank()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val checkName = userDataSource.checkName(userName.value!!)
            if (checkName.body()?.resultCode?.isNotBlank() == true) {
                _isAlreadyUsed.postValue(R.string.name_empty)
                return@launch
            }

            val result = userDataSource.updateName(userName.value!!)
            if (result.isSuccessful) {
                _msgTextId.postValue(R.string.set_name_success)
                _isNameUpdated.postValue(true)
                updateUserInfo()
            }

            _isAlreadyUsed.postValue(
                when (result.code()) {
                    200 -> -1
                    400 -> R.string.invalidate_name
                    else -> R.string.login_name_already_exist
                }
            )
        }
    }

    fun initNameUpdateInfo() {
        _isNameUpdated.value = false
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
            val response = userDataSource.logout()
            _logoutResult.postValue(response.isSuccessful)
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
}