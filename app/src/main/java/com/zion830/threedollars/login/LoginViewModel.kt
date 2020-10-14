package com.zion830.threedollars.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zion830.threedollars.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseViewModel

class LoginViewModel : BaseViewModel() {

    val userName: MutableLiveData<String> = MutableLiveData("")

    private val _isAvailable: MutableLiveData<Boolean> = MutableLiveData()
    val isAvailable: LiveData<Boolean>
        get() = _isAvailable

    fun tryLogin() {
        if (userName.value.isNullOrBlank()) {
            Log.w(javaClass.name, "userName이 없습니다.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val loginResult = UserRepository().tryLogin(userName.value!!)

            withContext(Dispatchers.Main) {
                Log.d("LoginViewModel", loginResult.toString())
            }
        }
    }
}