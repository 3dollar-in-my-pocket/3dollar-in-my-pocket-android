package com.zion830.threedollars.ui.community.poll

import androidx.lifecycle.viewModelScope
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseResponse
import com.threedollar.common.base.BaseViewModel
import com.threedollar.domain.community.data.CommentId
import com.threedollar.domain.community.data.PollComment
import com.threedollar.domain.community.data.PollCommentList
import com.threedollar.domain.community.data.PollItem
import com.threedollar.domain.community.model.ReportReasonsGroupType
import com.threedollar.domain.community.model.ReportReasonsModel
import com.threedollar.domain.community.repository.CommunityRepository
import com.zion830.threedollars.ui.like.likeRequestStickerId
import com.zion830.threedollars.ui.like.likeSticker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PollDetailViewModel @Inject constructor(private val communityRepository: CommunityRepository) : BaseViewModel() {

    override val screenName: ScreenName = ScreenName.POLL_DETAIL

    private var pollId: String = ""
    private val _report = MutableSharedFlow<BaseResponse<String>>()
    val report: SharedFlow<BaseResponse<String>> get() = _report.asSharedFlow()

    private val _pollSelected: MutableSharedFlow<String> = MutableSharedFlow()
    val pollSelected: SharedFlow<String> = _pollSelected.asSharedFlow()

    private val _pollDetail = MutableSharedFlow<PollItem>()
    val pollDetail: SharedFlow<PollItem> get() = _pollDetail.asSharedFlow()

    private val _createComment = MutableSharedFlow<CommentId>()
    val createComment: SharedFlow<CommentId> get() = _createComment.asSharedFlow()

    private val _editComment = MutableSharedFlow<String>()
    val editComment: SharedFlow<String> get() = _editComment.asSharedFlow()

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

    private val _commentLikeChanged = MutableSharedFlow<PollComment>()
    val commentLikeChanged: SharedFlow<PollComment> get() = _commentLikeChanged.asSharedFlow()

    private val _commentLikeFailed = MutableSharedFlow<String?>()
    val commentLikeFailed: SharedFlow<String?> get() = _commentLikeFailed.asSharedFlow()

    private val likingCommentIds = mutableSetOf<String>()

    private val _pollAd = MutableSharedFlow<List<AdvertisementModelV2>>()
    val pollAd: SharedFlow<List<AdvertisementModelV2>> get() = _pollAd.asSharedFlow()

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
                if (it.ok) _editComment.emit(commentId)
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

    /**
     * 댓글 좋아요를 먼저 화면에 반영하고 스티커 교체를 요청한다.
     * 실패하면 원래 상태로 되돌리고, 응답 전에는 같은 댓글의 요청을 다시 보내지 않는다.
     */
    fun toggleCommentLike(comment: PollComment) {
        val commentId = comment.current.comment.commentId
        if (!likingCommentIds.add(commentId)) return
        val toggled = comment.withToggledLike()
        sendClickLike(commentId, toggled.current.stickers.likeSticker.reactedByMe)
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                _commentLikeChanged.emit(toggled)
                val response = try {
                    communityRepository.putPollCommentSticker(pollId, commentId, comment.current.stickers.likeRequestStickerId()).firstOrNull()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    null
                }
                if (response?.ok != true) {
                    _commentLikeChanged.emit(comment)
                    _commentLikeFailed.emit(response?.message?.takeIf { it.isNotBlank() })
                }
            } finally {
                likingCommentIds.remove(commentId)
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

    // GA Events - Poll Detail
    fun sendClickReport() {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.REPORT
            )
        )
    }

    fun sendClickPollOption(optionId: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.POLL_OPTION,
                additionalParams = mapOf(
                    ParameterName.POLL_ID to pollId,
                    ParameterName.OPTION_ID to optionId
                )
            )
        )
    }

    private fun sendClickLike(commentId: String, isLiked: Boolean) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.LIKE,
                additionalParams = mapOf(
                    ParameterName.POLL_ID to pollId,
                    ParameterName.REVIEW_ID to commentId,
                    ParameterName.VALUE to isLiked
                )
            )
        )
    }

    fun sendClickReportReview(reviewId: String) {
        LogManager.sendEvent(
            ClickEvent(
                screen = screenName,
                objectType = LogObjectType.BUTTON,
                objectId = LogObjectId.REPORT_REVIEW,
                additionalParams = mapOf(
                    ParameterName.REVIEW_ID to reviewId,
                    ParameterName.POLL_ID to pollId
                )
            )
        )
    }
}