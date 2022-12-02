package com.zion830.threedollars.ui.food_truck_store_detail

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemAppearanceDayBinding
import com.zion830.threedollars.datasource.model.v2.response.store.AppearanceDayModel
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class AppearanceDayRecyclerAdapter : ListAdapter<AppearanceDayModel, AppearanceDayViewHolder>(
    BaseDiffUtilCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AppearanceDayViewHolder(parent)

    override fun onBindViewHolder(holder: AppearanceDayViewHolder, position: Int) {
        holder.bind(getItem(position), listener = null)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class AppearanceDayViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemAppearanceDayBinding, AppearanceDayModel>(
        R.layout.item_appearance_day,
        parent
    ) {
    @SuppressLint("Range")
    override fun bind(
        item: AppearanceDayModel,
        listener: OnItemClickListener<AppearanceDayModel>?
    ) {
        super.bind(item, listener)
        if (item.openingHours == "휴무") {
            binding.timeTextView.setTypeface(binding.timeTextView.typeface, Typeface.BOLD)
            binding.timeTextView.setTextColor(
                ContextCompat.getColor(
                    GlobalApplication.getContext(),
                    R.color.color_FF5C43
                )
            )
        }
        if (item.dayOfTheWeek == "일요일") {
            binding.view1.isVisible = false
        }
    }
}
