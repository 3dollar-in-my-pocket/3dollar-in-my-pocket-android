package com.zion830.threedollars.ui.community

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.domain.community.data.AdvertisementModelV2
import com.threedollar.domain.community.data.PollItem
import com.zion830.threedollars.databinding.ItemPollAdBinding
import com.zion830.threedollars.databinding.ItemPollAdmobBinding
import com.zion830.threedollars.databinding.ItemPollBinding
import com.zion830.threedollars.ui.community.data.PollListData
import com.zion830.threedollars.ui.community.utils.calculatePercentages
import com.zion830.threedollars.ui.community.utils.getDeadlineString
import com.zion830.threedollars.ui.community.utils.hasVotingPeriodEnded
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

class CommunityPollAdapter(
    private val choicePoll: (String, String) -> Unit,
    private val clickPoll: (PollItem) -> Unit,
    private val adClick: OnItemClickListener<AdvertisementModelV2>
) :
    ListAdapter<PollListData, ViewHolder>(BaseDiffUtilCallback()) {
    private val adMobViewHolders = mutableSetOf<CommunityPollAdMobViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_AD -> CommunityPollAdViewHolder(ItemPollAdBinding.inflate(inflater, parent, false))
            VIEW_TYPE_ADMOB -> CommunityPollAdMobViewHolder(ItemPollAdmobBinding.inflate(inflater, parent, false)).also { adMobViewHolders.add(it) }
            else -> CommunityPollViewHolder(ItemPollBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PollListData.Ad -> {
                (holder as CommunityPollAdViewHolder).onBind(item.advertisementModelV2, adClick)
            }

            is PollListData.Poll -> {
                (holder as CommunityPollViewHolder).onBind(item.pollItem, choicePoll, clickPoll)
            }

            PollListData.AdMob -> {
                (holder as CommunityPollAdMobViewHolder).onBind()
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PollListData.Ad -> VIEW_TYPE_AD
            is PollListData.Poll -> VIEW_TYPE_POLL
            PollListData.AdMob -> VIEW_TYPE_ADMOB
        }
    }

    /** 화면이 사라질 때 호출해 로드한 AdMob 배너를 해제한다. */
    fun releaseAdMob() {
        adMobViewHolders.forEach { it.release() }
        adMobViewHolders.clear()
    }

    private companion object {
        const val VIEW_TYPE_AD = 0
        const val VIEW_TYPE_POLL = 1
        const val VIEW_TYPE_ADMOB = 2
    }
}

/** 서버 광고가 없을 때 투표 카드 자리에 AdMob 배너를 채운다. 한 번 요청한 배너는 재바인딩돼도 다시 요청하지 않는다. */
class CommunityPollAdMobViewHolder(private val binding: ItemPollAdmobBinding) : ViewHolder(binding.root) {
    private var adView: AdView? = null

    fun onBind() {
        if (adView != null) return
        val context = binding.root.context
        adView = AdView(context).apply {
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, CARD_WIDTH_DP))
            adUnitId = context.getString(CommonR.string.admob_poll_list_card)
            loadAd(AdRequest.Builder().build())
        }.also {
            binding.flPollAdMob.addView(
                it,
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER),
            )
        }
    }

    fun release() {
        adView?.destroy()
        binding.flPollAdMob.removeAllViews()
        adView = null
    }

    private companion object {
        /** `item_poll_admob.xml` 카드 폭과 같다. */
        const val CARD_WIDTH_DP = 280
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
                if (first.choice.selectedByMe) context.getDrawable(DesignSystemR.drawable.rect_poll_selected_most)
                else context.getDrawable(DesignSystemR.drawable.rect_poll_default_most)
            } else {
                if (first.choice.selectedByMe) context.getDrawable(DesignSystemR.drawable.rect_poll_selected)
                else context.getDrawable(DesignSystemR.drawable.rect_poll_default)
            }
            val secondBack = if (secondVoteCount > firstVoteCount) {
                if (second.choice.selectedByMe) context.getDrawable(DesignSystemR.drawable.rect_poll_selected_most)
                else context.getDrawable(DesignSystemR.drawable.rect_poll_default_most)
            } else {
                if (second.choice.selectedByMe) context.getDrawable(DesignSystemR.drawable.rect_poll_selected)
                else context.getDrawable(DesignSystemR.drawable.rect_poll_default)
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