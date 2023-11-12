package com.threedollar.presentation.polls

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.repository.CommunityRepository
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PollListViewModel @Inject constructor(private val communityRepository: CommunityRepository) : BaseViewModel() {

    private val _pollItems: MutableSharedFlow<PagingData<PollItem>> = MutableSharedFlow()
    val pollItems: SharedFlow<PagingData<PollItem>> = _pollItems.asSharedFlow()
    private val _pollSelected: MutableSharedFlow<Pair<String, String>> = MutableSharedFlow()
    val pollSelected: SharedFlow<Pair<String, String>> = _pollSelected.asSharedFlow()


    fun getPollItems(categoryId: String, sortType: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollList(categoryId, sortType).collect {
                _pollItems.emit(it)
            }
        }
    }

    fun createPoll(pollCreateApiRequest: PollCreateApiRequest) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.createPoll(pollCreateApiRequest).collect {

            }
        }
    }

    fun votePoll(pollId: String, optionId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.putPollChoice(pollId, optionId).collect {
                _pollSelected.emit(Pair(pollId, optionId))
            }
        }
    }

}

sealed class PollSort(val type: String) {
    object Latest : PollSort("LATEST")
    object Popular : PollSort("POPULAR")
}