package com.zion830.threedollars

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.my.domain.repository.MyRepository
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.ext.toStringDefault
import com.threedollar.common.utils.Constants
import com.threedollar.network.data.store.StoreInfo
import com.threedollar.network.data.user.UserWithDetailApiResponse
import com.threedollar.network.request.PatchPushInformationRequest
import com.threedollar.network.request.PatchUserInfoRequest
import com.zion830.threedollars.datasource.UserDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(private val userDataSource: UserDataSource, private val myRepository: MyRepository) : BaseViewModel() {

    private val _userInfo: MutableLiveData<UserWithDetailApiResponse> = MutableLiveData()

    val userInfo: LiveData<UserWithDetailApiResponse>
        get() = _userInfo

    private val _isAlreadyUsed: MutableLiveData<String> = MutableLiveData()
    val isAlreadyUsed: LiveData<String>
        get() = _isAlreadyUsed

    private val _isNameUpdated = MutableSharedFlow<Unit>()
    val isNameUpdated: SharedFlow<Unit>
        get() = _isNameUpdated

    private val _logoutResult: MutableLiveData<Boolean> = MutableLiveData()
    val logoutResult: LiveData<Boolean>
        get() = _logoutResult

    val userName: MutableLiveData<String> = MutableLiveData("")

    val isNameEmpty: LiveData<Boolean> = userName.map {
        it.isNullOrBlank() || it == userInfo.value?.name
    }

    private val isUpdated: MutableLiveData<Boolean> = MutableLiveData(true)

    fun updateUserInfo() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            myRepository.getUserInfo().collect {
                _userInfo.postValue(it.data!!)
                isUpdated.postValue(true)
            }

        }
    }

    fun updateName() {
        EventTracker.logEvent(Constants.NICKNAME_CHANGE_BTN_CLICKED)

        if (isNameEmpty.value == true) {
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            myRepository.patchUserInfo(PatchUserInfoRequest(userName.value.toStringDefault())).collect {
                if (!it.ok) {
                    _isAlreadyUsed.postValue(it.message.toStringDefault("-"))
                } else {
                    _msgTextId.postValue(R.string.set_name_success)
                    _isAlreadyUsed.postValue("")
                    _isNameUpdated.emit(Unit)
                }
            }
        }
    }

    fun clearName() {
        userName.value = userInfo.value?.name.toStringDefault()
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

    fun patchPushInformation(informationTokenRequest: PatchPushInformationRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            userDataSource.patchPushInformation(informationTokenRequest)
        }
    }


    override fun handleError(t: Throwable) {
        super.handleError(t)
        _msgTextId.postValue(R.string.connection_failed)
    }
}