package com.threedollar.presentation.poll

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.home.domain.data.store.ReasonModel
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.listener.ActivityStarter
import com.threedollar.common.listener.EventTrackerListener
import com.threedollar.common.utils.Constants
import com.threedollar.domain.data.PollComment
import com.threedollar.domain.data.PollCommentList
import com.threedollar.domain.data.PollItem
import com.threedollar.presentation.R
import com.threedollar.presentation.databinding.ActivityPollDetailBinding
import com.threedollar.presentation.dialog.ReportChoiceDialog
import com.threedollar.presentation.utils.calculatePercentages
import com.threedollar.presentation.utils.getDeadlineString
import com.threedollar.presentation.utils.hasVotingPeriodEnded
import com.threedollar.presentation.utils.selectedPoll
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick
import javax.inject.Inject

@AndroidEntryPoint
class PollDetailActivity : BaseActivity<ActivityPollDetailBinding, PollDetailViewModel>({ ActivityPollDetailBinding.inflate(it) }) {

    @Inject
    lateinit var activityStarter: ActivityStarter

    @Inject
    lateinit var eventTrackerListener: EventTrackerListener
    override val viewModel: PollDetailViewModel by viewModels()
    private lateinit var pollItem: PollItem
    private var isCommentEdit = false
    private var editCommentId = ""
    private val pollReports = mutableListOf<ReasonModel>()
    private val pollCommentReports = mutableListOf<ReasonModel>()
    private val pollComment = mutableListOf<PollComment>()
    private var pollComments: PollCommentList? = null
    private var isLoading = false
    private val adapter by lazy {
        PollCommentAdapter {
            if (it.current.comment.isOwner) {
                isCommentEdit = true
                editCommentId = it.current.comment.commentId
                binding.etComment.requestFocusFromTouch()
                binding.etComment.setText(it.current.comment.content)
                val bundle = Bundle().apply {
                    putString("screen", "poll_detail")
                    putString("review_id", it.current.comment.commentId)
                }
                eventTrackerListener.logEvent(Constants.CLICK_EDIT_REVIEW, bundle)
            } else {
                ReportChoiceDialog().setType(ReportChoiceDialog.Type.COMMENT).setReportList(pollCommentReports).setReportCallback { reasonModel, s ->
                    if (reasonModel.type == "POLL_OTHER") viewModel.reportComment(it.current.comment.commentId, reasonModel.type, s)
                    else viewModel.reportComment(it.current.comment.commentId, reasonModel.type)
                    if (::pollItem.isInitialized) {
                        val bundle = Bundle().apply {
                            putString("screen", "report_review")
                            putString("poll_id", pollItem.poll.pollId)
                            putString("review_id", it.current.comment.commentId)
                        }
                        eventTrackerListener.logEvent(Constants.CLICK_REPORT, bundle)
                    }
                }.show(supportFragmentManager, "")
                val bundle = Bundle().apply {
                    putString("screen", "poll_detail")
                    putString("review_id", it.current.comment.commentId)
                }
                eventTrackerListener.logEvent(Constants.CLICK_REPORT_COMMENT, bundle)
            }
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "PollDetailActivity", screenName = "poll_detail")
    }

    override fun initView() {
        initViewModel()
        initAdapter()
        initButton()
        initFlow()
    }

