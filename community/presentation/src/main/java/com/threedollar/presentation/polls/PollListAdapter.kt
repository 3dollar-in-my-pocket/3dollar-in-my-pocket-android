package com.threedollar.presentation.polls

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.data.PollItem
import com.threedollar.presentation.R
import com.threedollar.presentation.databinding.ItemPollBinding
import com.threedollar.presentation.databinding.ItemRealtimeTitleBinding
import com.threedollar.presentation.utils.calculatePercentages
import com.threedollar.presentation.utils.getDeadlineString
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick

class PollListAdapter(private val choicePoll: (String, String) -> Unit, private val clickPoll: (PollItem) -> Unit) :
    PagingDataAdapter<PollItem, ViewHolder>(BaseDiffUtilCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 1) {
            getItem(position)?.let {
                (holder as PollListViewHolder).onBind(it, choicePoll, clickPoll)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 0) PollTitleViewHolder(ItemRealtimeTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else PollListViewHolder(ItemPollBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class PollListViewHolder(private val binding: ItemPollBinding) : ViewHolder(binding.root) {
    fun onBind(pollItem: PollItem, choicePoll: (String, String) -> Unit, clickPoll: (PollItem) -> Unit) {
        val context = binding.root.context
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
                if (first.choice.selectedByMe) context.getDrawable(R.drawable.rect_poll_selected_most)
                else context.getDrawable(R.drawable.rect_poll_default_most)
            } else {
                if (first.choice.selectedByMe) context.getDrawable(R.drawable.rect_poll_selected)
                else context.getDrawable(R.drawable.rect_poll_default)
            }
            val secondBack = if (secondVoteCount > firstVoteCount) {
                if (second.choice.selectedByMe) context.getDrawable(R.drawable.rect_poll_selected_most)
                else context.getDrawable(R.drawable.rect_poll_default_most)
            } else {
                if (second.choice.selectedByMe) context.getDrawable(R.drawable.rect_poll_selected)
                else context.getDrawable(R.drawable.rect_poll_default)
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
        binding.twPollComment.text = pollItem.meta.totalCommentsCount.toString()
        binding.twPollVote.text = context.getString(R.string.str_vote_count, pollItem.meta.totalParticipantsCount)
        binding.twPollEndDate.text = getDeadlineString(pollItem.poll.createdAt)

        binding.imgMedal.loadUrlImg(pollItem.pollWriter.medal.iconUrl)

        binding.llPollFirstChoice.onSingleClick {
            if (first.choice.selectedByMe) return@onSingleClick
            choicePoll(pollItem.poll.pollId, first.optionId)
        }
        binding.llPollSecondChoice.onSingleClick {
            if (second.choice.selectedByMe) return@onSingleClick
            choicePoll(pollItem.poll.pollId, second.optionId)
        }
        binding.root.onSingleClick { clickPoll(pollItem) }
    }
}

class PollTitleViewHolder(private val binding: ItemRealtimeTitleBinding) : ViewHolder(binding.root)