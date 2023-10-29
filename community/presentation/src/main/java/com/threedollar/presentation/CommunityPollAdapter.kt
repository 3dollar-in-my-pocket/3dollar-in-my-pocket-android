package com.threedollar.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.data.PollItem
import com.threedollar.presentation.databinding.ItemPollBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class CommunityPollAdapter(private val choicePoll: (String, String) -> Unit, private val clickPoll: () -> Unit) :
    ListAdapter<PollItem, CommunityPollViewHolder>(BaseDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityPollViewHolder {
        return CommunityPollViewHolder(ItemPollBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CommunityPollViewHolder, position: Int) {
        holder.onBind(getItem(position), choicePoll, clickPoll)
    }


}

class CommunityPollViewHolder(private val binding: ItemPollBinding) : ViewHolder(binding.root) {
    fun onBind(pollItem: PollItem, choicePoll: (String, String) -> Unit, clickPoll: () -> Unit) {
        val context = binding.root.context
        val first = pollItem.poll.options[0]
        val second = pollItem.poll.options[1]
        binding.twPollTitle.text = pollItem.poll.content.title
        binding.twPollNickName.text = pollItem.pollWriter.name
        binding.twMedalName.text = pollItem.pollWriter.medal.name
        binding.twPollComment.text = pollItem.meta.totalCommentsCount.toString()
        binding.twPollVote.text = context.getString(R.string.str_vote_count, pollItem.meta.totalParticipantsCount)
        binding.twPollEndDate.text = pollItem.poll.createdAt
        binding.twPollFirstChoice.text = first.name
        binding.twPollSecondChoice.text = second.name
        binding.twPollFirstChoice.onSingleClick { choicePoll(pollItem.poll.pollId, first.optionId) }
        binding.twPollSecondChoice.onSingleClick { choicePoll(pollItem.poll.pollId, second.optionId) }
        binding.twPollFirstChoice.isSelected = first.choice.selectedByMe
        binding.twPollSecondChoice.isSelected = second.choice.selectedByMe
        binding.twPollFirstChoice.backgroundTintList =
            if (first.choice.count < second.choice.count) context.getColorStateList(R.color.white) else context.getColorStateList(R.color.black)
        binding.twPollSecondChoice.backgroundTintList =
            if (first.choice.count > second.choice.count) context.getColorStateList(R.color.white) else context.getColorStateList(R.color.black)
    }
}