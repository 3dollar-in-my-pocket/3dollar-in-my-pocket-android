package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.FeedbackModel
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckFeedbackDetailBinding
import zion830.com.common.base.BaseDiffUtilCallback

class BossFeedBackDetailAdapter : ListAdapter<FeedbackModel?, BossFeedBackDetailViewHolder>(BaseDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BossFeedBackDetailViewHolder {
        return BossFeedBackDetailViewHolder(
            ItemFoodTruckFeedbackDetailBinding.inflate(
                android.view.LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BossFeedBackDetailViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.setBar(getItem(position)!!, position)
    }

}

class BossFeedBackDetailViewHolder(private val binding: ItemFoodTruckFeedbackDetailBinding) : ViewHolder(binding.root) {
    fun bind(item: FeedbackModel) {
        binding.titleTextView.text = "${item.emoji} ${item.description}"
        binding.reviewCountTextView.text = binding.root.context.getString(R.string.str_feedback_count, item.count)
    }

    fun setBar(item: FeedbackModel, position: Int) {
        binding.guideline.setGuidelinePercent(item.ratio)
        if (position > 2 || item.count == 0) {
            binding.barBackView.setBackgroundResource(R.drawable.rect_gray10_radius8)
            binding.barView.setBackgroundResource(R.drawable.rect_gray30_radius6)
        } else {
            binding.barBackView.setBackgroundResource(R.drawable.rect_pink100_radius8)
            binding.barView.setBackgroundResource(R.drawable.rect_pink_radius6)
        }
    }
}