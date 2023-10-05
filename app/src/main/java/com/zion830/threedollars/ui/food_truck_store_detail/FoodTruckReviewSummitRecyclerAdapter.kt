package com.zion830.threedollars.ui.food_truck_store_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckReviewBinding
import zion830.com.common.base.BaseDiffUtilCallback

class FoodTruckReviewSummitRecyclerAdapter(private val onClickAction: (FeedbackTypeResponse) -> Unit) :
    ListAdapter<FeedbackTypeResponse, FoodTruckReviewSummitRecyclerAdapter.FoodTruckReviewSummitViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FoodTruckReviewSummitViewHolder(ItemFoodTruckReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: FoodTruckReviewSummitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class FoodTruckReviewSummitViewHolder(private val binding: ItemFoodTruckReviewBinding) :
        ViewHolder(binding.root) {
        fun bind(item: FeedbackTypeResponse) {
            binding.reviewCheckBox.text = "${item.emoji} ${item.description}"
            binding.reviewCheckBox.setOnClickListener {
                binding.reviewCheckBox.setBackgroundResource(if (binding.reviewCheckBox.isChecked) R.drawable.rect_radius12_pink100_stroke_pink else R.drawable.rect_white_radius12_stroke_gray20)
                binding.reviewCheckBox.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.selector_type_radio_review, 0)
                binding.checkImageView.isVisible = binding.reviewCheckBox.isChecked
                onClickAction(item)
            }
        }
    }
}
