package com.zion830.threedollars.ui.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.threedollar.domain.my.model.UserInfoUpdateModel
import com.threedollar.domain.my.repository.MyRepository
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.response.my.Medal
import com.zion830.threedollars.datasource.model.v2.response.my.UserActivityData
import com.zion830.threedollars.ui.mypage.adapter.MyMedal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@HiltViewModel
class MyMealViewModel @Inject constructor(private val userDataSource: UserDataSource, private val myRepository: MyRepository) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.MY_MEDAL

    private val _userActivity: MutableLiveData<UserActivityData?> = MutableLiveData()
    val userActivity: LiveData<UserActivityData?> = _userActivity

    private val _myMedals: MutableLiveData<List<MyMedal>> = MutableLiveData()
    val myMedals: MutableLiveData<List<MyMedal>> = _myMedals

    val selectedMedal = userActivity.map { it?.medal }

    private val _allMedals: MutableLiveData<List<Medal>> = MutableLiveData()
    val allMedals: LiveData<List<Medal>> = _allMedals

    init {
        requestUserActivity()
        requestMyMedals()
    }

    private fun initAllMedals() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = userDataSource.getMedals()
            if (response.isSuccessful) {
                _allMedals.postValue(response.body()?.data ?: emptyList())
            }
        }
    }

    fun requestUserActivity() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = userDataSource.getUserActivity()
            if (response.isSuccessful) {
                val userActivity = response.body()?.data
                _userActivity.postValue(userActivity)
            } else {
                _msgTextId.postValue(CommonR.string.connection_failed)
            }
        }
    }

    fun requestMyMedals() {
        if (_allMedals.value.isNullOrEmpty()) {
            initAllMedals()
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = userDataSource.getMyMedals()
            _myMedals.postValue(_allMedals.value?.map { medal ->
                val hasMedal = (response.body()?.data
                    ?: emptyList()).find { it.medalId == medal.medalId } != null
                val isSelected = medal.medalId == _userActivity.value?.medal?.medalId
                MyMedal(medal, isSelected, hasMedal)
            } ?: emptyList())
        }
    }

    fun updateMedal(medalId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            myRepository.patchUserInfo(UserInfoUpdateModel(representativeMedalId = medalId)).collect {
                if (it.ok) {
                    requestUserActivity()
                } else {
                    _msgTextId.postValue(CommonR.string.error_change_medal)
                    _msgTextId.postValue(-1)
                }
            }
        }
    }

    fun sendClickMedal(medalId: Int) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.MY_MEDAL,
                objectType = LogObjectType.MEDAL,
                objectId = LogObjectId.MEDAL,
                additionalParams = mapOf(
                    ParameterName.MEDAL_ID to medalId.toString()
                )
            )
        )
    }
}
