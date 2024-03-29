package com.zion830.threedollars

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.utils.Constants
import com.threedollar.network.request.PushInformationRequest
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.response.my.MyInfoResponse
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    private val _userInfo: MutableLiveData<MyInfoResponse> = MutableLiveData()

    val userInfo: LiveData<MyInfoResponse>
        get() = _userInfo

    private val _isAlreadyUsed: MutableLiveData<Int> = MutableLiveData()
    val isAlreadyUsed: LiveData<Int>
        get() = _isAlreadyUsed

    private val _isNameUpdated: MutableLiveData<Boolean> = MutableLiveData()
    val isNameUpdated: LiveData<Boolean>
        get() = _isNameUpdated

    private val _logoutResult: MutableLiveData<Boolean> = MutableLiveData()
    val logoutResult: LiveData<Boolean>
        get() = _logoutResult

    val userName: MutableLiveData<String> = MutableLiveData("")

    val isNameEmpty: LiveData<Boolean> = Transformations.map(userName) {
        it.isNullOrBlank()
    }

    private val isUpdated: MutableLiveData<Boolean> = MutableLiveData(true)

    private val _isExistStoreInfo: MutableLiveData<Pair<StoreInfo, Boolean>> = MutableLiveData()
    val isExistStoreInfo: LiveData<Pair<StoreInfo, Boolean>> get() = _isExistStoreInfo

    fun updateUserInfo() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _userInfo.postValue(userDataSource.getMyInfo().body())
            isUpdated.postValue(true)
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