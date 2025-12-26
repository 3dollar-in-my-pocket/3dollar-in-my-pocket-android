package com.zion830.threedollars.ui.storeDetail.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.domain.home.data.store.HistoriesContentModel
import com.threedollar.common.ext.convertCreatedAt
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.databinding.ItemVisitHistoryBinding
import zion830.com.common.base.BaseDiffUtilCallback

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
            if (item.visit.type == "EXISTS") DesignSystemR.drawable.circle_green_4dp else DesignSystemR.drawable.circle_red_4dp,
            0,
            0,
            0
        )
        binding.createdAtTextView.text = item.visit.createdAt.convertCreatedAt()
        binding.userNameTextView.text = item.visitor.name
    }
}