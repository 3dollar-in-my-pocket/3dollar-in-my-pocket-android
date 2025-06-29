package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.FoodTruckReviewModel
import com.zion830.threedollars.databinding.ItemBossReviewNewBinding
import zion830.com.common.base.BaseDiffUtilCallback


class BossReviewRecyclerAdapter : ListAdapter<FoodTruckReviewModel, BossReviewViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BossReviewViewHolder(
        ItemBossReviewNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: BossReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class BossReviewViewHolder(private val binding: ItemBossReviewNewBinding) : ViewHolder(binding.root) {
    fun bind(item: FoodTruckReviewModel) {
        binding.apply {

        }
    }
}
