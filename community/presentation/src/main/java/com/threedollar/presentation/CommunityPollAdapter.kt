package com.threedollar.presentation

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.domain.data.AdvertisementModelV2
import com.threedollar.domain.data.PollItem
import com.threedollar.presentation.data.PollListData
import com.threedollar.presentation.databinding.ItemPollAdBinding
import com.threedollar.presentation.databinding.ItemPollBinding
import com.threedollar.presentation.utils.calculatePercentages
import com.threedollar.presentation.utils.getDeadlineString
import com.threedollar.presentation.utils.hasVotingPeriodEnded
import zion830.com.common.base.BaseDiffUtilCallback
import com.threedollar.common.R as CommonR
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick

class CommunityPollAdapter(
    private val choicePoll: (String, String) -> Unit,
    private val clickPoll: (PollItem) -> Unit,
    private val adClick: OnItemClickListener<AdvertisementModelV2>
) :
    ListAdapter<PollListData, ViewHolder>(BaseDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 0) CommunityPollAdViewHolder(ItemPollAdBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else CommunityPollViewHolder(ItemPollBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PollListData.Ad -> {
                (holder as CommunityPollAdViewHolder).onBind(item.advertisementModelV2, adClick)
            }

            is PollListData.Poll -> {
                (holder as CommunityPollViewHolder).onBind(item.pollItem, choicePoll, clickPoll)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PollListData.Ad -> 0
            is PollListData.Poll -> 1
        }
    }
}

class CommunityPollAdViewHolder(private val binding: ItemPollAdBinding) : ViewHolder(binding.root) {
    fun onBind(advertisementModelV2: AdvertisementModelV2, adClick: OnItemClickListener<AdvertisementModelV2>) {
        binding.clPollAd.onSingleClick {
            adClick.onClick(advertisementModelV2)
        }
        binding.imgAd.loadUrlImg(advertisementModelV2.image.url)
        binding.twAdSub.text = advertisementModelV2.subTitle.content
        binding.twAdSub.setTextColor(Color.parseColor(advertisementModelV2.subTitle.fontColor))
        binding.twAdTitle.text = advertisementModelV2.title.content
        binding.twAdTitle.setTextColor(Color.parseColor(advertisementModelV2.title.fontColor))
        binding.clPollAd.backgroundTintList = ColorStateList.valueOf(Color.parseColor(advertisementModelV2.background.color))
    }
}

class CommunityPollViewHolder(private val binding: ItemPollBinding) : ViewHolder(binding.root) {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    fun onBind(pollItem: PollItem, choicePoll: (String, String) -> Unit, clickPoll: (PollItem) -> Unit) {
        val context = binding.root.context
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
        binding.twPollVote.text = context.getString(CommonR.string.str_vote_count, pollItem.meta.totalParticipantsCount)
        binding.twPollEndDate.text = getDeadlineString(pollItem.poll.period.endDateTime)

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