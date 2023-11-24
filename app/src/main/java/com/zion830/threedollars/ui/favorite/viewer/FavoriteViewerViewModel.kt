package com.zion830.threedollars.ui.favorite.viewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.threedollar.common.base.BaseViewModel
import com.zion830.threedollars.R
import com.zion830.threedollars.datasource.UserDataSource
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewerViewModel @Inject constructor(private val userDataSource: UserDataSource) : BaseViewModel() {

    private val _favoriteViewer = MutableLiveData<MyFavoriteFolderResponse>()
    val favoriteViewer: LiveData<MyFavoriteFolderResponse> get() = _favoriteViewer
    private val _eventClick = MutableLiveData<Event>()
    val eventClick: LiveData<Event> get() = _eventClick

    fun onEventClick(event: Event) {
        _eventClick.value = event
    }

    fun onItemClick(item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel) {
        _eventClick.value = Event.Viewer(item)
    }

    fun getFavoriteViewer(id: String) {
        viewModelScope.launch {
            val response = userDataSource.getFavoriteViewer(id, null)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    _favoriteViewer.value = it
                }
            } else {
                _msgTextId.postValue(R.string.connection_failed)
            }
        }
    }

    sealed class Event {
        object Close : Event()
        data class Viewer(val item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel) : Event()
    }

}