package com.zion830.threedollars.ui.community.poll

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
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
import com.google.android.gms.ads.AdRequest
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.listener.ActivityStarter
import com.threedollar.domain.community.data.PollComment
import com.threedollar.domain.community.data.PollCommentList
import com.threedollar.domain.community.data.PollItem
import com.threedollar.domain.community.model.ReportReason
import com.zion830.threedollars.databinding.ActivityPollDetailBinding
import com.zion830.threedollars.ui.community.dialog.ReportChoiceDialog
import com.zion830.threedollars.ui.community.utils.calculatePercentages
import com.zion830.threedollars.ui.community.utils.getDeadlineString
import com.zion830.threedollars.ui.community.utils.hasVotingPeriodEnded
import com.zion830.threedollars.ui.community.utils.selectedPoll
import com.threedollar.common.R as CommonR
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick
import javax.inject.Inject
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class PollDetailActivity : BaseActivity<ActivityPollDetailBinding, PollDetailViewModel>({ ActivityPollDetailBinding.inflate(it) }) {

    @Inject
    lateinit var activityStarter: ActivityStarter

    override val viewModel: PollDetailViewModel by viewModels()
    private lateinit var pollItem: PollItem
    private var isCommentEdit = false
    private var editCommentId = ""
    private val pollReports = mutableListOf<ReportReason>()
    private val pollCommentReports = mutableListOf<ReportReason>()
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
            } else {
                viewModel.sendClickReportReview(it.current.comment.commentId)
                ReportChoiceDialog()
                    .setType(ReportChoiceDialog.Type.COMMENT)
                    .setReportList(pollCommentReports)
                    .setPollId(if (::pollItem.isInitialized) pollItem.poll.pollId else "")
                    .setReviewId(it.current.comment.commentId)
                    .setReportCallback { reasonModel, s ->
                        if (reasonModel.id == "POLL_OTHER") viewModel.reportComment(it.current.comment.commentId, reasonModel.id, s)
                        else viewModel.reportComment(it.current.comment.commentId, reasonModel.id)
                    }.show(supportFragmentManager, "")
            }
        }
    }

    override fun initView() {
        setLightSystemBars()
        initViewModel()
        initAdapter()
        initButton()
        initFlow()
        initAdmob()
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

    private fun initAdmob() {
        val adRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)
    }

    @SuppressLint("MissingPermission")
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
            viewModel.sendClickReport()
            ReportChoiceDialog()
                .setType(ReportChoiceDialog.Type.POLL)
                .setReportList(pollReports)
                .setPollId(if (::pollItem.isInitialized) pollItem.poll.pollId else "")
                .setReportCallback { reasonModel, s ->
                    if (reasonModel.id == "POLL_OTHER") viewModel.report(reasonModel.id, s)
                    else viewModel.report(reasonModel.id)
                }.show(supportFragmentManager, "")
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
                        pollReports.addAll(it.reasons)
                    }
                }
                launch {
                    viewModel.pollCommentReportList.collect {
                        pollCommentReports.addAll(it.reasons)
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
        if (::pollItem.isInitialized) {
            setResult(RESULT_OK, Intent().apply {
                putExtra("pollItem", pollItem)
            })
        }
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
                if (first.choice.selectedByMe) getDrawable(DesignSystemR.drawable.rect_poll_selected_most)
                else getDrawable(DesignSystemR.drawable.rect_poll_default_most)
            } else {
                if (first.choice.selectedByMe) getDrawable(DesignSystemR.drawable.rect_poll_selected)
                else getDrawable(DesignSystemR.drawable.rect_poll_default)
            }
            val secondBack = if (secondVoteCount > firstVoteCount) {
                if (second.choice.selectedByMe) getDrawable(DesignSystemR.drawable.rect_poll_selected_most)
                else getDrawable(DesignSystemR.drawable.rect_poll_default_most)
            } else {
                if (second.choice.selectedByMe) getDrawable(DesignSystemR.drawable.rect_poll_selected)
                else getDrawable(DesignSystemR.drawable.rect_poll_default)
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
        binding.twPollUserCount.text = getString(CommonR.string.str_vote_count, pollItem.meta.totalParticipantsCount)
        binding.twPollEndDate.text = getDeadlineString(pollItem.poll.period.endDateTime)

        binding.imgMedal.loadUrlImg(pollItem.pollWriter.medal.iconUrl)

        binding.llPollFirstChoice.onSingleClick {
            if (first.choice.selectedByMe) return@onSingleClick
            viewModel.sendClickPollOption(first.optionId)
            viewModel.votePoll(first.optionId)
        }
        binding.llPollSecondChoice.onSingleClick {
            if (second.choice.selectedByMe) return@onSingleClick
            viewModel.sendClickPollOption(second.optionId)
            viewModel.votePoll(second.optionId)
        }
    }
}