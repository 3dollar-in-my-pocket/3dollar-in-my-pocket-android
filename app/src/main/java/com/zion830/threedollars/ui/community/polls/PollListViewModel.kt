package com.zion830.threedollars.ui.community.polls

import androidx.lifecycle.viewModelScope
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.community.data.CreatePolicy
import com.threedollar.domain.community.data.PollId
import com.threedollar.domain.community.data.PollList
import com.threedollar.domain.community.model.PollCreateModel
import com.threedollar.domain.community.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PollListViewModel @Inject constructor(private val communityRepository: CommunityRepository) : BaseViewModel() {

    private val _pollItems: MutableSharedFlow<PollList> = MutableSharedFlow()
    val pollItems: SharedFlow<PollList> = _pollItems.asSharedFlow()
    private val _pollSelected: MutableSharedFlow<Pair<String, String>> = MutableSharedFlow()
    val pollSelected: SharedFlow<Pair<String, String>> = _pollSelected.asSharedFlow()
    private val _userPollPolicy: MutableSharedFlow<CreatePolicy> = MutableSharedFlow()
    val userPollPolicy: SharedFlow<CreatePolicy> = _userPollPolicy.asSharedFlow()
    private val _createPoll: MutableSharedFlow<PollId> = MutableSharedFlow()
    val createPoll: SharedFlow<PollId> get() = _createPoll.asSharedFlow()
    private val _toast: MutableSharedFlow<String> = MutableSharedFlow()
    val toast: SharedFlow<String> = _toast.asSharedFlow()

    init {
        getPollPolicy()
    }

    private fun getPollPolicy() {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollPolicy().collect {
                if (it.ok) _userPollPolicy.emit(it.data!!)
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun getPollItems(categoryId: String, sortType: String, cursor: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollList(categoryId, sortType, cursor).collect {
                if (it.ok) _pollItems.emit(it.data!!)
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun createPoll(pollCreateModel: PollCreateModel) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.createPoll(pollCreateModel).collect {
                if (it.ok) {
                    _createPoll.emit(it.data!!)
                    getPollPolicy()
                } else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun votePoll(pollId: String, optionId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.putPollChoice(pollId, optionId).collect {
                if (it.ok) _pollSelected.emit(Pair(pollId, optionId))
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

}

sealed class PollSort(val type: String) {
    object Latest : PollSort("LATEST")
    object Popular : PollSort("POPULAR")
}