    private fun initAdapter() {
        binding.recyclerComment.itemAnimator = null
        binding.recyclerComment.adapter = adapter
        binding.recyclerComment.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    val totalItemCount = it.itemCount
                    val lastVisibleItemPosition = it.findLastVisibleItemPosition()

                    if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 3)) {
                        pollComments?.let { pollList ->
                            if (pollList.cursor.hasMore) {
                                viewModel.getComment(pollList.cursor.nextCursor)
                                isLoading = true
                            }
                        }
                    }
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel.setPollId(intent.getStringExtra("id").orEmpty())
        viewModel.pollDetail()
    }

    private fun initButton() {
        binding.imgClose.onSingleClick { finish() }
        binding.imgCommentWrite.onSingleClick {
            if (isCommentEdit) viewModel.editComment(editCommentId, binding.etComment.text.toString())
            else viewModel.createComment(binding.etComment.text.toString())
        }
        binding.etComment.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    if (isCommentEdit) viewModel.editComment(editCommentId, binding.etComment.text.toString())
                    else viewModel.createComment(binding.etComment.text.toString())
                }

                else -> {}
            }
            return@setOnEditorActionListener true
        }
        binding.btnReport.onSingleClick {
            ReportChoiceDialog().setType(ReportChoiceDialog.Type.POLL).setReportList(pollReports).setReportCallback { reasonModel, s ->
                if (reasonModel.type == "POLL_OTHER") viewModel.report(reasonModel.type, s)
                else viewModel.report(reasonModel.type)
                if (::pollItem.isInitialized) {
                    val bundle = Bundle().apply {
                        putString("screen", "report_poll")
                        putString("poll_id", pollItem.poll.pollId)
                    }
                    eventTrackerListener.logEvent(Constants.CLICK_REPORT, bundle)
                }
            }.show(supportFragmentManager, "")
            if (::pollItem.isInitialized) {
                val bundle = Bundle().apply {
                    putString("screen", "poll_detail")
                    putString("poll_id", pollItem.poll.pollId)
                }
                eventTrackerListener.logEvent(Constants.CLICK_REPORT, bundle)
            }
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.pollDetail.collect {
                        pollItem = it
                        settingCommentCount(it.meta.totalCommentsCount)
                        settingPoll()
                    }
                }
                launch {
                    viewModel.pollSelected.collect {
                        val optionId = it
                        pollItem = selectedPoll(pollItem, optionId)
                        settingPoll()
                    }
                }
                launch {
                    viewModel.createComment.collect {
                        binding.etComment.setText("")
                        pollComments = null
                        adapter.submitList(emptyList())
                        pollComment.clear()
                        viewModel.getComment()
                    }
                }
                launch {
                    viewModel.editComment.collect { commentId ->
                        val content = binding.etComment.text.toString()
                        val comment = pollComment.find { it.current.comment.commentId == commentId } ?: return@collect
                        val currentComment = comment.current.comment.copy(content = content)
                        val changeComment = comment.copy(current = comment.current.copy(comment = currentComment))
                        val index = pollComment.indexOf(comment)
                        if (index > -1) {
                            pollComment[index] = changeComment
                            adapter.submitList(pollComment.toList())
                        }
                        isCommentEdit = false
                        editCommentId = ""
                        binding.etComment.setText("")
                    }
                }
                launch {
                    viewModel.report.collect {

                    }
                }
                launch {
                    viewModel.reportComment.collect {
                        pollComments = null
                        adapter.submitList(emptyList())
                        pollComment.clear()
                        viewModel.getComment()
                    }
                }
                launch {
                    viewModel.pollReportList.collect {
                        pollReports.addAll(it.reasonModels)
                    }
                }
                launch {
                    viewModel.pollCommentReportList.collect {
                        pollCommentReports.addAll(it.reasonModels)
                    }
                }
                launch {
                    viewModel.pollComment.collect {
                        isLoading = false
                        pollComments = it
                        pollComment.addAll(it.pollComments)
                        settingCommentCount(pollItem.meta.totalCommentsCount.coerceAtLeast(it.pollComments.size))
                        adapter.submitList(pollComment.toList())
                    }
                }
                launch {
                    viewModel.toast.collect {
                        Toast.makeText(this@PollDetailActivity, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun finish() {
        setResult(RESULT_OK, Intent().apply {
            putExtra("pollItem", pollItem)
        })
        activityStarter.activityNavigateToMainActivityOnCloseIfNeeded(this)
        super.finish()
    }

    private fun settingCommentCount(count: Int) {
        val text = "${count}개 의견"
        val spannableString = SpannableString(text)
        val spanStart = text.indexOf("개")
        val spanEnd = spanStart + "개".length
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(
            boldSpan,
            0,
            spanEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.twPollCommentCount.text = spannableString
    }

    private fun settingPoll() {
        if (!::pollItem.isInitialized) {
            Toast.makeText(this, "잘못된 접근 입니다.", Toast.LENGTH_SHORT).show()
            return finish()
        }
        val first = pollItem.poll.options[0]
        val second = pollItem.poll.options[1]
        val isSelected = first.choice.selectedByMe || second.choice.selectedByMe
        val hasVotingPeriodEnded = hasVotingPeriodEnded(pollItem.poll.period.endDateTime)
        binding.twPollFirstChoice.isVisible = !isSelected || !hasVotingPeriodEnded
        binding.twPollSecondChoice.isVisible = !isSelected || !hasVotingPeriodEnded
        binding.clPollVoteFirstChoice.isVisible = isSelected || hasVotingPeriodEnded
        binding.clPollVoteSecondChoice.isVisible = isSelected || hasVotingPeriodEnded
        if (hasVotingPeriodEnded) {
            binding.clPollVoteFirstChoice.isVisible = true
            binding.clPollVoteSecondChoice.isVisible = true
            binding.twPollFirstChoice.isVisible = false
            binding.twPollSecondChoice.isVisible = false
        } else {
            binding.twPollFirstChoice.isVisible = !isSelected
            binding.twPollSecondChoice.isVisible = !isSelected
            binding.clPollVoteFirstChoice.isVisible = isSelected
            binding.clPollVoteSecondChoice.isVisible = isSelected
        }

        if (isSelected || hasVotingPeriodEnded) {
            val firstVoteCount = first.choice.count
            val secondVoteCount = second.choice.count
            val firstBack = if (firstVoteCount > secondVoteCount) {
                if (first.choice.selectedByMe) getDrawable(R.drawable.rect_poll_selected_most)
                else getDrawable(R.drawable.rect_poll_default_most)
            } else {
                if (first.choice.selectedByMe) getDrawable(R.drawable.rect_poll_selected)
                else getDrawable(R.drawable.rect_poll_default)
            }
            val secondBack = if (secondVoteCount > firstVoteCount) {
                if (second.choice.selectedByMe) getDrawable(R.drawable.rect_poll_selected_most)
                else getDrawable(R.drawable.rect_poll_default_most)
            } else {
                if (second.choice.selectedByMe) getDrawable(R.drawable.rect_poll_selected)
                else getDrawable(R.drawable.rect_poll_default)
            }
            binding.llPollFirstChoice.background = firstBack
            binding.llPollSecondChoice.background = secondBack
            binding.clPollVoteFirstChoice.isSelected = firstVoteCount > secondVoteCount
            binding.clPollVoteSecondChoice.isSelected = secondVoteCount > firstVoteCount
            binding.twPollVoteFirstChoice.text = first.name
            binding.twPollVoteSecondChoice.text = second.name

            binding.twPollVoteFirstCount.text = "${first.choice.count}명"
            binding.twPollVoteSecondCount.text = "${second.choice.count}명"

            binding.twPollVoteFirstIcon.text = if (firstVoteCount > secondVoteCount) "\uD83E\uDD23" else "\uD83D\uDE1E"
            binding.twPollVoteSecondIcon.text = if (secondVoteCount > firstVoteCount) "\uD83E\uDD23" else "\uD83D\uDE1E"
            val percent = calculatePercentages(firstVoteCount, secondVoteCount)
            binding.twPollVoteFirstPercent.text = "${percent.first}%"
            binding.twPollVoteSecondPercent.text = "${percent.second}%"

        } else {
            binding.twPollFirstChoice.text = first.name
            binding.twPollSecondChoice.text = second.name
        }
        binding.twPollTitle.text = pollItem.poll.content.title
        binding.twPollNickName.text = pollItem.pollWriter.name
        binding.twMedalName.text = pollItem.pollWriter.medal.name
        binding.twPollUserCount.text = getString(R.string.str_vote_count, pollItem.meta.totalParticipantsCount)
        binding.twPollEndDate.text = getDeadlineString(pollItem.poll.period.endDateTime)

        binding.imgMedal.loadUrlImg(pollItem.pollWriter.medal.iconUrl)

        binding.llPollFirstChoice.onSingleClick {
            if (first.choice.selectedByMe) return@onSingleClick
            viewModel.votePoll(first.optionId)
            val bundle = Bundle().apply {
                putString("screen", "poll_detail")
                putString("poll_id", pollItem.poll.pollId)
                putString("option_id", first.optionId)
            }
            eventTrackerListener.logEvent(Constants.CLICK_POLL_OPTION, bundle)
        }
        binding.llPollSecondChoice.onSingleClick {
            if (second.choice.selectedByMe) return@onSingleClick
            viewModel.votePoll(second.optionId)
            val bundle = Bundle().apply {
                putString("screen", "poll_detail")
                putString("poll_id", pollItem.poll.pollId)
                putString("option_id", first.optionId)
            }
            eventTrackerListener.logEvent(Constants.CLICK_POLL_OPTION, bundle)
        }
    }
}