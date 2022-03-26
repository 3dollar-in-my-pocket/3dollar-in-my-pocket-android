package com.zion830.threedollars

import androidx.lifecycle.*
import com.zion830.threedollars.repository.UserRepository
import com.zion830.threedollars.repository.model.v2.response.my.MyInfoResponse
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseViewModel

class UserInfoViewModel : BaseViewModel() {

    private val userRepository = UserRepository()

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
            _userInfo.postValue(userRepository.getMyInfo().body())
            isUpdated.postValue(true)
        }
    }

    fun updateName() {
        EventTracker.logEvent(Constants.NICKNAME_CHANGE_BTN_CLICKED)

        if (userName.value.isNullOrBlank()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val checkName = userRepository.checkName(userName.value!!)
            if (checkName.body()?.resultCode?.isNotBlank() == true) {
                _isAlreadyUsed.postValue(R.string.name_empty)
                return@launch
            }

            val result = userRepository.updateName(userName.value!!)
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
            val result = userRepository.signout()

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
            val response = userRepository.logout()
            _logoutResult.postValue(response.isSuccessful)
        }
    }

    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(R.string.connection_failed)
    }
}