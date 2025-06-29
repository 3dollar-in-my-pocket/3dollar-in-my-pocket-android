package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckFeedbackBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class BossReviewSummitRecyclerAdapter(
    private val onClickAction: (FeedbackTypeResponse) -> Unit,
    private val selectedItems: MutableSet<String>
) : ListAdapter<FeedbackTypeResponse, BossReviewSummitRecyclerAdapter.BossReviewSummitViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BossReviewSummitViewHolder(ItemFoodTruckFeedbackBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: BossReviewSummitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class BossReviewSummitViewHolder(private val binding: ItemFoodTruckFeedbackBinding) :
        ViewHolder(binding.root) {
        fun bind(item: FeedbackTypeResponse) {
            val isSelected = selectedItems.contains(item.feedbackType)
            binding.reviewCheckBox.text = "${item.emoji} ${item.description}"
            binding.reviewCheckBox.isChecked = isSelected
            updateCheckBoxStyle(isSelected)

            binding.root.onSingleClick {
                val newCheckedState = !binding.reviewCheckBox.isChecked
                binding.reviewCheckBox.isChecked = newCheckedState
                updateCheckBoxStyle(newCheckedState)
                onClickAction(item)
            }
        }

        private fun updateCheckBoxStyle(isSelected: Boolean) {
            val context = binding.root.context
            if (isSelected) {
                binding.root.setBackgroundResource(R.drawable.rect_radius12_pink100_stroke_pink)
                binding.reviewCheckBox.typeface = Typeface.DEFAULT_BOLD
                binding.reviewCheckBox.setTextColor(context.getColor(R.color.pink))
            } else {
                binding.root.setBackgroundResource(R.drawable.rect_white_radius12_stroke_gray20)
                binding.reviewCheckBox.typeface = Typeface.DEFAULT
                binding.reviewCheckBox.setTextColor(context.getColor(R.color.gray95))
            }
        }
    }
}
