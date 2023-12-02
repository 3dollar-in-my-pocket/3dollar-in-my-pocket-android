package com.threedollar.presentation.poll

import androidx.lifecycle.viewModelScope
import com.home.domain.data.store.ReportReasonsModel
import com.home.domain.request.ReportReasonsGroupType
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.data.CommentId
import com.threedollar.domain.data.DefaultResponse
import com.threedollar.domain.data.PollCommentList
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PollDetailViewModel @Inject constructor(private val communityRepository: CommunityRepository) : BaseViewModel() {

    private var pollId: String = ""
    private val _report = MutableSharedFlow<BaseResponse<String>>()
    val report: SharedFlow<BaseResponse<String>> get() = _report.asSharedFlow()

    private val _pollSelected: MutableSharedFlow<String> = MutableSharedFlow()
    val pollSelected: SharedFlow<String> = _pollSelected.asSharedFlow()

    private val _pollDetail = MutableSharedFlow<PollItem>()
    val pollDetail: SharedFlow<PollItem> get() = _pollDetail.asSharedFlow()

    private val _createComment = MutableSharedFlow<CommentId>()
    val createComment: SharedFlow<CommentId> get() = _createComment.asSharedFlow()

    private val _editComment = MutableSharedFlow<BaseResponse<String>>()
    val editComment: SharedFlow<BaseResponse<String>> get() = _editComment.asSharedFlow()

    private val _reportComment = MutableSharedFlow<BaseResponse<String>>()
    val reportComment: SharedFlow<BaseResponse<String>> get() = _reportComment.asSharedFlow()

    private val _pollReportList = MutableSharedFlow<ReportReasonsModel>()
    val pollReportList: SharedFlow<ReportReasonsModel> get() = _pollReportList.asSharedFlow()

    private val _pollCommentReportList = MutableSharedFlow<ReportReasonsModel>()
    val pollCommentReportList: SharedFlow<ReportReasonsModel> get() = _pollCommentReportList.asSharedFlow()

    private val _pollComment: MutableSharedFlow<PollCommentList> = MutableSharedFlow()
    val pollComment: SharedFlow<PollCommentList> get() = _pollComment.asSharedFlow()
    private val _toast: MutableSharedFlow<String> = MutableSharedFlow()
    val toast: SharedFlow<String> = _toast.asSharedFlow()

    init {
        getReportList(ReportReasonsGroupType.POLL)
        getReportList(ReportReasonsGroupType.POLL_COMMENT)
    }

    fun report(reason: String, reasonDetail: String? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.reportPoll(pollId, reason, reasonDetail).collect {
                if (it.ok) _report.emit(it)
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun votePoll(optionId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.putPollChoice(pollId, optionId).collect {
                if (it.ok) _pollSelected.emit(optionId)
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun pollDetail() {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollId(pollId).collect {
                if (it.ok) {
                    _pollDetail.emit(it.data!!)
                    getComment()
                } else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun createComment(content: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.createPollComment(pollId, content).collect {
                if (it.ok) _createComment.emit(it.data!!)
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun editComment(commentId: String, content: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.editPollComment(pollId, commentId, content).collect {
                if (it.ok) _editComment.emit(it)
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun reportComment(commentId: String, reason: String, reasonDetail: String? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.reportPollComment(pollId, commentId, reason, reasonDetail).collect {
                if (it.ok) _reportComment.emit(it)
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun getComment(cursor: String? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollCommentList(pollId, cursor).collect {
                if (it.ok) _pollComment.emit(it.data!!)
                else _toast.emit(it.message.orEmpty())
            }
        }
    }

    private fun getReportList(reportReasonsGroupType: ReportReasonsGroupType) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getReportReasons(reportReasonsGroupType).collect {
                if (it.ok) {
                    if (reportReasonsGroupType == ReportReasonsGroupType.POLL) _pollReportList.emit(it.data!!)
                    else _pollCommentReportList.emit(it.data!!)
                } else _toast.emit(it.message.orEmpty())
            }
        }
    }

    fun setPollId(id: String) {
        pollId = id
    }

}