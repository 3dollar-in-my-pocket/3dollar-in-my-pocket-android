package com.zion830.threedollars.ui.food_truck_store_detail

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckReviewDetailBinding
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreFeedbackFullResponse
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder


class FoodTruckReviewRecyclerAdapter :
    ListAdapter<BossStoreFeedbackFullResponse.FoodTruckReviewModel, FoodTruckReviewViewHolder>(
        BaseDiffUtilCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FoodTruckReviewViewHolder(parent)

    override fun onBindViewHolder(holder: FoodTruckReviewViewHolder, position: Int) {
        holder.bind(getItem(position), listener = null)
        holder.setBar(getItem(position), position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class FoodTruckReviewViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemFoodTruckReviewDetailBinding, BossStoreFeedbackFullResponse.FoodTruckReviewModel>(
        R.layout.item_food_truck_review_detail,
        parent
    ) {
    @SuppressLint("Range", "SetTextI18n")
    override fun bind(
        item: BossStoreFeedbackFullResponse.FoodTruckReviewModel,
        listener: OnItemClickListener<BossStoreFeedbackFullResponse.FoodTruckReviewModel>?
    ) {
        super.bind(item, listener)
        binding.titleTextView.text = "${item.emoji}  ${item.description}"
    }

    fun setBar(
        item: BossStoreFeedbackFullResponse.FoodTruckReviewModel,
        position: Int
    ) {
        binding.guideline.setGuidelinePercent(item.ratio.toFloat())

        if (position > 2 || item.count == 0) {
            binding.barBackView.setBackgroundResource(R.drawable.rect_gray_radius16)
            binding.barView.setBackgroundResource(R.drawable.rect_gray10_radius16)
        } else {
            binding.barBackView.setBackgroundResource(R.drawable.rect_green_radius16_opa10)
            binding.barView.setBackgroundResource(R.drawable.rect_green_radius16)
        }
    }
}
