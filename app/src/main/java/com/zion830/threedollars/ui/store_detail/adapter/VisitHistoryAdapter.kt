package com.zion830.threedollars.ui.store_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.home.domain.data.store.BossStoreDetailItem
import com.home.domain.data.store.HistoriesContentModel
import com.home.domain.data.store.MenuModel
import com.threedollar.common.ext.convertCreatedAt
import com.threedollar.common.ext.toFormattedNumber
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckMenuBinding
import com.zion830.threedollars.databinding.ItemFoodTruckMenuEmptyBinding
import com.zion830.threedollars.databinding.ItemFoodTruckMenuMoreBinding
import com.zion830.threedollars.databinding.ItemVisitHistoryBinding
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuMoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.DetailVisitHistory
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder

class VisitHistoryAdapter : ListAdapter<HistoriesContentModel, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VisitHistoryViewHolder(ItemVisitHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VisitHistoryViewHolder -> {
                holder.bind(getItem(position) as HistoriesContentModel)
            }
        }
    }
}

class VisitHistoryViewHolder(private val binding: ItemVisitHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: HistoriesContentModel) {
        binding.createdAtTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (item.visit.type == "EXISTS") R.drawable.circle_green_4dp else R.drawable.circle_red_4dp,
            0,
            0,
            0
        )
        binding.createdAtTextView.text = item.visit.createdAt.convertCreatedAt()
        binding.userNameTextView.text = item.visitor.name
    }
}