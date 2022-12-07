package com.zion830.threedollars.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.FavoriteMyFolderDataSourceImpl
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {
    val favoriteMyFolderPager = Pager(PagingConfig(FavoriteMyFolderDataSourceImpl.LOAD_SIZE)) { FavoriteMyFolderDataSourceImpl() }.flow

    private val _myFavoriteFolderResponse: MutableLiveData<MyFavoriteFolderResponse> = MutableLiveData()
    val myFavoriteFolderResponse: LiveData<MyFavoriteFolderResponse> get() = _myFavoriteFolderResponse

    private val _isRefresh: MutableLiveData<Boolean> = MutableLiveData()
    val isRefresh: LiveData<Boolean> get() = _isRefresh

    fun getMyFavoriteFolder() {
        viewModelScope.launch {
            val response = userDataSource.getMyFavoriteFolder(null, 1)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _myFavoriteFolderResponse.value = it
                }
            } else {
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }

    fun allDeleteFavorite() {
        viewModelScope.launch {
            val response = userDataSource.allDeleteFavorite()
            _isRefresh.value = response.isSuccessful
        }
    }

    fun deleteFavorite(storeType: String, storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = userDataSource.deleteFavorite(storeType, storeId)
            _isRefresh.value = response.isSuccessful
        }
    }
}