package com.zion830.threedollars.ui.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.threedollar.network.data.user.MyFeedbacksData
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemBossReviewBinding
import com.zion830.threedollars.databinding.ItemMyBossReviewBinding
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder

class MyBossReviewRecyclerAdapter(
    private val feedBacks: List<FeedbackTypeResponse>,
    private val listener: OnItemClickListener<MyFeedbacksData>,
) : PagingDataAdapter<MyFeedbacksData, BaseViewHolder<ItemMyBossReviewBinding, MyFeedbacksData>>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyBossReviewBinding, MyFeedbacksData>(R.layout.item_my_boss_review, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyBossReviewBinding, MyFeedbacksData>, position: Int) {
        val item = getItem(position) ?: MyFeedbacksData()
        holder.bind(item, if (item.store.isDeleted == false) listener else null)
        val previousItem = if (position > 0) getItem(position - 1) else null
        holder.binding.apply {
            val feedbacks = item.feedbacks.orEmpty()
            tvCreatedAt.text = StringUtils.getTimeString(item.date, "yyyy.MM.dd")

            val titleVisibility = !(position > 0 && previousItem?.store?.storeId == item.store.storeId)
            tvStoreName.isVisible = titleVisibility
            ivCategory.isVisible = titleVisibility
            tvCount.text = "${feedbacks.size}개"
            feedbacks.forEach { feedbackKey ->
                val bossReview = ItemBossReviewBinding.inflate(LayoutInflater.from(llBoosReviews.context), null, false)
                val feedBack = feedBacks.find { it.feedbackType == feedbackKey.feedbackType }
                bossReview.tvReview.text = "${feedBack?.emoji} ${feedBack?.description}"
                llBoosReviews.addView(bossReview.root)
            }
        }
    }
}