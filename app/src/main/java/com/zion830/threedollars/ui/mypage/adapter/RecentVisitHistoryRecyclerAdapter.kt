package com.zion830.threedollars.ui.mypage.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemRecentVisitHistoryBinding
import com.zion830.threedollars.repository.model.v2.response.visit_history.VisitHistoryContent
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder

class RecentVisitHistoryRecyclerAdapter(
    private val onClick: (Int) -> Unit
) : BaseRecyclerView<ItemRecentVisitHistoryBinding, VisitHistoryContent>(R.layout.item_recent_visit_history) {

    override fun onBindViewHolder(holder: BaseViewHolder<ItemRecentVisitHistoryBinding, VisitHistoryContent>, position: Int) {
        val item = getItem(position)
        super.onBindViewHolder(holder, position)

        holder.binding.apply {
            tvCreatedAt.text = StringUtils.getTimeString(item?.createdAt)
            ivCategory.bindMenuIcons(item?.store?.categories)
            val categoryInfo = SharedPrefUtils.getCategories()
            val categories = item?.store?.categories?.joinToString(" ") {
                "#${categoryInfo.find { categoryInfo -> categoryInfo.category == it }?.name}"
            }
            tvCategories.text = categories
            layoutItem.setOnClickListener {
                if (!item.store.isDeleted) {
                    onClick(item.store.storeId)
                }
            }
        }
    }
}