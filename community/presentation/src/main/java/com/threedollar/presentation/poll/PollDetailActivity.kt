package com.threedollar.presentation.poll

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.home.domain.data.store.ReasonModel
import com.threedollar.domain.data.PollComment
import com.threedollar.domain.data.PollCommentList
import com.threedollar.domain.data.PollItem
import com.threedollar.presentation.R
import com.threedollar.presentation.databinding.ActivityPollDetailBinding
import com.threedollar.presentation.dialog.ReportChoiceDialog
import com.threedollar.presentation.utils.calculatePercentages
import com.threedollar.presentation.utils.getDeadlineString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class PollDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPollDetailBinding
    private val viewModel: PollDetailViewModel by viewModels()
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
            if (it.current.poll.isWriter) {
                isCommentEdit = true
                editCommentId = it.current.comment.commentId
                binding.etComment.requestFocusFromTouch()
                binding.etComment.setText(it.current.comment.content)
            } else {
                ReportChoiceDialog().setReportList(pollCommentReports).setReportCallback { reasonModel, s ->
                    if (reasonModel.type == "POLL_OTHER") viewModel.reportComment(it.current.comment.commentId, reasonModel.type, s)
                    else viewModel.reportComment(it.current.comment.commentId, reasonModel.type)
                }.show(supportFragmentManager, "")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPollDetailBinding.inflate(getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        setContentView(binding.root)
        viewModel.setPollId(intent.getStringExtra("id").orEmpty())
        viewModel.pollDetail()
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
        binding.imgClose.onSingleClick { finish() }
        binding.imgCommentWrite.onSingleClick {
            if (isCommentEdit) viewModel.editComment(editCommentId, binding.etComment.text.toString())
            else viewModel.createComment(binding.etComment.text.toString())
        }
        binding.etComment.setOnEditorActionListener { v, actionId, event ->
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
            ReportChoiceDialog().setReportList(pollReports).setReportCallback { reasonModel, s ->
                if (reasonModel.type == "POLL_OTHER") viewModel.report(reasonModel.type, s)
                else viewModel.report(reasonModel.type)
            }.show(supportFragmentManager, "")
        }

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
                        var firstChoice = pollItem.poll.options[0]
                        var secondChoice = pollItem.poll.options[1]
                        val editFirstCount = if (firstChoice.optionId == optionId) 1 else -1
                        val editSecondCount = if (secondChoice.optionId == optionId) 1 else -1
                        firstChoice = firstChoice.copy(
                            choice = firstChoice.choice.copy(
                                selectedByMe = firstChoice.optionId == optionId,
                                count = firstChoice.choice.count + editFirstCount
                            )
                        )
                        secondChoice = secondChoice.copy(
                            choice = secondChoice.choice.copy(
                                selectedByMe = secondChoice.optionId == optionId,
                                count = secondChoice.choice.count + editSecondCount
                            )
                        )
                        pollItem = pollItem.copy(poll = pollItem.poll.copy(options = listOf(firstChoice, secondChoice)))
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
                    viewModel.editComment.collect {
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
                        adapter.submitList(pollComment)
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
        val first = pollItem.poll.options[0]
        val second = pollItem.poll.options[1]
        val isSelected = first.choice.selectedByMe || second.choice.selectedByMe
        binding.twPollFirstChoice.isVisible = !isSelected
        binding.twPollSecondChoice.isVisible = !isSelected
        binding.clPollVoteFirstChoice.isVisible = isSelected
        binding.clPollVoteSecondChoice.isVisible = isSelected

        if (isSelected) {
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
        }
        binding.llPollSecondChoice.onSingleClick {
            if (second.choice.selectedByMe) return@onSingleClick
            viewModel.votePoll(second.optionId)
        }
    }
}