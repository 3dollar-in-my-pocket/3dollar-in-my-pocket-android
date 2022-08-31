package com.zion830.threedollars.ui.food_truck_store_detail

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckReviewBinding
import com.zion830.threedollars.repository.model.v2.response.store.BossStoreFeedbackTypeResponse
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class FoodTruckReviewSummitRecyclerAdapter(private val onClickAction: (BossStoreFeedbackTypeResponse.BossStoreFeedbackTypeModel) -> Unit) :
    ListAdapter<BossStoreFeedbackTypeResponse.BossStoreFeedbackTypeModel, FoodTruckReviewSummitRecyclerAdapter.FoodTruckReviewSummitViewHolder>(
        BaseDiffUtilCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FoodTruckReviewSummitViewHolder(parent)

    override fun onBindViewHolder(holder: FoodTruckReviewSummitViewHolder, position: Int) {
        holder.bind(getItem(position), listener = null)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class FoodTruckReviewSummitViewHolder(parent: ViewGroup) :
        BaseViewHolder<ItemFoodTruckReviewBinding, BossStoreFeedbackTypeResponse.BossStoreFeedbackTypeModel>(
            R.layout.item_food_truck_review,
            parent
        ) {
        @SuppressLint("Range", "SetTextI18n")
        override fun bind(
            item: BossStoreFeedbackTypeResponse.BossStoreFeedbackTypeModel,
            listener: OnItemClickListener<BossStoreFeedbackTypeResponse.BossStoreFeedbackTypeModel>?
        ) {
            super.bind(item, listener)
            binding.reviewCheckBox.text = "${item.emoji} ${item.description}"
            binding.reviewCheckBox.setOnClickListener {
                binding.reviewCheckBox.setBackgroundResource(if (binding.reviewCheckBox.isChecked) R.drawable.rect_green_stroke_green else R.drawable.rect_white_stroke_gray)
                onClickAction(item)
            }
        }
    }
}
