package com.zion830.threedollars.ui.food_truck_store_detail

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.AppearanceDayModel
import com.home.domain.data.store.DayOfTheWeekType
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemAppearanceDayBinding
import zion830.com.common.base.BaseDiffUtilCallback

class AppearanceDayRecyclerAdapter : ListAdapter<AppearanceDayModel, AppearanceDayViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AppearanceDayViewHolder(ItemAppearanceDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: AppearanceDayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = position
}

class AppearanceDayViewHolder(private val binding: ItemAppearanceDayBinding) : ViewHolder(binding.root) {
    fun bind(item: AppearanceDayModel) {
        binding.dayTextView.text = item.dayOfTheWeek.dayString
        if (item.openingHoursModel == null) {
            binding.timeTextView.text = "휴무"
            binding.timeTextView.setTextColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.gray50))
        } else {
            binding.timeTextView.text = item.openingHoursModel!!.toConvert()
            binding.timeTextView.setTextColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.gray70))
        }
        binding.locationTextView.text = item.locationDescription
        if (item.dayOfTheWeek == DayOfTheWeekType.SUNDAY) {
            binding.view.isVisible = false
        }
    }
}
