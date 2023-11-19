package com.threedollar.presentation.poll

import androidx.lifecycle.viewModelScope
import com.home.domain.data.store.ReportReasonsModel
import com.home.domain.request.ReportReasonsGroupType
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
    private val _report = MutableSharedFlow<DefaultResponse>()
    val report: SharedFlow<DefaultResponse> get() = _report.asSharedFlow()

    private val _pollSelected: MutableSharedFlow<String> = MutableSharedFlow()
    val pollSelected: SharedFlow<String> = _pollSelected.asSharedFlow()

    private val _pollDetail = MutableSharedFlow<PollItem>()
    val pollDetail: SharedFlow<PollItem> get() = _pollDetail.asSharedFlow()

    private val _createComment = MutableSharedFlow<CommentId>()
    val createComment: SharedFlow<CommentId> get() = _createComment.asSharedFlow()

    private val _editComment = MutableSharedFlow<DefaultResponse>()
    val editComment: SharedFlow<DefaultResponse> get() = _editComment.asSharedFlow()

    private val _reportComment = MutableSharedFlow<DefaultResponse>()
    val reportComment: SharedFlow<DefaultResponse> get() = _reportComment.asSharedFlow()

    private val _pollReportList = MutableSharedFlow<ReportReasonsModel>()
    val pollReportList: SharedFlow<ReportReasonsModel> get() = _pollReportList.asSharedFlow()

    private val _pollCommentReportList = MutableSharedFlow<ReportReasonsModel>()
    val pollCommentReportList: SharedFlow<ReportReasonsModel> get() = _pollCommentReportList.asSharedFlow()

    private val _pollComment: MutableSharedFlow<PollCommentList> = MutableSharedFlow()
    val pollComment: SharedFlow<PollCommentList> get() = _pollComment.asSharedFlow()

    init {
        getReportList(ReportReasonsGroupType.POLL)
        getReportList(ReportReasonsGroupType.POLL_COMMENT)
    }

    fun report(reason: String, reasonDetail: String? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.reportPoll(pollId, reason, reasonDetail).collect {
                _report.emit(it)
            }
        }
    }

    fun votePoll(optionId: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.putPollChoice(pollId, optionId).collect {
                _pollSelected.emit(optionId)
            }
        }
    }

    fun pollDetail() {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollId(pollId).collect {
                _pollDetail.emit(it)
            }
        }
    }

    fun createComment(content: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.createPollComment(pollId, content).collect {
                _createComment.emit(it)
            }
        }
    }

    fun editComment(commentId: String, content: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.editPollComment(pollId, commentId, content).collect {
                _editComment.emit(it)
            }
        }
    }

    fun reportComment(commentId: String, reason: String, reasonDetail: String? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.reportPollComment(pollId, commentId, reason, reasonDetail).collect {
                _reportComment.emit(it)
            }
        }
    }

    fun getComment(cursor: String? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getPollCommentList(pollId, cursor).collect {
                _pollComment.emit(it)
            }
        }
    }

    private fun getReportList(reportReasonsGroupType: ReportReasonsGroupType) {
        viewModelScope.launch(coroutineExceptionHandler) {
            communityRepository.getReportReasons(reportReasonsGroupType).collect {
                if (reportReasonsGroupType == ReportReasonsGroupType.POLL) _pollReportList.emit(it)
                else _pollCommentReportList.emit(it)
            }
        }
    }

    fun setPollId(id: String) {
        pollId = id
    }

}