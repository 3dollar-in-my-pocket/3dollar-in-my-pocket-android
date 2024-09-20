package com.zion830.threedollars.ui.mypage.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.my.domain.repository.MyRepository
import com.threedollar.common.base.BaseViewModel
import com.threedollar.common.ext.toStringDefault
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.UserDataSource
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.datasource.model.v2.response.my.Medal
import com.zion830.threedollars.datasource.model.v2.response.my.UserActivityData
import com.threedollar.network.request.PatchUserInfoRequest
import com.zion830.threedollars.ui.mypage.adapter.MyMedal
import com.zion830.threedollars.utils.getErrorMessage
import com.zion830.threedollars.utils.showCustomBlackToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMealViewModel @Inject constructor(private val userDataSource: UserDataSource, private val myRepository: MyRepository) : BaseViewModel() {

    private val _userActivity: MutableLiveData<UserActivityData?> = MutableLiveData()
    val userActivity: LiveData<UserActivityData?> = _userActivity

    private val _myMedals: MutableLiveData<List<MyMedal>> = MutableLiveData()
    val myMedals: MutableLiveData<List<MyMedal>> = _myMedals

    val selectedMedal = userActivity.map { it?.medal }

    private val _allMedals: MutableLiveData<List<Medal>> = MutableLiveData()
    val allMedals: LiveData<List<Medal>> = _allMedals

    private val _myFavoriteModel: MutableLiveData<List<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>> = MutableLiveData()
    val myFavoriteModel: LiveData<List<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>> get() = _myFavoriteModel
    var id = ""
    var isMoveMedalPage = false

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
                _msgTextId.postValue(R.string.connection_failed)
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
            myRepository.patchUserInfo(PatchUserInfoRequest(representativeMedalId = medalId)).collect {
                if (it.ok) {
                    requestUserActivity()
                } else {
                    _msgTextId.postValue(R.string.error_change_medal)
                    _msgTextId.postValue(-1)
                }
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
