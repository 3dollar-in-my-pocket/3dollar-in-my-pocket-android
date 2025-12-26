package com.zion830.threedollars.ui.community.poll

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.community.data.PollComment
import com.zion830.threedollars.databinding.ItemPollCommentBinding
import com.zion830.threedollars.databinding.ItemPollCommentBlindBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PollCommentAdapter(private val optionClick: (PollComment) -> Unit) :
    ListAdapter<PollComment, ViewHolder>(BaseDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) PollCommentBlindViewHolder(ItemPollCommentBlindBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else PollCommentViewHolder(ItemPollCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is PollCommentViewHolder -> {
                holder.onBind(getItem(position), optionClick)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).current.commentReport.reportedByMe) 1 else 0
    }
}

class PollCommentViewHolder(private val binding: ItemPollCommentBinding) : ViewHolder(binding.root) {
    fun onBind(pollComment: PollComment, commentClick: (PollComment) -> Unit) {
        binding.twPollCommentOption.onSingleClick { commentClick(pollComment) }
        binding.clCommentBack.isSelected = pollComment.current.comment.isOwner
        binding.twPollCommentOption.text = if (pollComment.current.comment.isOwner) "수정" else "신고"
        binding.twMedalName.text = pollComment.current.commentWriter.medal.name
        binding.imgMedal.loadUrlImg(pollComment.current.commentWriter.medal.iconUrl)
        binding.twPollCommentChoice.isVisible = pollComment.current.poll.selectedOptions.isNotEmpty()
        if (binding.twPollCommentChoice.isVisible) binding.twPollCommentChoice.text = pollComment.current.poll.selectedOptions.first().name
        binding.twPollCommentDate.text = getTimeAgo(pollComment.current.comment.createdAt)
        binding.twPollCommentNick.text = pollComment.current.commentWriter.name
        binding.twPollCommentWriter.isVisible = pollComment.current.poll.isWriter
        binding.twPollCommentContent.text = pollComment.current.comment.content
    }

    private fun getTimeAgo(timeString: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        val parsedDate: Date
        try {
            parsedDate = formatter.parse(timeString) ?: return ""
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

        val now = Calendar.getInstance().time
        val difference = now.time - parsedDate.time

        val seconds = difference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        return when {
            hours < 1 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            else -> SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(parsedDate)
        }
    }

}

class PollCommentBlindViewHolder(private val binding: ItemPollCommentBlindBinding) : ViewHolder(binding.root)