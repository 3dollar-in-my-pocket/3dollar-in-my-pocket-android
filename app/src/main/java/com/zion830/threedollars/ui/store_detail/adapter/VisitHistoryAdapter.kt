package com.zion830.threedollars.ui.store_detail.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemVisitHistoryBinding
import com.zion830.threedollars.repository.model.v2.response.store.DetailVisitHistory
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder

class VisitHistoryAdapter : BaseRecyclerView<ItemVisitHistoryBinding, DetailVisitHistory>(R.layout.item_visit_history) {

    override fun onBindViewHolder(holder: BaseViewHolder<ItemVisitHistoryBinding, DetailVisitHistory>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.tvCreatedAt.text = StringUtils.getTimeString(getItem(position).createdAt)
    }
}