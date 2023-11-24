package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.FoodTruckReviewModel
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckReviewDetailBinding
import zion830.com.common.base.BaseDiffUtilCallback


class BossReviewRecyclerAdapter : ListAdapter<FoodTruckReviewModel, BossReviewViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BossReviewViewHolder(
        ItemFoodTruckReviewDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: BossReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.setBar(getItem(position), position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class BossReviewViewHolder(private val binding: ItemFoodTruckReviewDetailBinding) : ViewHolder(binding.root) {
    fun bind(item: FoodTruckReviewModel) {
        binding.titleTextView.text = "${item.emoji}  ${item.description}"
        binding.reviewCountTextView.text = GlobalApplication.getContext().getString(R.string.food_truck_review_count,item.count)
    }

    fun setBar(item: FoodTruckReviewModel, position: Int) {
        binding.guideline.setGuidelinePercent(item.ratio.toFloat())

        if (position > 2 || item.count == 0) {
            binding.barBackView.setBackgroundResource(R.drawable.rect_gray10_radius8)
            binding.barView.setBackgroundResource(R.drawable.rect_gray30_radius6)
        } else {
            binding.barBackView.setBackgroundResource(R.drawable.rect_pink100_radius8)
            binding.barView.setBackgroundResource(R.drawable.rect_pink_radius6)
        }
    }
}
