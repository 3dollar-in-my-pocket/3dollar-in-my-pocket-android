package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.home.domain.data.store.FeedbackModel
import com.home.domain.data.store.FeedbackType
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFeedbackTagBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

class FeedbackRecyclerAdapter(private val onFeedBackMoreClick: OnItemClickListener<Unit>) :
    ListAdapter<FeedbackModel, FeedbackViewHolder>(BaseDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder =
        FeedbackViewHolder(ItemFeedbackTagBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder.bind(getItem(position), onFeedBackMoreClick)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class FeedbackViewHolder(private val binding: ItemFeedbackTagBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FeedbackModel, onFeedBackMoreClick: OnItemClickListener<Unit>) {
        binding.feedbackCount.isGone = item.feedbackType == FeedbackType.MORE
        binding.feedbackArrow.isGone = item.feedbackType != FeedbackType.MORE
        if (item.feedbackType == FeedbackType.MORE) {
            binding.feedbackMessage.text = binding.root.context.getString(CommonR.string.all_more)
            binding.root.onSingleClick { onFeedBackMoreClick.onClick(Unit) }
        } else {
            binding.feedbackMessage.text = "${item.emoji} ${item.description}"
            binding.feedbackCount.text = binding.root.context.getString(CommonR.string.food_truck_review_count, item.count)
            binding.root.onSingleClick { }
        }

    }
}