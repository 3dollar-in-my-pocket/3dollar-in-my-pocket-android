package com.zion830.threedollars.ui.favorite

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.zion830.threedollars.BuildConfig
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.datasource.FavoriteMyFolderDataSourceImpl
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.utils.getErrorMessage
import com.zion830.threedollars.utils.showCustomBlackToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseViewModel
import zion830.com.common.ext.toStringDefault
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {
    val favoriteMyFolderPager = Pager(PagingConfig(FavoriteMyFolderDataSourceImpl.LOAD_SIZE)) { FavoriteMyFolderDataSourceImpl() }.flow

    private val _myFavoriteFolderResponse: MutableLiveData<MyFavoriteFolderResponse> = MutableLiveData()
    val myFavoriteFolderResponse: LiveData<MyFavoriteFolderResponse> get() = _myFavoriteFolderResponse

    private val _isRefresh: MutableLiveData<Boolean> = MutableLiveData()
    val isRefresh: LiveData<Boolean> get() = _isRefresh

    private val _dynamicLink: MutableLiveData<String> = MutableLiveData()
    val dynamicLink: LiveData<String> get() = _dynamicLink

    fun getMyFavoriteFolder() {
        viewModelScope.launch {
            val response = userDataSource.getMyFavoriteFolder(null, 1)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _myFavoriteFolderResponse.value = it
                }
            } else {
                response.errorBody()?.string()?.getErrorMessage()?.let { showCustomBlackToast(it) }
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

    fun createShareUrl() {
        val folderId = _myFavoriteFolderResponse.value?.folderId.toStringDefault()
        val folderName = _myFavoriteFolderResponse.value?.name.toStringDefault()
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("${GlobalApplication.DYNAMIC_LINK}/bookmark?folderId=$folderId")
            domainUriPrefix = BuildConfig.DEEP_LINK
            androidParameters { }
            iosParameters(if (BuildConfig.DEBUG) "com.macgongmon.-dollar-in-my-pocket-debug" else "com.macgongmon.-dollar-in-my-pocket") {
                appStoreId = "1496099467"
                minimumVersion = "3.3.0"
            }
            socialMetaTagParameters {
                title = "내 음식 플리 들어볼래?"
                description = folderName
                imageUrl = Uri.parse("https://storage.threedollars.co.kr/share/favorite_share.png")
            }
        }.addOnCompleteListener {
            if (it.isComplete) {
                _dynamicLink.value = it.result.shortLink?.toString()
            }
        }
    }
}