package com.zion830.threedollars.ui.mypage.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.ext.toStringDefault
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.request.UpdateMedalRequest
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.datasource.model.v2.response.my.Medal
import com.zion830.threedollars.datasource.model.v2.response.my.UserActivityData
import com.zion830.threedollars.datasource.model.v2.response.visit_history.VisitHistoryContent
import com.zion830.threedollars.ui.mypage.adapter.MyMedal
import com.zion830.threedollars.utils.getErrorMessage
import com.zion830.threedollars.utils.showCustomBlackToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    private val _userActivity: MutableLiveData<UserActivityData?> = MutableLiveData()
    val userActivity: LiveData<UserActivityData?> = _userActivity

    private val _myVisitHistory: MutableLiveData<List<VisitHistoryContent>?> = MutableLiveData()
    val myVisitHistory: LiveData<List<VisitHistoryContent>?> = _myVisitHistory

    private val _myMedals: MutableLiveData<List<MyMedal>> = MutableLiveData()
    val myMedals: MutableLiveData<List<MyMedal>> = _myMedals

    val selectedMedal = Transformations.map(userActivity) { it?.medal }

    private val _allMedals: MutableLiveData<List<Medal>> = MutableLiveData()
    val allMedals: LiveData<List<Medal>> = _allMedals

    private val _myFavoriteModel: MutableLiveData<List<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>> = MutableLiveData()
    val myFavoriteModel: LiveData<List<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>> get() = _myFavoriteModel
    var id = ""
    var isMoveMedalPage = false
    fun initAllMedals() {
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
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }

    fun requestVisitHistory() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = userDataSource.getMyVisitHistory(null, 20)
            if (response.isSuccessful) {
                val myVisitHistory = response.body()?.data
                _myVisitHistory.postValue(myVisitHistory?.contents)
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

    fun changeMedal(medalId: Int) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = userDataSource.updateMyMedals(UpdateMedalRequest(medalId))
            if (response.isSuccessful) {
                requestUserActivity()
            } else {
                _msgTextId.postValue(R.string.error_change_medal)
                _msgTextId.postValue(-1)
            }
        }
    }

    fun getMyFavoriteFolder(cursor: String? = null, size: Int = 5) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = userDataSource.getMyFavoriteFolder(cursor, size)
            if (response.isSuccessful) {
                id = response.body()?.data?.folderId.toStringDefault()
                _myFavoriteModel.value = response.body()?.data?.favorites
            } else {
                response.errorBody()?.string()?.getErrorMessage()?.let { showCustomBlackToast(it) }
            }
        }
    }
}
