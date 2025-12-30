package com.zion830.threedollars.ui.favorite.viewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseViewModel
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.network.util.errorResponseOrNull
import com.zion830.threedollars.datasource.UserDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewerViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.BOOKMARK_LIST_VIEWER

    private val _favoriteViewer = MutableLiveData<MyFavoriteFolderResponse>()
    val favoriteViewer: LiveData<MyFavoriteFolderResponse> get() = _favoriteViewer
    private val _eventClick = MutableLiveData<Event>()
    val eventClick: LiveData<Event> get() = _eventClick

    private val _error = Channel<FavoriteViewerError>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val error: Flow<FavoriteViewerError> = _error.receiveAsFlow()

    fun onEventClick(event: Event) {
        _eventClick.value = event
    }

    fun onItemClick(item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel) {
        _eventClick.value = Event.Viewer(item)
    }

    fun getFavoriteViewer(id: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = userDataSource.getFavoriteViewer(id, null)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _favoriteViewer.value = it
                }
            } else {
                val errorResponse = response.errorResponseOrNull()
                val errorEvent = if (errorResponse != null) {
                    FavoriteViewerError.ApiError(message = errorResponse.message.orEmpty())
                } else {
                    FavoriteViewerError.Unknown
                }
                _error.send(errorEvent)
            }
        }
    }

    fun sendClickStore(storeId: String, storeType: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = ScreenName.BOOKMARK_LIST_VIEWER,
                objectType = LogObjectType.CARD,
                objectId = LogObjectId.STORE,
                additionalParams = mapOf(
                    ParameterName.STORE_ID to storeId,
                    ParameterName.STORE_TYPE to storeType
                )
            )
        )
    }

    sealed class Event {
        object Close : Event()
        data class Viewer(val item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel) : Event()
    }

}