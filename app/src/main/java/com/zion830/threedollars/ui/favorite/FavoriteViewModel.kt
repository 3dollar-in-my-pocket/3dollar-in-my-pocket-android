package com.zion830.threedollars.ui.favorite

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
import com.threedollar.domain.home.repository.HomeRepository
import com.threedollar.common.base.BaseViewModel
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.datasource.FavoriteMyFolderDataSourceImpl
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.network.NewServiceApi
import com.zion830.threedollars.utils.getErrorMessage
import com.zion830.threedollars.utils.showCustomBlackToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    private val homeRepository: HomeRepository,
    private val newServiceApi: NewServiceApi
) : BaseViewModel() {
    val favoriteMyFolderPager = Pager(PagingConfig(FavoriteMyFolderDataSourceImpl.LOAD_SIZE)) {
        FavoriteMyFolderDataSourceImpl(newServiceApi)
    }.flow

    private val _myFavoriteFolderResponse: MutableLiveData<MyFavoriteFolderResponse> = MutableLiveData()
    val myFavoriteFolderResponse: LiveData<MyFavoriteFolderResponse> get() = _myFavoriteFolderResponse

    private val _isRefresh: MutableLiveData<Boolean> = MutableLiveData()
    val isRefresh: LiveData<Boolean> get() = _isRefresh

    private val _effect = Channel<Event>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.receiveAsFlow()

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

    fun deleteFavorite(storeId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            homeRepository.deleteFavorite(storeId).collect { model ->
                _isRefresh.value = model.ok
            }
        }
    }

    fun share() {
        val folderId = myFavoriteFolderResponse.value?.folderId
        if (folderId.isNullOrEmpty()) {
            return
        }

        _effect.trySend(Event.Share(folderId))
    }

    sealed interface Event {
        data class Share(
            val folderId: String
        ) : Event
    }
}
